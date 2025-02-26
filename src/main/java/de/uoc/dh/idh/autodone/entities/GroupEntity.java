package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class GroupEntity {

	public enum Visibility {
		DIRECT, PRIVATE, PUBLIC, UNLISTED
	}

	//

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@ManyToOne(optional = false)
	public TokenEntity token;

	@OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "group")
	public List<StatusEntity> status;

	//

	@Enumerated(STRING)
	@Column(nullable = false)
	public Visibility visibility;

	//

	@Column()
	public String description;

	@Column(nullable = false)
	public boolean enabled;

	@Column(nullable = false)
	public String name;

	@Column(nullable = false)
	public boolean threaded;

	//

	@Transient()
	public List<Exception> exceptions;

}
