package de.uoc.dh.idh.autodone.utils;

import static java.util.Arrays.asList;
import static org.springframework.web.util.ServletRequestPathUtils.PATH_ATTRIBUTE;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

@Component()
public class ThymeleafUtils extends AbstractDialect implements IExpressionObjectDialect {

	@Autowired()
	private BuildProperties buildProperties;

	protected ThymeleafUtils() {
		super(ThymeleafUtils.class.getSimpleName());
	}

	@Override()
	public IExpressionObjectFactory getExpressionObjectFactory() {
		return new IExpressionObjectFactory() {

			@Override()
			public Set<String> getAllExpressionObjectNames() {
				return new HashSet<>(asList("buildProperties", "servletPath"));
			}

			@Override()
			public Object buildObject(IExpressionContext context, String expressionObjectName) {
				return switch (expressionObjectName) {
				case "buildProperties" -> buildProperties;
				case "servletPath" -> context.getVariable(PATH_ATTRIBUTE).toString();
				default -> null;
				};
			}

			@Override()
			public boolean isCacheable(String expressionObjectName) {
				return switch (expressionObjectName) {
				case "buildProperties" -> true;
				case "servletPath" -> true;
				default -> false;
				};
			}

		};
	}

}
