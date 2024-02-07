package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_REVOKE;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.request;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.TokenEntity;
import de.uoc.dh.idh.autodone.repositories.ClientRepository;
import de.uoc.dh.idh.autodone.repositories.TokenRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class TokenService implements OAuth2AuthorizedClientService {

	@Autowired()
	private ClientRepository clientRepository;

	@Autowired()
	private ServerService serverService;

	@Autowired()
	private TokenRepository tokenRepository;

	//

	public TokenEntity getOne() {
		return getOne((OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public TokenEntity getOne(OAuth2AuthenticationToken oauth) {
		return getOne(oauth.getName(), oauth.getAuthorizedClientRegistrationId());
	}

	public TokenEntity getOne(String username, String domain) {
		return tokenRepository.findOneByUsernameAndServerDomain(username, domain);
	}

	//

	public OAuth2AuthorizedClient loadAuthorizedClient() {
		return loadAuthorizedClient((OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public OAuth2AuthorizedClient loadAuthorizedClient(OAuth2AuthenticationToken oauth) {
		return loadAuthorizedClient(oauth.getAuthorizedClientRegistrationId(), oauth.getName());
	}

	@Override()
	@SuppressWarnings("all")
	public OAuth2AuthorizedClient loadAuthorizedClient(String domain, String username) {
		var client = clientRepository.findByRegistrationId(domain);
		var token = tokenRepository.findOneByUsernameAndServerDomain(username, domain);
		var oauth = new OAuth2AccessToken(BEARER, token.token, null, null);

		return new OAuth2AuthorizedClient(client, username, oauth);
	}

	//

	public void removeAuthorizedClient() {
		removeAuthorizedClient((OAuth2AuthenticationToken) getContext().getAuthentication());
	}

	public void removeAuthorizedClient(OAuth2AuthenticationToken token) {
		removeAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
	}

	@Override()
	public void removeAuthorizedClient(String domain, String username) {
		var server = serverService.getOne(domain);
		var token = tokenRepository.findOneByUsernameAndServerDomain(username, domain);

		var data = new HashMap<String, Object>();
		data.put("client_id", server.clientId);
		data.put("client_secret", server.clientSecret);
		data.put("token", token.token);

		var href = remoteHref(domain, MASTODON_OAUTH_REVOKE);
		request(TokenEntity.class).post(href, data);
		tokenRepository.delete(token);
	}

	//

	@Override()
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
		var domain = authorizedClient.getClientRegistration().getRegistrationId();
		var token = tokenRepository.findOneByUsernameAndServerDomain(principal.getName(), domain);

		if (token == null) {
			token = new TokenEntity();
			token.server = serverService.getOne(domain);
			token.username = principal.getName();
		}

		token.token = authorizedClient.getAccessToken().getTokenValue();
		tokenRepository.save(token);
	}

}
