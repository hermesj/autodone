package de.uoc.dh.idh.autodone.utils;

import static org.springframework.util.StringUtils.deleteAny;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component()
public class ObjectUtils {

	public static final int CONVERT = 1;

	public static final int OVERWRITE = 2;

	public static final int TRIM_NULL = 4;

	public static final int FORCE = 7;

	//

	private static ConversionService conversionService;

	//

	public static <T> T copyFields(T source, T target) {
		return copyFields(source, target, 0);
	}

	public static <T> T copyFields(T source, T target, int flags) {
		if (source != null && target != null && source != target) {
			if (target.getClass().isAssignableFrom(source.getClass())) {
				for (var field : source.getClass().getDeclaredFields()) {
					try {
						var sourceValue = field.get(source);
						var targetValue = field.get(target);

						if (sourceValue != null) {
							if ((flags & CONVERT) == CONVERT) {
								sourceValue = conversionService.convert(sourceValue, field.getType());
							}

							if (field.getType().equals(String.class) && (flags & TRIM_NULL) == TRIM_NULL) {
								sourceValue = trimToNull((String) sourceValue);
							}

							if (targetValue == null || (flags & OVERWRITE) == OVERWRITE) {
								field.set(target, sourceValue);
							}
						}
					} catch (Exception exception) {
						continue;
					}
				}
			}
		}

		return target;
	}

	//

	public static <T> T mapFields(Map<String, ?> source, T target) {
		return mapFields(source, target, 0);
	}

	public static <T> T mapFields(Map<String, ?> source, T target, int flags) {
		if (source != null && target != null && !source.isEmpty()) {
			for (var key : source.keySet()) {
				try {
					var field = target.getClass().getField(key);
					var sourceValue = source.get(key);
					var targetValue = field.get(target);

					if (sourceValue != null) {
						if ((flags & CONVERT) == CONVERT) {
							sourceValue = conversionService.convert(sourceValue, field.getType());
						}

						if (field.getType().equals(String.class) && (flags & TRIM_NULL) == TRIM_NULL) {
							sourceValue = trimToNull((String) sourceValue);
						}

						if (targetValue == null || (flags & OVERWRITE) == OVERWRITE) {
							field.set(target, sourceValue);
						}
					}
				} catch (Exception exception) {
					continue;
				}
			}
		}

		return target;
	}

	//

	// keeps only the given keys, so request parameters cannot set other public fields
	public static <V> Map<String, V> pick(Map<String, V> source, String... keys) {
		var picked = new HashMap<String, V>();

		for (var key : keys) {
			if (source.containsKey(key)) {
				picked.put(key, source.get(key));
			}
		}

		return picked;
	}

	// same as StringTrimmerEditor("\r", true), which is stateful and must not be shared between threads
	public static String trimToNull(String text) {
		var trimmed = deleteAny(text.trim(), "\r");
		return trimmed.isEmpty() ? null : trimmed;
	}

	//

	private ObjectUtils(ConversionService conversionService) {
		ObjectUtils.conversionService = conversionService;
	}

}
