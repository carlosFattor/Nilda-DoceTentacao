package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.URL;

import play.data.validation.Constraints.Required;

@Entity
public class Gallery implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    @Required
    private String name;
    @Required
    @Column
    private String description;
    @URL
	private String urlSmall;
    @URL
    private String urlLarge;
    
    private String image;
    
	public Gallery() {
		super();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getUrlSmall() {
		return urlSmall;
	}
	public void setUrlSmall(String urlSmall) {
		this.urlSmall = urlSmall;
	}
	public String getUrlLarge() {
		return urlLarge;
	}
	public void setUrlLarge(String urlLarge) {
		this.urlLarge = urlLarge;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gallery other = (Gallery) obj;
		if (id != other.id)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Gallery [id=" + id + ", name=" + name + ", description="
				+ description + ", image=" + image + "]";
	}
}