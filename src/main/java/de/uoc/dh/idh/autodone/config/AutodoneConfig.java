package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.AutodoneApplication.getEnvironment;

import org.springframework.context.annotation.Configuration;

@Configuration()
public class AutodoneConfig {

	public static final int AUTODONE_PAGINATION;

	public static final int AUTODONE_SCHEDULING;

	public static final int AUTODONE_THREADPOOL;

	static {
		AUTODONE_PAGINATION = getEnvironment().getProperty("autodone.pagination", int.class);
		AUTODONE_SCHEDULING = getEnvironment().getProperty("autodone.scheduling", int.class);
		AUTODONE_THREADPOOL = getEnvironment().getProperty("autodone.threadpool", int.class);
	}

}
