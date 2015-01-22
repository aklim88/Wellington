package pl.klimczakowie.cpublication2.web.tooling;

import java.io.Serializable;
import java.util.List;

public class CriterionDisplay implements Serializable {
	private static final long serialVersionUID = -7979584936102532750L;

	private String fieldName;
	private String fieldDisplay;
	private List<String> availableCriterions;

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldDisplay() {
		return fieldDisplay;
	}
	public void setFieldDisplay(String fieldDisplay) {
		this.fieldDisplay = fieldDisplay;
	}
	public List<String> getAvailableCriterions() {
		return availableCriterions;
	}
	public void setAvailableCriterions(List<String> availableCriterions) {
		this.availableCriterions = availableCriterions;
	}

	
}
