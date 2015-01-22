package pl.klimczakowie.cpublication2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "___countrys")
public class Country implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1235682077774974704L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "__UID_____countrys")
	private Long id;

	@Column(length = 3)
	private String code;

	@Column(length = 32)
	private String country;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Country [id=" + id + ", code=" + code + ", country=" + country
				+ "]";
	}

	
}
