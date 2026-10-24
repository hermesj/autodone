package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.utils.ObjectUtils.pick;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.trimToNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

	@Test()
	void picksOnlyAllowedKeys() {
		var form = Map.of("name", "Group", "token", "foreign-uuid", "status", "foreign-uuid", "_method", "post");

		assertEquals(Map.of("name", "Group"), pick(form, "name", "description"));
	}

	@Test()
	void trimsLikeStringTrimmerEditor() {
		assertEquals("a\nb", trimToNull("  a\r\nb \r\n"));
		assertNull(trimToNull(" \r\n "));
	}

	@Test()
	void trimsIndependentlyAcrossThreads() throws Exception {
		var executor = Executors.newFixedThreadPool(8);

		try {
			var futures = IntStream.range(0, 2000).mapToObj((i) -> {
				return executor.submit(() -> trimToNull(" status " + i + " ").equals("status " + i));
			}).toList();

			for (Future<Boolean> future : futures) {
				assertEquals(true, future.get());
			}
		} finally {
			executor.shutdown();
		}
	}

}
