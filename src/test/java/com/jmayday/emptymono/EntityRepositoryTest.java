package com.jmayday.emptymono;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options.ChunkedEncodingPolicy;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;


class EntityRepositoryTest {

  private static EntityRepository service;
  private static WireMockServer server;

  @BeforeAll
  static void setUp() {

    server = new WireMockServer(options()
        .dynamicPort()
        .useChunkedTransferEncoding(ChunkedEncodingPolicy.BODY_FILE)
        .usingFilesUnderDirectory("src/test/resources/entities")
    );
    server.start();

    WebClient webClient = WebClient
        .builder()
        .baseUrl(server.baseUrl())
        .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
        .build();

    service = new EntityRepository(webClient,
        "/entities/{id}");
  }

  @Test
  void shouldReturnEmptyObjectFor404WithResponseBody() {
    // uncomment dependency management in pom to make it working
    assertThat(service.getEntity(12345).blockOptional())
        .isEmpty();
  }

  @Test
  void shouldReturnEmptyObjectFor404WithoutResponseBody() {
    assertThat(service.getEntity(333).blockOptional())
        .isEmpty();
  }

  @Test
  void shouldFindEntity() {
    Optional<MyEntity> bankAccount = service.getEntity(4)
        .blockOptional();

    assertThat(bankAccount).hasValueSatisfying(v -> {
      assertSoftly(s -> {
        s.assertThat(v.getId()).isEqualTo(4);
        s.assertThat(v.getName()).describedAs("name").isEqualTo("Name");
        s.assertThat(v.getOwner()).describedAs("owner").isEqualTo("Owner");
      });
    });

  }

}