package pl.klimczakowie.cpublication2.web.view;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.validator.StringValidator;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.model.Logo;
import pl.klimczakowie.cpublication2.web.auth.Role;
import pl.klimczakowie.cpublication2.web.auth.User;
import pl.klimczakowie.cpublication2.web.tooling.MyFileUploadField;

public class FirmRegisterPage extends FirmManagement {
    private static final long serialVersionUID = 1898458116391158798L;
    private final FeedbackPanel feedback = new FeedbackPanel("feedback");

    public FirmRegisterPage() {
        Firm firm = new Firm();
        firm.setLogo(new Logo());
        add(feedback.setOutputMarkupId(true));
        add(new InputForm("inputForm", firm));
    }

    @Override
    protected String getPageTitle() {

        return "Register Company";
    }

    private class InputForm extends Form<Firm> {

        private static final long serialVersionUID = -6789693786570942596L;

        public InputForm(String id, Firm firm) {
            super(id, new CompoundPropertyModel<Firm>(firm));
            TextField<String> login = new TextField<String>("login");
            login.setRequired(true);
            login.add(new StringValidator(6, 20));
            add(login);
            PasswordTextField password = new PasswordTextField("passwd");
            password.setRequired(true);
            password.add(new StringValidator(6, 20));
            add(password);
            PasswordTextField cpassword = new PasswordTextField("confirmpassword", Model.of(""));
            cpassword.setRequired(true);
            add(cpassword);

            add(new TextField<String>("name").setRequired(true));
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

            add(new SaveAjaxButton("saveButton", this).setDefaultFormProcessing(true));
            add(new EqualPasswordInputValidator(password, cpassword));
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
                formFirm.setStatus("ok");
                formFirm = persistenceReference.zapiszEncje(formFirm);
                User user = new User();
                user.setLogin(formFirm.getLogin());
                user.setPasswd(formFirm.getPasswd());
                user.setFirmId(formFirm.getId());
                user.getRoles().add(Role.USER);
                setUser(user);
                setResponsePage(FirmEditPage.class);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(feedback);
            }
        }

    }

}
