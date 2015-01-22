package pl.klimczakowie.cpublication2.web.auth;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class CpublicationSignOutPage extends WebPage {
    private static final long serialVersionUID = 2594699517517122462L;

    public CpublicationSignOutPage(final PageParameters parameters) {
        super(parameters);
        getSession().invalidate();
        setResponsePage(getApplication().getHomePage());
    }
}
