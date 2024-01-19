package de.uoc.dh.idh.autodone.utils;

import org.springframework.stereotype.Component;

@Component()
public class ObjectUtils {

	public static <T> T mergeFields(T source, T target) {
		if (source != null && target != null && source != target) {
			if (target.getClass().isAssignableFrom(source.getClass())) {
				for (var field : source.getClass().getDeclaredFields()) {
					try {
						var sourceValue = field.get(source);
						var targetValue = field.get(target);

						if (sourceValue != null && targetValue == null) {
							field.set(target, sourceValue);
						}
					} catch (Exception exception) {
						continue;
					}
				}
			}
		}

		return target;
	}

}
