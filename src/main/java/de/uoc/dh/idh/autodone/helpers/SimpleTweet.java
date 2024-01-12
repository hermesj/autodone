package de.uoc.dh.idh.autodone.helpers;

import java.net.URL;
import java.time.LocalTime;
import java.util.Date;

public class SimpleTweet {
    private Date date;
    private LocalTime time;
    private String text;
    private URL url;
    private Double longitude;
    private Double latitude;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime localTime) {
		this.time = localTime;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Tweet schedule: " + date + " " + time + "\n");
		buff.append("Tweet content: " + text + " " + url + " loc: " + longitude + latitude + "\n");
		return buff.toString();
	}

    
}
