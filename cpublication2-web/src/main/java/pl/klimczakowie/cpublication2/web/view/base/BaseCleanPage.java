package pl.klimczakowie.cpublication2.web.view.base;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.auth.AuthLoggingConstatns;
import pl.klimczakowie.cpublication2.web.auth.Cpublication2AuthenticatedWebSession;
import pl.klimczakowie.cpublication2.web.auth.Role;
import pl.klimczakowie.cpublication2.web.auth.User;

/**
 * Klasa bazowa wszystkich stron HTML, loguje w logu jaki uzytkownik wchodzi na
 * strone
 * 
 * @author Agnieszka Klimczak
 */
@RequireHttps
public abstract class BaseCleanPage extends WebPage {
    private static final long serialVersionUID = 5393838756489254126L;

    private User user;
    private transient Logger log;

    protected transient boolean hasUserRole;
    protected transient boolean hasAnyRole;

    protected transient Cpublication2AuthenticatedWebSession session;
    protected transient String hostname = "Unknown";

    public BaseCleanPage(final PageParameters parameters) {
        super(parameters);

        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }

        session = getSession();
        hasUserRole = session.hasAnyRole(Role.USER);
        hasAnyRole = (hasUserRole);

        logPageView(getClass(), getUser());
    }
    //zakomentować poniższe na serwerze
    //
    //
//    @Override
//    public void renderHead(IHeaderResponse response) {
//        response.render(CssHeaderItem.forReference(CSSResourceLocator.getMainCss()));
//    }


    public User getUser() {
        if (user == null) {
            user = getSession().getUser();
        }
        return user;
    }

    public void setUser(User user) {
    	this.user = user;
    	getSession().setUser(user);
    }

    public Logger getLogger() {
        if (log == null) {
            synchronized (this) {
                if (log == null) {
                    log = LoggerFactory.getLogger(getClass());
                }
            }
        }
        return log;
    }

    /**
     * @see org.apache.wicket.markup.html.WebPage#getSession
     */
    @Override
    public Cpublication2AuthenticatedWebSession getSession() {
        Session session = super.getSession();
        if (session instanceof Cpublication2AuthenticatedWebSession) {
            Cpublication2AuthenticatedWebSession cpublication2Session = (Cpublication2AuthenticatedWebSession)session;
            if (!cpublication2Session.isInitialized()) {
            	cpublication2Session.initWith(getPersistence());
            }
            return cpublication2Session;
        }
        return null;
    }

    protected abstract Cpublication2Service getPersistence();

	public void logPageView(Class<?> loggedClazz, User user) {
        AuthorizeInstantiation annotation = loggedClazz.getAnnotation(AuthorizeInstantiation.class);
        String pageAuthorizeInstantiation = annotation != null ? Arrays.deepToString(annotation.value()) : "AnonymouseInstantiation";

        getLogger().info(AuthLoggingConstatns.WEB_LOGGER_VIEW_PAGE,
                         new String[] {user != null ? user.getLogin() : "Anonymous", loggedClazz.getCanonicalName(), pageAuthorizeInstantiation, hostname});
    }
}
