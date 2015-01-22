package pl.klimczakowie.cpublication2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "___categorys")
public class Category implements Serializable {
	private static final long serialVersionUID = -2849150454420619181L;

	@Id
	@GeneratedValue
	@Column(name = "__UID_____categorys")
	private Long id;

	@Column(length = 256)
	private String category;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", category=" + category + "]";
	}

}
