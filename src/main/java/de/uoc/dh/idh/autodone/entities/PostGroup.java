package de.uoc.dh.idh.autodone.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "postgroup")
public class PostGroup {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fbId;

    @ManyToOne
    @JoinColumn(name="mastodonuser_id", nullable = false)
    private MastodonUser mastodonUser;

    @OneToMany(mappedBy="postgroup", cascade = CascadeType.ALL)
    private List<MastodonPost> mastodonPosts = new ArrayList<MastodonPost>();

    @Column(columnDefinition="boolean default true")
    private boolean enabled = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostGroup page = (PostGroup) o;

        return fbId.equals(page.fbId);
    }

    @Override
    public String toString() {
        return name;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public MastodonUser getMastodonUser() {
		return mastodonUser;
	}

	public void setMastodonUser(MastodonUser mastodonUser) {
		this.mastodonUser = mastodonUser;
	}

	public List<MastodonPost> getMastodonPosts() {
		return mastodonPosts;
	}

	public void setMastodonPosts(List<MastodonPost> mastodonPosts) {
		this.mastodonPosts = mastodonPosts;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
    
}
