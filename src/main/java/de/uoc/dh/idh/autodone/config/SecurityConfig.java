package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.AutodoneApplication.getEnvironment;
import static de.uoc.dh.idh.autodone.config.ControllerConfig.paths;
import static org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;
import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_AUTHORIZATION_REQUEST_PATTERN;
import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;
import static org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL;
import static org.springframework.web.util.pattern.PathPatternParser.defaultInstance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;

import de.uoc.dh.idh.autodone.repositories.ClientRepository;
import de.uoc.dh.idh.autodone.services.TokenService;

@Configuration()
public class SecurityConfig {

	public static final String OAUTH_AUTHORIZE;

	public static final String OAUTH_REDIRECT;

	public static final String SCHEME;

	static {
		var rootPattern = defaultInstance.parse(DEFAULT_FILTER_PROCESSES_URI);
		var pathPattern = defaultInstance.parse("{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME + "}");

		OAUTH_AUTHORIZE = DEFAULT_AUTHORIZATION_REQUEST_PATTERN;
		OAUTH_REDIRECT = rootPattern.combine(pathPattern).toString();
		SCHEME = getEnvironment().matchesProfiles("test") ? "http" : "https";
	}

	//

	@Bean()
	public OAuth2AuthorizedClientService authorizedClientService(TokenService service) {
		return service;
	}

	//

	@Bean()
	public SecurityFilterChain securityFilterChain(ClientRepository repository, HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorizeHttpRequests) -> {
			for (var path : paths) {
				authorizeHttpRequests.requestMatchers(path).permitAll();
			}

			authorizeHttpRequests.requestMatchers("/images/**").permitAll();
			authorizeHttpRequests.requestMatchers("/webjars/**").permitAll();
			authorizeHttpRequests.requestMatchers(DEFAULT_LOGIN_PAGE_URL).permitAll();
			authorizeHttpRequests.anyRequest().authenticated();
		});

		http.oauth2Login((oauth2Login) -> {
			oauth2Login.clientRegistrationRepository(repository);
			oauth2Login.loginPage(DEFAULT_LOGIN_PAGE_URL);
		});

		if (getEnvironment().matchesProfiles("test")) {
			http.csrf((csrf) -> csrf.disable());
			http.headers((headers) -> headers.disable());
		}

		return http.build();
	}

}
