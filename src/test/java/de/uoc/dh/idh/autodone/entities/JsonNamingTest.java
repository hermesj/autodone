package de.uoc.dh.idh.autodone.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Mastodon answers in snake_case; the entities rely on {@code @JsonNaming} being
 * honoured by the JSON converter a plain {@link RestTemplate} picks (Jackson 3
 * since Spring Boot 4).
 */
class JsonNamingTest {

	@Test()
	void readsSnakeCaseAndIgnoresUnknownProperties() {
		var rest = new RestTemplate();
		var server = MockRestServiceServer.bindTo(rest).build();

		server.expect(requestTo("https://mastodon.example/api/v1/apps")).andRespond(withSuccess("""
				{"id": "1", "name": "autodone", "vapid_key": "x",
				 "client_id": "the-id", "client_secret": "the-secret"}
				""", APPLICATION_JSON));

		var entity = rest.getForObject("https://mastodon.example/api/v1/apps", ServerEntity.class);

		assertEquals("the-id", entity.clientId);
		assertEquals("the-secret", entity.clientSecret);
		server.verify();
	}

}
