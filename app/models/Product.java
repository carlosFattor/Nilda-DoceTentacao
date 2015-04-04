package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.validator.constraints.URL;

import play.data.validation.Constraints.Required;

@Entity
@NamedQueries({
	@NamedQuery(name = "product.FindAllByCategoryId", query="select p from Product p where p.category.id = :id"),
	@NamedQuery(name = "product.FindAllByName", query="select p from Product p where p.name like :name")
})

public class Product implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@Required
	private String name;
	@Required
	@Column
	private String description;
	@Required
	@URL
	private String imageSmallUrl;
	@Required
	@URL
	private String imageLargelUrl;
	@Required
	@URL
	private String commentsUrl;
	
	private boolean feature;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Category category;
	
	public Product() {
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

	public String getImageSmallUrl() {
		return imageSmallUrl;
	}

	public void setImageSmallUrl(String imageSmallUrl) {
		this.imageSmallUrl = imageSmallUrl;
	}

	public String getImageLargelUrl() {
		return imageLargelUrl;
	}

	public void setImageLargelUrl(String imageLargelUrl) {
		this.imageLargelUrl = imageLargelUrl;
	}	
	public String getCommentsUrl() {
		return commentsUrl;
	}

	public void setCommentsUrl(String commentsUrl) {
		this.commentsUrl = commentsUrl;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isFeature() {
		return feature;
	}

	public void setFeature(boolean feature) {
		this.feature = feature;
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
		Product other = (Product) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description="
				+ description + ", imageSmallUrl=" + imageSmallUrl
				+ ", imageLargelUrl=" + imageLargelUrl + ", commentsUrl="
				+ commentsUrl + ", category=" + category + "]";
	}
}
