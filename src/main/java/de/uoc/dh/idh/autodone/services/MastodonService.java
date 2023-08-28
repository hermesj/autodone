package de.uoc.dh.idh.autodone.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.uoc.dh.idh.autodone.entities.MastodonPost;
import de.uoc.dh.idh.autodone.entities.MastodonUser;
import lombok.extern.log4j.Log4j2;

@Transactional
@Log4j2
@Service
public class MastodonService {

	public String post(MastodonUser user, MastodonPost post) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProfilePicture(String oauthToken) {
		// TODO Auto-generated method stub
		return null;
	}

}
