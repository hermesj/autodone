package de.uoc.dh.idh.autodone.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;
import de.uoc.dh.idh.autodone.utils.TSVParseException;

@Service()
public class ImportService {

	public String parse(InputStream inputStream) {
		ZoneId clientZoneId = null;
		GroupEntity toReturn = new GroupEntity();
		List<StatusEntity> posts = new ArrayList<StatusEntity>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			int lineNo = 1;
			while ((line = in.readLine()) != null) {

				StatusEntity actualPost = new StatusEntity();

				String[] values = line.split("\t"); // Splitting the line by tabs
				LocalDate date = parseDate(values[0]);
				if (date == null) {
					actualPost.exceptions
							.add(new TSVParseException("Not parseable date (first column in row " + lineNo + ")."));
				}
				LocalTime time = parseTime(values[1]);
				if (time == null) {
					actualPost.exceptions
							.add(new TSVParseException("Not parseable time (second column in row " + lineNo + ")."));
				}
				String content = values[2];
				if (content == null || content.isEmpty()) {
					actualPost.exceptions
							.add(new TSVParseException("No content found (third column in row " + lineNo + ")."));
				}

				// URL picUrl = parseURL(values[4]);
				
								
				//TODO: Get media from URL

				//TODO: ClientTime > UTC
				ZonedDateTime convertedDateTime = setLocalTime(clientZoneId, date, time);
				
				actualPost.date = convertedDateTime.toLocalDateTime();
				actualPost.status = content;
				
						
				posts.add(actualPost);
				
				
				lineNo++;
			}
		} catch (IOException e) {
			e.printStackTrace(); // Handle exceptions appropriately
		}
		toReturn.status = posts;
		return toReturn.toString();
	}

	/**
	 * Conversion into the server time zone
	 * 
	 * @param clientZoneId client zone id
	 * @param localDate    client date
	 * @param localTime    client time
	 * @return server time zone
	 */
	public static ZonedDateTime setLocalTime(ZoneId clientZoneId, LocalDate localDate, LocalTime localTime) {
		ZoneId myZoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
		ZonedDateTime clientZonedDateTime = ZonedDateTime.of(localDateTime, clientZoneId);
		ZonedDateTime serverZonedDateTime = clientZonedDateTime.withZoneSameInstant(myZoneId);
		return serverZonedDateTime;
	}

	private LocalDate parseDate(String dateString) {
		String[] dateFormats = { "yyyy-MM-dd", "dd/MM/yyyy", "dd.MM.yyyy" };

		for (String format : dateFormats) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				return LocalDate.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				// Continue trying other formats
			}
		}
		return null;
	}

	private LocalTime parseTime(String timeString) {
		String[] timeFormats = { "HH:mm:ss", "HH:mm" };

		for (String format : timeFormats) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				return LocalTime.parse(timeString, formatter);
			} catch (DateTimeParseException e) {
				// Continue trying other formats
			}
		}
		return null;
	}

	private URL parseURL(String urlString) throws MalformedURLException {

		return new URL(urlString);

	}

}
