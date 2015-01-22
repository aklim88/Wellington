package pl.klimczakowie.cpublication2.web.view;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.lang.Bytes;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Logo;
import pl.klimczakowie.cpublication2.web.tooling.MyFileUploadField;

public class FirmEditPage extends FirmManagement {

    private static final long serialVersionUID = 8842111416309462961L;

    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    private final byte[] logo;

    public FirmEditPage(PageParameters parameters) {
        add(feedback.setOutputMarkupId(true));
        Firm firm = persistenceReference.pobierzEncje(Firm.class, getUser().getFirmId());
        logo = firm.getLogo() != null ? firm.getLogo().getLogo() : null;
        add(new InputForm("inputForm", firm));
    }

    @Override
    protected String getPageTitle() {

        return "Edit Company";
    }

    private class InputForm extends Form<Firm> {

        private static final long serialVersionUID = -6789693786570942596L;

        public InputForm(String id, Firm firm) {
            super(id, new CompoundPropertyModel<Firm>(firm));
            add(new Label("name"));
            add(new TextField<String>("street").setRequired(true));
            add(new TextField<String>("postCode").setRequired(true));
            add(new TextField<String>("city").setRequired(true));
            add(new CountryDropDownChoice("country").setRequired(true));
            add(new TextField<String>("phone").setRequired(true));
            add(new TextField<String>("fax").setRequired(false));
            add(new TextField<String>("homepage").setRequired(false));
            add(new TextField<String>("email").setRequired(false));
            add(new CategoryDropDownChoice("category").setRequired(true));
            TextArea<String> description = new TextArea<String>("description");
            description.setRequired(false);
            add(description);
            setMultiPart(true);
            setMaxSize(Bytes.kilobytes(64));
            add(new MyFileUploadField("logo.logo"));

            final Logo image = firm.getLogo();
            IResource imageResource = new DynamicImageResource() {
                private static final long serialVersionUID = -1473185395438905405L;

                @Override
                protected byte[] getImageData(IResource.Attributes attributes) {
                    return image != null ? image.getLogo() : null;
                }
            };
            add(new NonCachingImage("image.logo", imageResource).setVisibilityAllowed(image != null).setVisible(image != null));
            add(new SaveAjaxButton("saveButton", this).setDefaultFormProcessing(true));

        }

        private final class CountryDropDownChoice extends DropDownChoice<String> {

            private static final long serialVersionUID = 571822569466137797L;

            public CountryDropDownChoice(String id) {
                super(id, getCountries());
            }
        }

        private final class CategoryDropDownChoice extends DropDownChoice<String> {

            private static final long serialVersionUID = 571822569466137797L;

            public CategoryDropDownChoice(String id) {
                super(id, getCategories());
            }
        }

        private final class SaveAjaxButton extends AjaxButton {
            private static final long serialVersionUID = 2919263666105760806L;

            private SaveAjaxButton(String id, Form<?> form) {
                super(id, form);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Firm formFirm = (Firm) form.getDefaultModelObject();
                if (formFirm.getLogo() == null || formFirm.getLogo().getLogo() == null) {
                    if (formFirm.getLogo() == null) {
                        formFirm.setLogo(new Logo());
                    }
                    formFirm.getLogo().setLogo(logo);
                }
                persistenceReference.zapiszEncje(formFirm);
                setResponsePage(FirmViewPage.class);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }

    }

}
