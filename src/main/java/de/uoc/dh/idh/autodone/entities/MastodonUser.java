package de.uoc.dh.idh.autodone.entities;

import java.util.List;

import org.springframework.data.annotation.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@ToString
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "mastodonuser")
public class MastodonUser {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String mstdId;

    @Column(nullable = false, columnDefinition="TEXT")
    private String oauthToken;

    @Column
    private String name;

    @Column
    private String email;

    @OneToMany(mappedBy = "mastodonuser", cascade = CascadeType.REMOVE)
    @ElementCollection
    private List<PostGroup> groups;
    
    @OneToMany(mappedBy = "mastodonuser", cascade = CascadeType.REMOVE)
    @ElementCollection
    private List<ImageFile> imageFileList;



    public MastodonUser(String fbId, String oauthToken, String name, String email, List<PostGroup> groups) {
        this.mstdId = fbId;
        this.oauthToken = oauthToken;
        this.name = name;
        this.email = email;
        this.groups = groups;
    }



	public String getMstdId() {
		return mstdId;
	}



	public void setMstdId(String mstdId) {
		this.mstdId = mstdId;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getOauthToken() {
		return oauthToken;
	}



	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public List<PostGroup> getGroups() {
		return groups;
	}



	public void setGroups(List<PostGroup> groups) {
		this.groups = groups;
	}



	public List<ImageFile> getImageFileList() {
		return imageFileList;
	}



	public void setImageFileList(List<ImageFile> imageFileList) {
		this.imageFileList = imageFileList;
	}
    
	
    
}

