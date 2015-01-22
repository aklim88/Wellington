package pl.klimczakowie.cpublication2.web.auth;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.value.ValueMap;

import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.view.FirmEditPage;
import pl.klimczakowie.cpublication2.web.view.base.BasePage;

public final class CpublicationSignInPage extends BasePage {
    private static final long serialVersionUID = 2942911155996131246L;

    @Named("cpublication2Persistence")
    @Inject
    private Cpublication2Service persistenceReference;

    /**
     * Constructor
     */
    public CpublicationSignInPage() {
        super(null);
        add(new Cpublication2SignInForm("signInForm"));
    }

    /**
     * Constructor
     * 
     * @param parameters
     *            Parameters to page
     */
    public CpublicationSignInPage(final PageParameters parameters) {
        super(null);
        add(new Cpublication2SignInForm("signInForm"));
    }

    public final class Cpublication2SignInForm extends Form<Void> {

        private static final long serialVersionUID = 149449232069607638L;
        private static final String USERNAME = "username";
        private static final String PASSWORD = "password";

        // El-cheapo model for form
        private final ValueMap properties = new ValueMap();

        /**
         * Constructor
         * 
         * @param id
         *            id of the form component
         */
        public Cpublication2SignInForm(final String id) {
            super(id);

            // Attach textfield components that edit properties map model
            add(new TextField<String>(USERNAME, new PropertyModel<String>(properties, USERNAME)));
            add(new PasswordTextField(PASSWORD, new PropertyModel<String>(properties, PASSWORD)));
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        public final void onSubmit() {
            // Get session info
            Cpublication2AuthenticatedWebSession session = getMySession();

            // Sign the user in
            if (session.signIn(getUsername(), getPassword())) {

                setResponsePage(FirmEditPage.class);

            } else {
                // Get the error message from the properties file associated
                // with the Component
                String errmsg = getString("loginError", null, "Unable to sign you in");

                // Register the error message with the feedback panel
                error(errmsg);
            }
        }

        /**
         * @return
         */
        private String getPassword() {
            return properties.getString(PASSWORD);
        }

        /**
         * @return
         */
        private String getUsername() {
            return properties.getString(USERNAME);
        }

        /**
         * @return
         */
        private Cpublication2AuthenticatedWebSession getMySession() {
            return (Cpublication2AuthenticatedWebSession) getSession();
        }
    }

    @Override
    protected String getPageTitle() {
        return "Sign in";
    }

    @Override
    protected Cpublication2Service getPersistence() {
        return persistenceReference;
    }
}
