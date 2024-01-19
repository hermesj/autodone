package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_REDIRECT;
import static de.uoc.dh.idh.autodone.config.SecurityConfig.SCHEME;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.reactive.function.client.WebClient.create;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import de.uoc.dh.idh.autodone.entities.MastodonInstanceEntity;
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

	public Iterable<MastodonInstanceEntity> getAll() {
		return instanceRepository.findAll();
	}

	public MastodonInstanceEntity getOne(String domain) {
		var instance = instanceRepository.findByDomain(domain);
		var metadata = identify(domain);

		if (instance == null) {
			instance = register(metadata.domain);
			instance.domain = metadata.domain;
			instanceRepository.save(instance);
		}

		return metadata;
	}

	private MastodonInstanceEntity identify(String domain) {
		var http = create(fromPath(MASTODON_API_INSTANCE).scheme(SCHEME).host(domain).build().toString());
		return http.get().retrieve().bodyToMono(MastodonInstanceEntity.class).block();
	}

	private MastodonInstanceEntity register(String domain) {
		var formData = new LinkedMultiValueMap<String, String>();
		formData.add("client_name", buildProperties.getName());
		formData.add("redirect_uris", fromCurrentContextPath().path(OAUTH_REDIRECT).buildAndExpand(domain).toString());
		formData.add("scopes", MASTODON_OAUTH_SCOPES);
		formData.add("website", fromCurrentContextPath().build().toString());

		var http = create(fromPath(MASTODON_API_APPS).scheme(SCHEME).host(domain).build().toString());
		return http.post().body(fromFormData(formData)).retrieve().bodyToMono(MastodonInstanceEntity.class).block();
	}

}
