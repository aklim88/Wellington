package pl.klimczakowie.cpublication2.web.view.base.resources;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.Model;

public class StaticImage extends WebComponent {
	private static final String IMAGE_DIR = "../../internal/";
	private static final long serialVersionUID = 259926742342650016L;

	public StaticImage(String id, String image) {
		super(id, new Model<String>(image));
	}

	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		checkComponentTag(tag, "img");
		tag.put("src", IMAGE_DIR + getDefaultModelObjectAsString ());
	}

}