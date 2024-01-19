package de.uoc.dh.idh.autodone.utils;

import org.springframework.stereotype.Component;

@Component()
public class ObjectUtils {

	public static <T> T mergeFields(T source, T target) {
		if (source != null && target != null && source != target) {
			if (target.getClass().isAssignableFrom(source.getClass())) {
				for (var field : source.getClass().getDeclaredFields()) {
					try {
						var sourceField = field.get(source);
						var targetField = field.get(target);

						if (sourceField != null && targetField == null) {
							field.set(target, sourceField);
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
