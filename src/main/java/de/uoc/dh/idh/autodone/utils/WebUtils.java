package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.SCHEME;
import static java.util.List.of;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.uoc.dh.idh.autodone.entities.TokenEntity;

@Component()
public class WebUtils<T> {

	public static Href href() {
		return new Href(fromCurrentRequest());
	}

	public static Href href(UriComponentsBuilder uri) {
		return new Href(uri);
	}

	//

	public static Href localHref() {
		return new Href(fromCurrentContextPath());
	}

	public static Href localHref(String path) {
		return new Href(fromCurrentContextPath().path(path));
	}

	//

	public static Href remoteHref(String domain) {
		return new Href(newInstance().host(domain));
	}

	public static Href remoteHref(String domain, String path) {
		return new Href(fromPath(path).host(domain));
	}

	//

	@SuppressWarnings("all")
	public static Request<Map> request() {
		return request(Map.class);
	}

	public static <T> Request<T> request(Class<T> type) {
		return new Request<>(type);
	}

	//

	public static class Href {

		private UriComponentsBuilder uri;

		//

		private Href(UriComponentsBuilder uri) {
			this.uri = uri.scheme(SCHEME);
		}

		//

		@Override()
		public String toString() {
			return toString(new Object[] {});
		}

		public String toString(Object... params) {
			return uri.buildAndExpand(params).toString();
		}

	}

	//

	public static class Request<T> {

		private final HttpHeaders head;

		private final RestTemplate rest;

		private final Class<T> type;

		//

		private Request(Class<T> type) {
			this.head = new HttpHeaders();
			this.rest = new RestTemplate();
			this.type = type;

			head.setAccept(of(APPLICATION_JSON));
			head.setContentType(APPLICATION_JSON);
		}

		//

		public Request<T> auth(TokenEntity token) {
			head.setBearerAuth(token.token);
			return this;
		}

		public Request<T> form() {
			head.setContentType(MULTIPART_FORM_DATA);
			return this;
		}

		//

		public T get(Href href) {
			return rest.exchange(href.toString(), GET, new HttpEntity<>(head), type).getBody();
		}

		public T post(Href href, Map<String, ?> body) {
			return rest.exchange(href.toString(), POST, new HttpEntity<>(body, head), type).getBody();
		}

	}

}
