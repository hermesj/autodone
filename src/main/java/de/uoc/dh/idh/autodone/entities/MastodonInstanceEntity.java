package de.uoc.dh.idh.autodone.entities;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

@Entity()
@JsonNaming(SnakeCaseStrategy.class)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "domain" }) })
public class MastodonInstanceEntity {

	@Id()
	@GeneratedValue(strategy = GenerationType.UUID)
	public String uuid;

	//

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "instance")
	public List<MastodonClientEntity> clients;

	//

	@Column(nullable = false)
	public String clientId;

	@Column(nullable = false)
	public String clientSecret;

	@Column(nullable = false)
	public String domain;

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
