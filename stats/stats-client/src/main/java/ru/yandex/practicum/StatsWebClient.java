/* package ru.yandex.practicum;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Qualifier("asyncClient")
public class StatsWebClient implements StatsClient {
    private final WebClient webClient;

    public StatsWebClient() {
        this.webClient = WebClient.create("http://localhost:9090");
    }

    @Override
    public void sendRequest() {
        try {
            nonblock();
        } catch (Exception ex) {
            log.error("error", ex);
        }
    }

    public void nonblock() throws InterruptedException {
        log.info("sending request...");
        Mono<String> monoResponse = webClient
                .get()
                .uri("/test")
                .retrieve()
                .bodyToMono(String.class);
        monoResponse.subscribe(s -> log.info("got response: " + s));
        log.info("sleeping...");
        Thread.sleep(5000);
        log.info("completed");
    }
}

 */
