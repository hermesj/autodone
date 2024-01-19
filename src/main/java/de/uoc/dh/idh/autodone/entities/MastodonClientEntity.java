package de.uoc.dh.idh.autodone.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity()
@JsonNaming(SnakeCaseStrategy.class)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "instance", "username" }) })
public class MastodonClientEntity {

	@Id()
	@GeneratedValue(strategy = GenerationType.UUID)
	public String uuid;

	//

	@ManyToOne(optional = false)
	public MastodonInstanceEntity instance;

	//

	@Column(nullable = false)
	public byte[] token;

	@Column(nullable = false)
	public String username;

}
