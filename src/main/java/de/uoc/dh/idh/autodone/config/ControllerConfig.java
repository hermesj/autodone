package de.uoc.dh.idh.autodone.config;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.servlet.mvc.method.RequestMappingInfo.paths;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration()
public class ControllerConfig {

	public static final List<String> paths;

	static {
		paths = new ArrayList<>();
	}

	//

	@Value("classpath:templates/pages/*.html")
	private Resource[] resources;

	//

	@Autowired()
	public void controllerConfig(RequestMappingHandlerMapping mapping) throws Exception {
		for (var resource : resources) {
			var file = resource.getFilename();
			var path = "/" + file.substring(0, file.lastIndexOf('.'));
			var info = paths(path).methods(GET).build();

			var handler = new Handler(path);
			mapping.registerMapping(info, handler, handler.method);
			paths.add(path);
		}
	}

	//

	public static class Handler {

		public final String handle;

		public final Method method;

		//

		public Handler(String handle) throws Exception {
			this.handle = "pages" + handle;
			this.method = getClass().getMethod("handle");
		}

		//

		public String handle() {
			return handle;
		}

	}

}
