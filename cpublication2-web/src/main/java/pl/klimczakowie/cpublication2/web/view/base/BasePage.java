package pl.klimczakowie.cpublication2.web.view.base;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.klimczakowie.cpublication2.web.auth.CpublicationSignInPage;
import pl.klimczakowie.cpublication2.web.auth.CpublicationSignOutPage;
import pl.klimczakowie.cpublication2.web.view.ContactPage;
import pl.klimczakowie.cpublication2.web.view.FirmEditPage;
import pl.klimczakowie.cpublication2.web.view.FirmListPage;
import pl.klimczakowie.cpublication2.web.view.FirmRegisterPage;
import pl.klimczakowie.cpublication2.web.view.HomePage;
import pl.klimczakowie.cpublication2.web.view.SearchPage;
import pl.klimczakowie.cpublication2.web.view.TermsPage;

/**
 * Klasa bazowa wszystkich stron HTML, loguje w logu jaki uzytkownik wchodzi na
 * strone
 * 
 * @author Agnieszka Klimczak
 */
@RequireHttps
public abstract class BasePage extends BaseCleanPage {
    private static final long serialVersionUID = 5393838756489254126L;

    public BasePage(final PageParameters parameters) {
        super(parameters);

        add(new BookmarkablePageLink<HomePage>("homepage", HomePage.class).setAutoEnable(true));
        add(new BookmarkablePageLink<SearchPage>("searchpage", SearchPage.class).setAutoEnable(true));
        add(new BookmarkablePageLink<FirmListPage>("firmListPage", FirmListPage.class).setAutoEnable(true));
        BookmarkablePageLink<FirmEditPage> myPage = new BookmarkablePageLink<FirmEditPage>("myPage", FirmEditPage.class);
        Label loggedUsername = new Label("loggedUsername", getUser() != null ? getUser().getLogin() : "");
        BookmarkablePageLink<CpublicationSignOutPage> logout = new BookmarkablePageLink<CpublicationSignOutPage>("logout", CpublicationSignOutPage.class);
        BookmarkablePageLink<CpublicationSignInPage> login = new BookmarkablePageLink<CpublicationSignInPage>("login", CpublicationSignInPage.class);
        BookmarkablePageLink<FirmRegisterPage> registerPage = new BookmarkablePageLink<FirmRegisterPage>("registerPage", FirmRegisterPage.class);
        add(new BookmarkablePageLink<TermsPage>("termspage", TermsPage.class).setAutoEnable(true));
        add(new BookmarkablePageLink<ContactPage>("contactpage", ContactPage.class).setAutoEnable(true));

        myPage.setAutoEnable(true);
        add(myPage);
        add(loggedUsername);
        logout.setAutoEnable(true);
        add(logout);
        login.setAutoEnable(true);
        add(login);
        registerPage.setAutoEnable(true);
        add(registerPage);
        if (session != null && !session.isSignedIn()) {
            loggedUsername.setVisibilityAllowed(false).setVisible(false);
            logout.setVisibilityAllowed(false).setVisible(false);
            myPage.setVisibilityAllowed(false).setVisible(false);
        }
        if (session != null && session.isSignedIn()) {
            login.setVisibilityAllowed(false).setVisible(false);
            registerPage.setVisibilityAllowed(false).setVisible(false);
        }
        add(new Label("pageTitle", getPageTitle()).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));

    }

    protected abstract String getPageTitle();
}
