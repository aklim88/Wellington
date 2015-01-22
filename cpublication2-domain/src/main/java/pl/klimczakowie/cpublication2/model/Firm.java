package pl.klimczakowie.cpublication2.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "___companys")
public class Firm implements Serializable {
	private static final long serialVersionUID = -8942775738246594030L;

	@Id
	@Column(name = "__UID_____companys")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "data_importu")
	@NamedCriteria("Date of import")
	private Date dataImportu;

	@Column(name = "name_of_source", length = 256)
	private String nameOfSource;

	@Column(name = "attribs", length = 32)
	private String attribs;

	@Column(length = 64)
	private String login;

	@Column(length = 64)
	private String passwd;

	@Column(name = "trade_mark_name", length = 256)
	@NamedCriteria("Trademark name")
	private String tradeMarkName;

	@Column(name = "trade_mark_no", length = 256)
	@NamedCriteria("Trademark no")
	private String tradeMarkNo;

	@Column(name = "date_of_publication")
	@NamedCriteria("Date of publication")
	private Date dateOfPublication;

	@Column(name = "nice_classification", length = 256)
	private String niceClassification;

	@Column(name = "status_of_trade_mark", length = 256)
	@NamedCriteria("Status of rademark")
	private String statusOfTrademark;

	@Column(name = "name", length = 512)
	@NamedCriteria("Company name")
	private String name;

	@Column(name = "id_no", length = 32)
	private String idNo;

	@Column(name = "natural_or_legal_person", length = 32)
	private String naturalOrLegalPerson;

	@Column(name = "address", length = 512)
	@NamedCriteria("Street")
	private String street;

	@Column(name = "post_code", length = 16)
	@NamedCriteria("Post code")
	private String postCode;

	@Column(name="town", length = 64)
	@NamedCriteria("City")
	private String city;

	@Column(length = 32)
	@NamedCriteria("Country")
	private String country;

	@Column(length = 64)
	private String phone;

	@Column(length = 64)
	private String fax;

	@Column(length = 64)
	private String email;

	@Column(length = 64)
	@NamedCriteria("Web page")
	private String homepage;

	@Column(length = 64)
	private String category;

	@Column(length = 1024)
	private String description;

	@Column(length = 16)
	private String version;

	@Column(length = 32)
	private String status;

	@OneToOne(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="logo")
	private Logo logo;

	@Column(length = 32)
	private String type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataImportu() {
		return dataImportu;
	}

	public void setDataImportu(Date dataImportu) {
		this.dataImportu = dataImportu;
	}

	public String getNameOfSource() {
		return nameOfSource;
	}

	public void setNameOfSource(String nameOfSource) {
		this.nameOfSource = nameOfSource;
	}

	public String getAttribs() {
		return attribs;
	}

	public void setAttribs(String attribs) {
		this.attribs = attribs;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getTradeMarkName() {
		return tradeMarkName;
	}

	public void setTradeMarkName(String tradeMarkName) {
		this.tradeMarkName = tradeMarkName;
	}

	public String getTradeMarkNo() {
		return tradeMarkNo;
	}

	public void setTradeMarkNo(String tradeMarkNo) {
		this.tradeMarkNo = tradeMarkNo;
	}

	public Date getDateOfPublication() {
		return dateOfPublication;
	}

	public void setDateOfPublication(Date dateOfPublication) {
		this.dateOfPublication = dateOfPublication;
	}

	public String getNiceClassification() {
		return niceClassification;
	}

	public void setNiceClassification(String niceClassification) {
		this.niceClassification = niceClassification;
	}

	public String getStatusOfTrademark() {
		return statusOfTrademark;
	}

	public void setStatusOfTrademark(String statusOfTrademark) {
		this.statusOfTrademark = statusOfTrademark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getNaturalOrLegalPerson() {
		return naturalOrLegalPerson;
	}

	public void setNaturalOrLegalPerson(String naturalOrLegalPerson) {
		this.naturalOrLegalPerson = naturalOrLegalPerson;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public String getDescriptionBRed() {
		if (description == null) {
			return null;
		}
		return description.replace("\n", "<BR />");
	}
	

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Firm [id=" + id + ", dataImportu=" + dataImportu
				+ ", nameOfSource=" + nameOfSource + ", attribs=" + attribs
				+ ", login=" + login + ", passwd=" + passwd
				+ ", tradeMarkName=" + tradeMarkName + ", tradeMarkNo="
				+ tradeMarkNo + ", dateOfPublication=" + dateOfPublication
				+ ", niceClassification=" + niceClassification
				+ ", statusOfTrademark=" + statusOfTrademark + ", name=" + name
				+ ", idNo=" + idNo + ", naturalOrLegalPerson="
				+ naturalOrLegalPerson + ", street=" + street + ", postCode="
				+ postCode + ", city=" + city + ", country=" + country
				+ ", phone=" + phone + ", fax=" + fax + ", email=" + email
				+ ", homepage=" + homepage + ", category=" + category
				+ ", description=" + description + ", version=" + version
				+ ", status=" + status + ", type=" + type
				+ "]";
	}

	public Logo getLogo() {
		return logo;
	}

	public void setLogo(Logo logo) {
		this.logo = logo;
	}
	
}
