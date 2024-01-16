package de.uoc.dh.idh.autodone.repositories;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_REDIRECT;
import static de.uoc.dh.idh.autodone.config.SecurityConfig.SCHEME;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

@Repository()
public class MastodonRegistrationRepository implements ClientRegistrationRepository {

	@Autowired()
	private BuildProperties buildProperties;

	@Autowired()
	private MastodonInstanceRepository instanceRepository;

	@Value("${mastodon.api.apps}")
	private String MASTODON_API_APPS;

	@Value("${mastodon.api.instance}")
	private String MASTODON_API_INSTANCE;

	@Value("${mastodon.oauth.authorize}")
	private String MASTODON_OAUTH_AUTHORIZE;

	@Value("${mastodon.oauth.scopes}")
	private String MASTODON_OAUTH_SCOPES;

	@Value("${mastodon.oauth.token}")
	private String MASTODON_OAUTH_TOKEN;

	@Value("${mastodon.oauth.userinfo}")
	private String MASTODON_OAUTH_USERINFO;

	@Override()
	public ClientRegistration findByRegistrationId(String domain) {
		var builder = ClientRegistration.withRegistrationId(domain);
		var instance = instanceRepository.findByDomain(domain);

		builder.authorizationGrantType(AUTHORIZATION_CODE);
		builder.clientAuthenticationMethod(CLIENT_SECRET_POST);

		builder.clientId(instance.clientId);
		builder.clientName(buildProperties.getName());
		builder.clientSecret(instance.clientSecret);

		builder.authorizationUri(fromPath(MASTODON_OAUTH_AUTHORIZE).scheme(SCHEME).host(domain).build().toString());
		builder.redirectUri(fromCurrentContextPath().path(OAUTH_REDIRECT).buildAndExpand(domain).toString());
		builder.tokenUri(fromPath(MASTODON_OAUTH_TOKEN).scheme(SCHEME).host(domain).build().toString());
		builder.userInfoUri(fromPath(MASTODON_OAUTH_USERINFO).scheme(SCHEME).host(domain).build().toString());

		builder.scope(MASTODON_OAUTH_SCOPES.split(" "));
		builder.userNameAttributeName("username");

		return builder.build();
	}

}
