package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_REDIRECT;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import de.uoc.dh.idh.autodone.entities.MastodonInstance;
import de.uoc.dh.idh.autodone.repositories.MastodonInstanceRepository;

@Service()
public class MastodonInstanceService {

	@Autowired()
	private BuildProperties buildProperties;

	@Autowired()
	private MastodonInstanceRepository instanceRepository;

	@Value("${mastodon.api.apps}")
	private String MASTODON_API_APPS;

	@Value("${mastodon.api.instance}")
	private String MASTODON_API_INSTANCE;

	@Value("${mastodon.oauth.scopes}")
	private String MASTODON_OAUTH_SCOPES;

	public Iterable<MastodonInstance> getAll() {
		return instanceRepository.findAll();
	}

	public MastodonInstance getOne(String domain) {
		var instance = instanceRepository.findByDomain(domain);
		var metadata = identify(domain);

		if (instance == null) {
			instance = register(metadata.domain);
			instance.domain = metadata.domain;
			instanceRepository.save(instance);
		}

		return metadata;
	}

	private MastodonInstance identify(String domain) {
		var client = WebClient.create(fromPath(MASTODON_API_INSTANCE).scheme("http").host(domain).build().toString());
		return client.get().retrieve().bodyToMono(MastodonInstance.class).block();
	}

	private MastodonInstance register(String domain) {
		var formData = new LinkedMultiValueMap<String, String>();
		formData.add("client_name", buildProperties.getName());
		formData.add("redirect_uris", fromCurrentContextPath().path(OAUTH_REDIRECT).buildAndExpand(domain).toString());
		formData.add("scopes", MASTODON_OAUTH_SCOPES);
		formData.add("website", fromCurrentContextPath().build().toString());

		var client = WebClient.create(fromPath(MASTODON_API_APPS).scheme("http").host(domain).build().toString());
		return client.post().body(fromFormData(formData)).retrieve().bodyToMono(MastodonInstance.class).block();
	}

}
