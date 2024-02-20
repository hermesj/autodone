package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.AutodoneApplication.getEnvironment;

import org.springframework.context.annotation.Configuration;

@Configuration()
public class MastodonConfig {

	public static final String MASTODON_API_APPS;

	public static final String MASTODON_API_INSTANCE;

	public static final String MASTODON_API_MEDIA;

	public static final String MASTODON_API_STATUS;

	//

	public static final String MASTODON_OAUTH_AUTHORIZE;

	public static final String MASTODON_OAUTH_REVOKE;

	public static final String MASTODON_OAUTH_SCOPES;

	public static final String MASTODON_OAUTH_TOKEN;

	public static final String MASTODON_OAUTH_USERINFO;

	//

	static {
		MASTODON_API_APPS = getEnvironment().getProperty("mastodon.api.apps", String.class);
		MASTODON_API_INSTANCE = getEnvironment().getProperty("mastodon.api.instance", String.class);
		MASTODON_API_MEDIA = getEnvironment().getProperty("mastodon.api.media", String.class);
		MASTODON_API_STATUS = getEnvironment().getProperty("mastodon.api.status", String.class);

		MASTODON_OAUTH_AUTHORIZE = getEnvironment().getProperty("mastodon.oauth.authorize", String.class);
		MASTODON_OAUTH_REVOKE = getEnvironment().getProperty("mastodon.oauth.revoke", String.class);
		MASTODON_OAUTH_SCOPES = getEnvironment().getProperty("mastodon.oauth.scopes", String.class);
		MASTODON_OAUTH_TOKEN = getEnvironment().getProperty("mastodon.oauth.token", String.class);
		MASTODON_OAUTH_USERINFO = getEnvironment().getProperty("mastodon.oauth.userinfo", String.class);
	}

}
