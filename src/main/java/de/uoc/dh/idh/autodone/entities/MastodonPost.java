package de.uoc.dh.idh.autodone.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Entity(name = "mastodonpost")
public class MastodonPost implements Comparable<MastodonPost>{



    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "postgroup_id", nullable = false)
    protected PostGroup group;

    @ManyToOne
    @JoinColumn(name = "mastodonuser_id", nullable = false)
    private MastodonUser mastodonuser;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imageFile_id")
    private List<ImageFile> imageFile = new ArrayList<ImageFile>();

    private String pageID;

    @Column (length= 10485760, nullable = false)
    private String content;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column(columnDefinition = "boolean default false")
    private boolean scheduled;

    @Column(columnDefinition = "boolean default false")
    private boolean posted;

	@Column(columnDefinition = "boolean default false")
    private boolean error;

    @Column (length= 10485760)
    private String img;

    @Column (nullable = false)
    private int timezoneOffset;

    @Column
    private float longitude;

    @Column
    private float latitude;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled;

    public MastodonPost(String content, String date, PostGroup group, String facebookpageID) {
        this.content = content;
        this.pageID = facebookpageID;
        this.date = date;
        this.group = group;
        this.mastodonuser = group.getMastodonUser();
    }



	public int compareTo(MastodonPost post) {

        if (this.posted) {
            return 1;
        } else if (post.isPosted()) {
            return -1;
        }

        StringBuilder time1 = new StringBuilder(post.time.replace(":", ""));
        StringBuilder time2 = new StringBuilder(this.time.replace(":", ""));

        while (time1.length() < 4) {
            time1.insert(0, "0");
        }

        while (time2.length() < 4) {
            time2.insert(0, "0");
        }

        String dateTime1 = post.date.replace("-", "") + time1.toString();
        String dateTime2 = this.date.replace("-", "") + time2.toString();

        return dateTime2.compareTo(dateTime1);

    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", facebookpage=" + group.getFbId() +
                ", fbuser=" + mastodonuser.getMstdId() +
                ", facebookpageID='" + pageID + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", scheduled=" + scheduled +
                ", posted=" + posted +
                ", img='" + img + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", enabled=" + enabled +
                '}';
    }
    
    public boolean isPosted() {
		return posted;
	}


	public void setPosted(boolean posted) {
		this.posted = posted;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public PostGroup getGroup() {
		return group;
	}


	public void setGroup(PostGroup group) {
		this.group = group;
	}


	public MastodonUser getMastodonuser() {
		return mastodonuser;
	}


	public void setMastodonuser(MastodonUser mastodonuser) {
		this.mastodonuser = mastodonuser;
	}


	public List<ImageFile> getImageFile() {
		return imageFile;
	}


	public void setImageFile(List<ImageFile> imageFile) {
		this.imageFile = imageFile;
	}


	public String getPageID() {
		return pageID;
	}


	public void setPageID(String pageID) {
		this.pageID = pageID;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public boolean isScheduled() {
		return scheduled;
	}


	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}


	public boolean isError() {
		return error;
	}


	public void setError(boolean error) {
		this.error = error;
	}


	public String getImg() {
		return img;
	}


	public void setImg(String img) {
		this.img = img;
	}


	public int getTimezoneOffset() {
		return timezoneOffset;
	}


	public void setTimezoneOffset(int timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}


	public float getLongitude() {
		return longitude;
	}


	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}


	public float getLatitude() {
		return latitude;
	}


	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}

