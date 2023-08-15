package de.uoc.dh.idh.autodone.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name="image")
public class ImageFile {


	@Id
	@GeneratedValue
    private UUID id;

    private String fileName;
    private long fileSize;
    private String fileType;

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "mastodon_user_id")
    private MastodonUser mastodonUser;

    @ManyToOne
    @JoinColumn(name = "mastodon_post_id")
    private MastodonPost mastodonPost;

    
    public ImageFile(String fileName, String fileType, long fileSize, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.data = data;
    }
    

}




