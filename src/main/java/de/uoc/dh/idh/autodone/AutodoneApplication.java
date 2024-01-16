package de.uoc.dh.idh.autodone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

@SpringBootApplication()
public class AutodoneApplication extends SpringApplication {

	private static ConfigurableApplicationContext context;

	private static ConfigurableEnvironment environment;

	public static ApplicationContext getContext() {
		return context;
	}

	public static Environment getEnvironment() {
		return environment;
	}

	public static void main(String[] args) {
		new AutodoneApplication().run(args);
	}

	protected AutodoneApplication() {
		super(AutodoneApplication.class);
	}

	@Override()
	protected void applyInitializers(ConfigurableApplicationContext context) {
		super.applyInitializers(AutodoneApplication.context = context);
	}

	@Override()
	protected void bindToSpringApplication(ConfigurableEnvironment environment) {
		super.bindToSpringApplication(AutodoneApplication.environment = environment);
	}

}
