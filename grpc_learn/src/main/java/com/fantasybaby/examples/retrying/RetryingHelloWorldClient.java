/*
 * Copyright 2020 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fantasybaby.examples.retrying;

import com.fantasybaby.examples.helloworld.GreeterGrpc;
import com.fantasybaby.examples.helloworld.HelloReply;
import com.fantasybaby.examples.helloworld.HelloRequest;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A client that requests a greeting from the {@link RetryingHelloWorldServer} with a retrying policy.
 */
@Slf4j
public class RetryingHelloWorldClient {
  static final String ENV_DISABLE_RETRYING = "DISABLE_RETRYING_IN_RETRYING_EXAMPLE";


  private final boolean enableRetries;
  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;
  private final AtomicInteger totalRpcs = new AtomicInteger();
  private final AtomicInteger failedRpcs = new AtomicInteger();

  protected Map<String, ?> getRetryingServiceConfig() {
    return new Gson()
            .fromJson(
                    new JsonReader(
                            new InputStreamReader(
                                    RetryingHelloWorldClient.class.getResourceAsStream(
                                            "/retrying/retrying_service_config.json"),
                                    UTF_8)),
                    Map.class);
  }

  /**
   * Construct client connecting to HelloWorld server at {@code host:port}.
   */
  public RetryingHelloWorldClient(String host, int port, boolean enableRetries) {

    ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext();
    if (enableRetries) {
      Map<String, ?> serviceConfig = getRetryingServiceConfig();
      log.info("Client started with retrying configuration: " + serviceConfig);
      /**
       * 失败之后自己尝试,
       */
      channelBuilder.defaultServiceConfig(serviceConfig).enableRetry();
    }
    channel = channelBuilder.build();
    blockingStub = GreeterGrpc.newBlockingStub(channel);
    this.enableRetries = enableRetries;
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
  }

  /**
   * Say hello to server in a blocking unary call.
   */
  public void greet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response = null;
    StatusRuntimeException statusRuntimeException = null;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      failedRpcs.incrementAndGet();
      statusRuntimeException = e;
    }

    totalRpcs.incrementAndGet();

    if (statusRuntimeException == null) {
      log.info("Greeting: {0}", new Object[]{response.getMessage()});
    } else {
      log.info("RPC failed: {0}", new Object[]{statusRuntimeException.getStatus()});
    }
  }

  private void printSummary() {
    log.info(
            "\n\nTotal RPCs sent: {}. Total RPCs failed: {}\n",
            new Object[]{
                    totalRpcs.get(), failedRpcs.get()});

    if (enableRetries) {
      log.info(
              
              "Retrying enabled. To disable retries, run the client with environment variable {}=true.",
              ENV_DISABLE_RETRYING);
    } else {
      log.info(
              
              "Retrying disabled. To enable retries, unset environment variable {} and then run the client.",
              ENV_DISABLE_RETRYING);
    }
  }

  public static void main(String[] args) throws Exception {
    boolean enableRetries = !Boolean.parseBoolean(System.getenv(ENV_DISABLE_RETRYING));
    final RetryingHelloWorldClient client = new RetryingHelloWorldClient("localhost", 50051, enableRetries);
    ForkJoinPool executor = new ForkJoinPool();

    for (int i = 0; i < 50; i++) {
      final String userId = "user" + i;
      executor.execute(
              () -> client.greet(userId));
    }
    executor.awaitQuiescence(100, TimeUnit.SECONDS);
    executor.shutdown();
    client.printSummary();
    client.shutdown();
  }
}
