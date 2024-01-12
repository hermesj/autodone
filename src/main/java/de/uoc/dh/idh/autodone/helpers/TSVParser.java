package de.uoc.dh.idh.autodone.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TSVParser {

	public List<SimpleTweet> readTSV(File file) {
		List<SimpleTweet> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t"); // Splitting the line by tabs

                // Check for empty required fields (Date, Time, Text)
                if (values.length < 3 || values[0].isEmpty() || values[1].isEmpty() || values[2].isEmpty()) {
                    System.out.println("Warning: Missing required data. Skipping line.");
                    continue; // Skip this line
                }
                if (values.length < 6) {
                    String[] extendedValues = new String[6];
                    System.arraycopy(values, 0, extendedValues, 0, values.length);
                    for (int i = values.length; i < 6; i++) {
                        extendedValues[i] = "";
                    }
                    values = extendedValues;
                }
                SimpleTweet entry = new SimpleTweet();
                entry.setDate(parseDate(values[0]));
                entry.setTime(parseTime(values[1]));
                entry.setText(values[2]);
                entry.setUrl((values[3]==null || values[3].isEmpty()) ? null : parseURL(values[3])); // Only parse if not empty
                entry.setLongitude((values[3]==null || values[4].isEmpty()) ? null : parseDouble(values[4])); // Only parse if not empty
                entry.setLatitude((values[3]==null || values[5].isEmpty() )? null : parseDouble(values[5])); // Only parse if not empty
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return entries;
	}

	public List<SimpleTweet> readGoogleSpreadsheet(String fileURL) {
		List<SimpleTweet> entries = new ArrayList<SimpleTweet>();
		// Code to read from Google Spreadsheet
		// - Authentication and accessing the file
		// - Reading lines
		// - Validating and converting the data
		// - Adding to the list
		return entries;
	}

	private Date parseDate(String dateString) {
	    String[] dateFormats = {
	        "yyyy-MM-dd", "dd/MM/yyyy", "dd.MM.yyyy"
	    };

	    for (String format : dateFormats) {
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        try {
	            return sdf.parse(dateString);
	        } catch (ParseException e) {
	            // Continue trying other formats
	        }
	    }
	    return null; // Or throw a new IllegalArgumentException("Invalid date format");
	}

	private LocalTime parseTime(String timeString) {
	    String[] timeFormats = {
	        "HH:mm:ss", "HH:mm"
	    };

	    for (String format : timeFormats) {
	        try {
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	            return LocalTime.parse(timeString, formatter);
	        } catch (DateTimeParseException e) {
	            // Continue trying other formats
	        }
	    }
	    return null; // Or throw a new IllegalArgumentException("Invalid time format");
	}


	private URL parseURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			return null; // Or handle this exception as needed
		}
	}

	private Double parseDouble(String doubleString) {
		try {
			return Double.parseDouble(doubleString);
		} catch (NumberFormatException e) {
			return null; // Or handle this exception as needed
		}
	}

	// DataEntry class and other helper methods

}
