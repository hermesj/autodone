package de.uoc.dh.idh.autodone.services;

import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.MastodonClientEntity;
import de.uoc.dh.idh.autodone.repositories.MastodonClientRepository;
import de.uoc.dh.idh.autodone.repositories.MastodonInstanceRepository;
import de.uoc.dh.idh.autodone.repositories.MastodonRegistrationRepository;

@Service()
public class MastodonClientService implements OAuth2AuthorizedClientService {

	@Autowired()
	private MastodonClientRepository clientRepository;

	@Autowired()
	private MastodonInstanceRepository instanceRepository;

	@Autowired()
	private MastodonRegistrationRepository registrationRepository;

	@Override()
	@SuppressWarnings("unchecked")
	public OAuth2AuthorizedClient loadAuthorizedClient(String domain, String username) {
		var client = clientRepository.findOneByInstanceDomainAndUsername(domain, username);
		var registration = registrationRepository.findByRegistrationId(domain);
		var token = new OAuth2AccessToken(BEARER, new String(client.token), null, null);

		return new OAuth2AuthorizedClient(registration, username, token);
	}

	@Override()
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
		var domain = authorizedClient.getClientRegistration().getRegistrationId();
		var client = clientRepository.findOneByInstanceDomainAndUsername(domain, principal.getName());

		if (client == null) {
			client = new MastodonClientEntity();
			client.instance = instanceRepository.findOneByDomain(domain);
			client.username = principal.getName();
		}

		client.token = authorizedClient.getAccessToken().getTokenValue().getBytes();
		clientRepository.save(client);
	}

	@Override()
	public void removeAuthorizedClient(String domain, String username) {
		clientRepository.deleteOneByInstanceDomainAndUsername(domain, username);
	}

}
