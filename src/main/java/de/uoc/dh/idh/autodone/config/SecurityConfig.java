package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.AutodoneApplication.getEnvironment;
import static org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;
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

	public static final String OAUTH_AUTHORIZE = DEFAULT_LOGIN_PAGE_URL + "/auth/{domain}";

	public static final String OAUTH_REDIRECT = DEFAULT_LOGIN_PAGE_URL + "/code/{domain}";

	public static final String SCHEME = getEnvironment().matchesProfiles("test") ? "http" : "https";

	@Autowired()
	private MastodonRegistrationRepository registrationRepository;

	@Bean()
	public OAuth2AuthorizedClientService authorizedClientService(JdbcOperations operations) {
		return new JdbcOAuth2AuthorizedClientService(operations, registrationRepository);
	}

	@Bean()
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests((authorizeHttpRequests) -> {
			authorizeHttpRequests.requestMatchers("/").permitAll();
			authorizeHttpRequests.requestMatchers("/about").permitAll();
			authorizeHttpRequests.requestMatchers("/webjars/**").permitAll();
			authorizeHttpRequests.requestMatchers(DEFAULT_LOGIN_PAGE_URL).permitAll();

			if (getEnvironment().matchesProfiles("test")) {
				authorizeHttpRequests.anyRequest().permitAll();
			}
		});

		httpSecurity.oauth2Login((oauth2Login) -> {
			oauth2Login.clientRegistrationRepository(registrationRepository);
			oauth2Login.loginPage(DEFAULT_LOGIN_PAGE_URL);

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
