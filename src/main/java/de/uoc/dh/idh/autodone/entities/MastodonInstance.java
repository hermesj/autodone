package de.uoc.dh.idh.autodone.entities;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class MastodonInstance {

	@Id()
	public String domain;

	//

	@Column(nullable = false)
	public String clientId;

	@Column(nullable = false)
	public String clientSecret;

	//

	@Transient()
	public String description;

	@Transient()
	public List<Map<String, String>> rules;

	@Transient()
	public String title;

	@Transient()
	public String version;

}
