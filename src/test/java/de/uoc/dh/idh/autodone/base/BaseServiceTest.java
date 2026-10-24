package de.uoc.dh.idh.autodone.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.server.ResponseStatusException;

class BaseServiceTest {

	private static final UUID KNOWN = UUID.randomUUID();

	private final OAuth2AuthenticationToken oauth = new OAuth2AuthenticationToken(
			new DefaultOAuth2User(List.of(), Map.of("username", "autodone"), "username"), List.of(), "mastodon.social");

	// only finds KNOWN for its owner, like the repositories
	private final BaseService<String> service = new BaseService<>() {

		@Override()
		public void delete(UUID uuid, String username, String domain) {
		}

		@Override()
		public String getOne(UUID uuid, String username, String domain) {
			return KNOWN.equals(uuid) && "autodone".equals(username) ? "entity" : null;
		}

		@Override()
		public Iterable<String> getOwn(String username, String domain) {
			return List.of();
		}

		@Override()
		public Page<String> getPage(Pageable request, String username, String domain) {
			return Page.empty();
		}

		@Override()
		protected Sort getSort() {
			return Sort.unsorted();
		}

	};

	@Test()
	void returnsOwnEntity() {
		assertEquals("entity", service.getOne(KNOWN.toString(), oauth));
	}

	@Test()
	void unknownOrForeignEntityIsNotFound() {
		assertNotFound(() -> service.getOne(UUID.randomUUID().toString(), oauth));
	}

	@Test()
	void malformedUuidIsNotFound() {
		assertNotFound(() -> service.getOne("not-a-uuid", oauth));
		assertNotFound(() -> service.getOne(null, oauth));
		assertNotFound(() -> service.delete("not-a-uuid", oauth));
	}

	//

	private static void assertNotFound(Runnable runnable) {
		var exception = assertThrows(ResponseStatusException.class, runnable::run);
		assertEquals(NOT_FOUND, exception.getStatusCode());
	}

}
