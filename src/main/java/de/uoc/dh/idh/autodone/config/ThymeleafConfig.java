package de.uoc.dh.idh.autodone.config;

import static de.uoc.dh.idh.autodone.utils.WebUtils.href;
import static java.util.Set.of;
import static org.springframework.web.util.ServletRequestPathUtils.PATH_ATTRIBUTE;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import de.uoc.dh.idh.autodone.utils.ProjectUtils;

@Configuration()
public class ThymeleafConfig extends AbstractDialect implements IExpressionObjectDialect {

	@Autowired()
	private ProjectUtils projectUtils;

	private final Set<String> expressionObjectNames;

	//

	public ThymeleafConfig() {
		super(ThymeleafConfig.class.getSimpleName());
		expressionObjectNames = of("path", "pom", "uri");
	}

	//

	@Override()
	public IExpressionObjectFactory getExpressionObjectFactory() {
		return new IExpressionObjectFactory() {

			@Override()
			public Set<String> getAllExpressionObjectNames() {
				return expressionObjectNames;
			}

			@Override()
			public Object buildObject(IExpressionContext context, String expressionObjectName) {
				return switch (expressionObjectName) {
				case "path" -> context.getVariable(PATH_ATTRIBUTE).toString();
				case "pom" -> projectUtils.projectModel;
				case "uri" -> href();
				default -> null;
				};
			}

			@Override()
			public boolean isCacheable(String expressionObjectName) {
				return switch (expressionObjectName) {
				case "path" -> true;
				case "pom" -> true;
				case "uri" -> false;
				default -> false;
				};
			}

		};
	}

}
