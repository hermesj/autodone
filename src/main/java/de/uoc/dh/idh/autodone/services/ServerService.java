package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_API_APPS;
import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_API_INSTANCE;
import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_OAUTH_SCOPES;
import static de.uoc.dh.idh.autodone.config.SecurityConfig.OAUTH_REDIRECT;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.copyFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.localHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.request;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.ServerEntity;
import de.uoc.dh.idh.autodone.repositories.ServerRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class ServerService {

	@Autowired()
	private BuildProperties buildProperties;

	@Autowired()
	private ServerRepository serverRepository;

	//

	public Iterable<ServerEntity> getAll() {
		return serverRepository.findAll();
	}

	public ServerEntity getOne(String domain) {
		return getOne(domain, false);
	}

	public ServerEntity getOne(String domain, boolean metadata) {
		var server = serverRepository.findOneByDomain(domain);

		if (metadata) {
			server = copyFields(server, metadata(domain));

			if (server.uuid == null) {
				server = serverRepository.save(copyFields(register(server.domain), server));
			}
		}

		return server;
	}

	//

	private ServerEntity metadata(String domain) {
		var href = remoteHref(domain, MASTODON_API_INSTANCE);
		return request(ServerEntity.class).get(href);
	}

	private ServerEntity register(String domain) {
		var data = new HashMap<String, Object>();
		data.put("client_name", buildProperties.getName());
		data.put("redirect_uris", localHref(OAUTH_REDIRECT).toString(domain));
		data.put("scopes", MASTODON_OAUTH_SCOPES);
		data.put("website", localHref().toString());

		var href = remoteHref(domain, MASTODON_API_APPS);
		return request(ServerEntity.class).post(href, data);
	}

}
