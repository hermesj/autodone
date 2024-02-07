package de.uoc.dh.idh.autodone.repositories;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_AUTHORIZE;
import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_SCOPES;
import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_TOKEN;
import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_USERINFO;
import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_REDIRECT;
import static de.uoc.dh.idh.autodone.utils.WebUtils.localHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static org.springframework.security.oauth2.client.registration.ClientRegistration.withRegistrationId;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository()
@Transactional()
public class ClientRepository implements ClientRegistrationRepository {

	@Autowired()
	private BuildProperties buildProperties;

	@Autowired()
	private ServerRepository serverRepository;

	//

	@Override()
	public ClientRegistration findByRegistrationId(String domain) {
		var builder = withRegistrationId(domain);
		var server = serverRepository.findOneByDomain(domain);

		builder.authorizationGrantType(AUTHORIZATION_CODE);
		builder.clientAuthenticationMethod(CLIENT_SECRET_POST);

		builder.clientId(server.clientId);
		builder.clientName(buildProperties.getName());
		builder.clientSecret(server.clientSecret);

		builder.authorizationUri(remoteHref(domain, MASTODON_OAUTH_AUTHORIZE).toString());
		builder.redirectUri(localHref(OAUTH_REDIRECT).toString(domain));
		builder.tokenUri(remoteHref(domain, MASTODON_OAUTH_TOKEN).toString());
		builder.userInfoUri(remoteHref(domain, MASTODON_OAUTH_USERINFO).toString());

		builder.scope(MASTODON_OAUTH_SCOPES.split(" "));
		builder.userNameAttributeName("username");

		return builder.build();
	}

}
