package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.MastodonConfig.MASTODON_API_MEDIA;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.FORCE;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.copyFields;
import static de.uoc.dh.idh.autodone.utils.WebUtils.remoteHref;
import static de.uoc.dh.idh.autodone.utils.WebUtils.request;
import static org.springframework.data.domain.Sort.by;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import de.uoc.dh.idh.autodone.base.BaseService;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.repositories.MediaRepository;
import jakarta.transaction.Transactional;

@Service()
@Transactional()
public class MediaService extends BaseService<MediaEntity> {

	@Autowired()
	private MediaRepository mediaRepository;

	@Autowired()
	private TimerService timerService;

	//

	public MediaEntity getAny(UUID uuid) {
		return mediaRepository.findById(uuid).get();
	}

	public Page<MediaEntity> getPage(String page, String sort, StatusEntity status) {
		return getPage(pageRequest(page, sort), status.media);
	}

	//

	public MediaEntity publish(UUID uuid) {
		return publish(getAny(uuid));
	}

	public MediaEntity publish(MediaEntity media) {
		var data = new LinkedMultiValueMap<String, Object>();
		data.add("description", media.description);
		data.add("filename", media.uuid.toString());
		data.add("name", media.uuid.toString());

		data.add("file", new ByteArrayResource(media.file) {

			@Override()
			public String getFilename() {
				return media.uuid.toString();
			}

		});

		media.contentType = null;
		media.file = null;

		var href = remoteHref(media.status.group.token.server.domain, MASTODON_API_MEDIA);
		var post = request(MediaEntity.class).auth(media.status.group.token).form().post(href, data);
		return save(copyFields(post, media, FORCE));
	}

	//

	public MediaEntity save(MediaEntity media) {
		media = mediaRepository.save(media);
		timerService.unscheduleMedia(media);

		if (media.status.group.enabled) {
			timerService.scheduleMedia(media);
		}

		return media;
	}

	//

	@Override()
	public void delete(UUID uuid, String username, String domain) {
		mediaRepository //
				.deleteByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override()
	public MediaEntity getOne(UUID uuid, String username, String domain) {
		return mediaRepository //
				.findOneByUuidAndStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(uuid, username, domain);
	}

	@Override
	public Iterable<MediaEntity> getOwn(String username, String domain) {
		return mediaRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(username, domain);
	}

	@Override()
	public Page<MediaEntity> getPage(Pageable request, String username, String domain) {
		return mediaRepository //
				.findAllByStatusGroupTokenUsernameAndStatusGroupTokenServerDomain(request, username, domain);
	}

	//

	@Override()
	protected Sort getSort() {
		return by("status.group.name");
	}

}
