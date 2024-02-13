package de.uoc.dh.idh.autodone.services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.uoc.dh.idh.autodone.entities.GroupEntity;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;

@Service()
public class ImportService {

	
	//TODO change return value to GroupEntity
	public String parse(InputStream inputStream) {
		
		//TODO: Set client ZoneID
		ZoneId clientZoneId = ZoneId.of("Europe/Paris");
		
		
		GroupEntity toReturn = new GroupEntity();
		List<StatusEntity> posts = new ArrayList<StatusEntity>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			int lineNo = 1;
			while ((line = in.readLine()) != null) {

				StatusEntity actualPost = new StatusEntity();

				String[] columns = line.split("\t"); // Splitting the line by tabs
				
				LocalDate date = parseDate(columns[0]);
				if (date == null) {
					actualPost.exceptions
							.add(new ParseException("Not parseable date (first column) ", lineNo));
				}
				LocalTime time = parseTime(columns[1]);
				if (time == null) {
					actualPost.exceptions
							.add(new ParseException("Not parseable time (second column) ", lineNo));
				}
				
				if(date!=null && time !=null) {
					Instant dateTimeUTC = getUTC(clientZoneId, date, time);
					if(dateTimeUTC.isBefore(Instant.now())) {
						dateTimeUTC = getFutureDate(dateTimeUTC);
					}
						
					actualPost.date = dateTimeUTC;
				}
				
				
				String content = columns[2];
				if (content == null || content.isEmpty()) {
					actualPost.exceptions
							.add(new ParseException("No content found (third column) ", lineNo));
				}
				
				actualPost.status = content;
				
				// check picture URL
				if(columns.length>3) {
					String pictureURL = columns[3];
					if(pictureURL!=null && !pictureURL.trim().isEmpty()) {
						MediaEntity me = getPictureFromURL(pictureURL);
						if(me==null) {
							actualPost.exceptions
							.add(new ParseException("URL (forth column) does not point to a picture or picture is not accessible.", lineNo));
						}
						else {
						 // check picture descriptions
							if(columns.length>4) {
								me.description = columns[4]; 
							}
							List<MediaEntity> mes = new ArrayList<MediaEntity>();
							mes.add(me);
							actualPost.media = mes;
						}
					}
				}
						
				posts.add(actualPost);
				
				lineNo++;
			}
		} catch (IOException e) {
			e.printStackTrace(); // Handle exceptions appropriately
		}
		toReturn.status = posts;
		return toReturn.toString();
	}

	private Instant getFutureDate(Instant dateTimeUTC) {
		 LocalDateTime dateTimeInUtc = LocalDateTime.ofInstant(dateTimeUTC, ZoneId.of("UTC"));
         
         int currentYear = Year.now(ZoneId.of("UTC")).getValue();
         
         LocalDateTime adjustedDateTime = dateTimeInUtc.withYear(currentYear);
         
         if (adjustedDateTime.isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
             adjustedDateTime = adjustedDateTime.plusYears(1);
         }
         
         return adjustedDateTime.atZone(ZoneId.of("UTC")).toInstant();
         
	}

	private static Instant getUTC(ZoneId clientZoneId, LocalDate localDate, LocalTime localTime) {
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
		ZonedDateTime clientZonedDateTime = ZonedDateTime.of(localDateTime, clientZoneId);
		Instant nowInUTC = clientZonedDateTime.toInstant();
		return nowInUTC;
	}

	private LocalDate parseDate(String dateString) {
		String[] dateFormats = { "yyyy-MM-dd", "dd/MM/yyyy", "dd.MM.yyyy", "d.M.yyyy", "d/M/yyyy"};

		String[] dateFormatsWithoutYear = { "MM-dd", "dd/MM", "dd.MM", "d.M", "d/M"};
		for (String format : dateFormats) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				return LocalDate.parse(dateString, formatter);
			} catch (DateTimeParseException e) {
				// Continue trying other formats
			}
		}
		
	    for (String format : dateFormatsWithoutYear) {
	        try {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	            String dateStringWithCurrentYear = dateString + "." + Year.now(ZoneId.of("UTC"));
	            formatter = DateTimeFormatter.ofPattern(format + ".yyyy");
	            return LocalDate.parse(dateStringWithCurrentYear, formatter);
	        } catch (DateTimeParseException e) {
	        	// Continue trying other formats
	        }
	    }
		return null;
	}

	private LocalTime parseTime(String timeString) {
		String[] timeFormats = { "HH:mm:ss", "HH:mm", "H:mm", "H:mm:ss"};

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

	private MediaEntity getPictureFromURL(String urlString){
		MediaEntity toReturn = new MediaEntity();
		HttpURLConnection connection = null;
        String contentType = null;
        byte[] imageData = null;
        

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Check if URL contains picture
            contentType = connection.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
            	// picture to bytearray
                imageData = readInputStreamAsByteArray(connection.getInputStream());
            } else {
                //not a picture
                return null; 
            }
        } catch (IOException e) {
        	// IOException	
            return null; 
        } 
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        toReturn.contentType = contentType;
        toReturn.file = imageData;
		return toReturn;

	}
	
	private static byte[] readInputStreamAsByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
	
	

}
