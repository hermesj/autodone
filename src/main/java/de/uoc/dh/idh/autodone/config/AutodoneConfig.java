package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.AutodoneApplication.getEnvironment;

import java.util.List;

import org.springframework.context.annotation.Configuration;

@Configuration()
@SuppressWarnings("unchecked")
public class AutodoneConfig {

	public static final List<String> AUTODONE_IMPORT_DATE;

	public static final List<String> AUTODONE_IMPORT_DAYS;

	public static final List<String> AUTODONE_IMPORT_TIME;

	//

	public static final String AUTODONE_IMPORT_SKIP;

	public static final String AUTODONE_IMPORT_SNIP;

	//

	public static final int AUTODONE_PAGINATION;

	public static final int AUTODONE_SCHEDULING;

	public static final int AUTODONE_THREADPOOL;

	//

	static {
		AUTODONE_IMPORT_DATE = getEnvironment().getProperty("autodone.import.date", List.class);
		AUTODONE_IMPORT_DAYS = getEnvironment().getProperty("autodone.import.days", List.class);
		AUTODONE_IMPORT_TIME = getEnvironment().getProperty("autodone.import.time", List.class);

		AUTODONE_IMPORT_SKIP = getEnvironment().getProperty("autodone.import.skip", String.class);
		AUTODONE_IMPORT_SNIP = getEnvironment().getProperty("autodone.import.snip", String.class);

		AUTODONE_PAGINATION = getEnvironment().getProperty("autodone.pagination", int.class);
		AUTODONE_SCHEDULING = getEnvironment().getProperty("autodone.scheduling", int.class);
		AUTODONE_THREADPOOL = getEnvironment().getProperty("autodone.threadpool", int.class);
	}

}
