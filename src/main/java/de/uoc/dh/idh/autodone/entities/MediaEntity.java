package de.uoc.dh.idh.autodone.entities;

import static jakarta.persistence.GenerationType.UUID;
import static java.util.Base64.getEncoder;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data()
@Entity()
@JsonNaming(SnakeCaseStrategy.class)
public class MediaEntity {

	@Id()
	@GeneratedValue(strategy = UUID)
	public UUID uuid;

	//

	@ManyToOne(optional = false)
	public StatusEntity status;

	//

	@Column()
	public String contentType;

	@Column(length = 1500)
	public String description;

	@Column(length = 1024000)
	public byte[] file;

	@Column()
	public String id;

	@Column()
	public String url;

	//

	public String getUrl() {
		try {
			return "data:" + contentType + ";base64," + getEncoder().encodeToString(file);
		} catch (Exception exception) {
			return url;
		}
	}

}
