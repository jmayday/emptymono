package com.jmayday.emptymono;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static reactor.core.publisher.Mono.empty;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EntityRepository {

  private final String path;
  private final WebClient webclient;

  @Autowired
  public EntityRepository(WebClient webClient,
      @Value("${entity.path}") String path) {
    this.webclient = webClient;
    this.path = path;
  }

  public Mono<MyEntity> getEntity(int id) {

    return webclient
        .get()
        .uri(path, Map.of("id", id))

        .retrieve()
        .onStatus(NOT_FOUND::equals, clientResponse -> empty())
        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono
            .error(new RuntimeException(
                format("Cannot get entity. Service response: %s",
                    clientResponse.statusCode().getReasonPhrase()))))
        .bodyToMono(MyEntity.class);
  }

}
