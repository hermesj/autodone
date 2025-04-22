package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.UUID;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "server_uuid", "username" }) })
public class TokenEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "token")
	public List<GroupEntity> groups;

	@ManyToOne(optional = false)
	public ServerEntity server;

	//

	@Column(nullable = false)
	public String token;

	@Column(nullable = false)
	public String username;

}
