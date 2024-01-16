package de.uoc.dh.idh.autodone.config;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;

import de.uoc.dh.idh.autodone.repositories.MastodonRegistrationRepository;

@Configuration()
public class SecurityConfig {

	public static final String OAUTH_LOGIN = "/login";

	public static final String OAUTH_AUTHORIZE = OAUTH_LOGIN + "/auth/{registrationId}";

	public static final String OAUTH_REDIRECT = OAUTH_LOGIN + "/code/{registrationId}";

	@Autowired()
	private MastodonRegistrationRepository registrationRepository;

	@Bean()
	public OAuth2AuthorizedClientService authorizedClientService(JdbcOperations operations) {
		return new JdbcOAuth2AuthorizedClientService(operations, registrationRepository);
	}

	@Bean()
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests((authorizeHttpRequests) -> {
			authorizeHttpRequests.requestMatchers("/").permitAll();
			authorizeHttpRequests.requestMatchers("/about").permitAll();
			authorizeHttpRequests.requestMatchers("/webjars/**").permitAll();
			authorizeHttpRequests.requestMatchers(OAUTH_LOGIN).permitAll();
		});

		httpSecurity.oauth2Login((oauth2Login) -> {
			oauth2Login.clientRegistrationRepository(registrationRepository);
			oauth2Login.loginPage(OAUTH_LOGIN);

			oauth2Login.authorizationEndpoint((authorizationEndpoint) -> {
				authorizationEndpoint.baseUri(fromUriString(OAUTH_AUTHORIZE).buildAndExpand("").toString());
			});

			oauth2Login.redirectionEndpoint((redirectionEndpoint) -> {
				redirectionEndpoint.baseUri(fromUriString(OAUTH_REDIRECT).buildAndExpand("*").toString());
			});
		});

		return httpSecurity.build();
	}

}
