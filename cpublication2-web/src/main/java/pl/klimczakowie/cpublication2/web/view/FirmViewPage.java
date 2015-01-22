package pl.klimczakowie.cpublication2.web.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Logo;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.view.base.BasePage;

public class FirmViewPage extends BasePage {
	private static final Logger LOG = LoggerFactory.getLogger(FirmViewPanel.class);

    private static final long serialVersionUID = -4925314496297679424L;
    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;

    public FirmViewPage() {
        super(null);
        add(feedback.setOutputMarkupId(true));
        Firm firm = persistenceReference.pobierzEncje(Firm.class, getUser().getFirmId());
        add(new InputForm("inputForm", firm));
    }

    private class InputForm extends Form<Firm> {
        private static final long serialVersionUID = 1997572509197780476L;

        public InputForm(String id, Firm firm) {
            super(id, new CompoundPropertyModel<Firm>(firm));
            final Logo logo = firm.getLogo();

            IResource imageResource = new DynamicImageResource() {
                private static final long serialVersionUID = -1473185395438905405L;

                @Override
                protected byte[] getImageData(IResource.Attributes attributes) {
                    return logo != null ? logo.getLogo() : null;
                }
            };

            add(new Label("name"));
            add(new Label("street"));
            add(new Label("postCode"));
            add(new Label("city"));
            add(new Label("country"));
            add(new Label("phone"));
            add(new Label("fax"));
            add(new Label("homepage"));
            add(new Label("email"));
            add(new Label("descriptionBRed").setEscapeModelStrings(false));
            add(new NonCachingImage("logo.logo", imageResource).setVisibilityAllowed(logo != null).setVisible(logo != null));

            add(new ExitAjaxButton("exitButton", this).setDefaultFormProcessing(true));

            add(new Image("map") {
				private static final long serialVersionUID = -4275917512711776325L;

				@Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    String src = getModelObject().getCountry() + ", " + getModelObject().getCity() + ", " + getModelObject().getPostCode() + ", " + getModelObject().getStreet();;
                    try {
						src = "http://maps.google.com/maps/api/staticmap?center=" + URLEncoder.encode(src, "UTF-8") + "&zoom=14&size=714x300&maptype=roadmap&markers=color:0xdab100|";
					} catch (UnsupportedEncodingException e) {
						LOG.error("Could not encode image url: " + src, e);
					}
                    tag.getAttributes().put("src", src);
                }
            });
        }

        private final class ExitAjaxButton extends AjaxButton {
            private static final long serialVersionUID = -4147490899188148760L;

            private ExitAjaxButton(String id, Form<?> form) {
                super(id, form);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(FirmListPage.class);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }
    }

    @Override
    protected String getPageTitle() {
        return "Company View";
    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }

}
