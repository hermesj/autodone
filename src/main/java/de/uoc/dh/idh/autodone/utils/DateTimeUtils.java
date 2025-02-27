package de.uoc.dh.idh.autodone.utils;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMPORT_DATE;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMPORT_DAYS;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMPORT_TIME;
import static java.time.LocalDateTime.ofInstant;
import static java.time.Year.now;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.of;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Component()
public class DateTimeUtils {

	@Autowired()
	private HttpSession httpSession;

	//

	public ZoneOffset offset() {
		return (ZoneOffset) httpSession.getAttribute("zoneOffset");
	}

	//

	public Instant parse(String dateTime) {
		return LocalDateTime.parse(dateTime).toInstant(offset());
	}

	public Instant parse(LocalDate date, LocalTime time) {
		return of(date, time, offset()).toInstant();
	}

	//

	public LocalDate parseDate(String date) {
		for (var format : AUTODONE_IMPORT_DATE) {
			try {
				return LocalDate.parse(date, ofPattern(format));
			} catch (Exception exception) {
				continue;
			}
		}

		for (var format : AUTODONE_IMPORT_DAYS) {
			try {
				return LocalDate.parse(date + "." + now(UTC), ofPattern(format + ".yyyy"));
			} catch (Exception exception) {
				continue;
			}
		}

		return null;
	}

	//

	public LocalTime parseTime(String time) {
		for (var format : AUTODONE_IMPORT_TIME) {
			try {
				return LocalTime.parse(time, ofPattern(format));
			} catch (Exception exception) {
				continue;
			}
		}

		return null;
	}

	//

	private DateTimeUtils(ConversionService conversionService, ConverterRegistry converterRegistry) {
		converterRegistry.addConverter(new Converter<Instant, String>() {

			@Override()
			public String convert(Instant source) {
				return conversionService.convert(ofInstant(source, offset()), String.class);
			}

		});

		converterRegistry.addConverter(new Converter<String, Instant>() {

			@Override()
			public Instant convert(String source) {
				return conversionService.convert(source, LocalDateTime.class).toInstant(offset());
			}

		});
	}

}
