package de.uoc.dh.idh.autodone;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import de.uoc.dh.idh.autodone.services.ImportService;

class ImportServiceTests {
	
	@Test
	void testDateTime() {
		ImportService is = new ImportService();
		ZoneId clientZoneId = ZoneId.of("Europe/Paris");
		System.out.println(clientZoneId);
		
		LocalDateTime ldt = LocalDateTime.now();
		System.out.println(ldt);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		System.out.println(zonedDateTime);
		Instant nowInUTC = zonedDateTime.toInstant();
		System.out.println(nowInUTC);
	}

}
