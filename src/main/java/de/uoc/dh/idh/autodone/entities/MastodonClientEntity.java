package de.uoc.dh.idh.autodone.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

// classpath:org/springframework/security/oauth2/client/oauth2-client-schema.sql

@Entity(name = "oauth2_authorized_client")
@IdClass(MastodonClientEntity.PrimaryKey.class)
public class MastodonClientEntity {

	@Id()
	@Column(columnDefinition = "VARCHAR(100) NOT NULL")
	public String clientRegistrationId;

	@Id()
	@Column(columnDefinition = "VARCHAR(200) NOT NULL")
	public String principalName;

	//

	@Column(columnDefinition = "TIMESTAMP NOT NULL")
	public Date accessTokenExpiresAt;

	@Column(columnDefinition = "TIMESTAMP NOT NULL")
	public Date accessTokenIssuedAt;

	@Column(columnDefinition = "VARCHAR(1000) DEFAULT NULL")
	public String accessTokenScopes;

	@Column(columnDefinition = "VARCHAR(100) NOT NULL")
	public String accessTokenType;

	@Column(columnDefinition = "BLOB NOT NULL")
	public byte[] accessTokenValue;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL")
	public Date createdAt;

	@Column(columnDefinition = "TIMESTAMP DEFAULT NULL")
	public Date refreshTokenIssuedAt;

	@Column(columnDefinition = "BLOB DEFAULT NULL")
	public byte[] refreshTokenValue;

	//

	public class PrimaryKey {

		public String clientRegistrationId;

		public String principalName;

	}

}
