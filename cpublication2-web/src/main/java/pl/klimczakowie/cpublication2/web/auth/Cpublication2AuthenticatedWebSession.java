/**
 * 
 */
package pl.klimczakowie.cpublication2.web.auth;

import java.io.IOException;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.klimczakowie.cpublication2.model.Firm;
import pl.klimczakowie.cpublication2.persistence.Cpublication2Service;
import pl.klimczakowie.cpublication2.web.tooling.CriteriaSessionContainer;
import pl.klimczakowie.cpublication2.web.tooling.CriteriaSessionContainer.CriteriaContainer;
import pl.klimczakowie.cpublication2.web.view.base.BaseCleanPage;

/**
 * Session's class
 * 
 * @author Agnieszka Klimczak
 */
public class Cpublication2AuthenticatedWebSession extends AuthenticatedWebSession {
    private static final long serialVersionUID = 8468386138983584976L;
    private static final Logger LOG = LoggerFactory.getLogger(Cpublication2AuthenticatedWebSession.class);

    /**
     * Logged user
     */
    private User user;

    /**
     * User criterions
     */
    protected CriteriaSessionContainer criteria = new CriteriaSessionContainer();

	private transient Cpublication2Service perstitenceReference;
	private boolean initialized;

    public Cpublication2AuthenticatedWebSession(Request request) {
        super(request);
    }

    public void initWith(Cpublication2Service perstitenceReference) {
        this.perstitenceReference = perstitenceReference;
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * @see AuthenticatedWebSession#authenticate(String, String)
     */
    @Override
    public boolean authenticate(String username, String password) {
        try {
        	Firm firm = perstitenceReference.findFirmForUser(username, password);
        	if (firm == null) {
        		return false;
        	}

        	user = new User();
        	user.setLogin(firm.getLogin());
        	user.setPasswd(firm.getPasswd());
        	user.getRoles().add(Role.USER);
        	user.setFirmId(firm.getId());
            LOG.info(AuthLoggingConstatns.WEB_LOGGER_USER_LOGGED_IN, user);
            return true;
        } catch (Exception e) {
            LOG.error("authenticate", e);
        }

        LOG.info(AuthLoggingConstatns.WEB_LOGGER_USER_FAILED_LOGIN, username);
        return false;
    }

    public static class UserNamePasswordCallbackHandler implements CallbackHandler {
        private String _userName;
        private char[] _password;

        public UserNamePasswordCallbackHandler(String userName, char[] password) {
            _userName = userName;
            _password = password;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback && _userName != null) {
                    ((NameCallback)callback).setName(_userName);
                } else if (callback instanceof PasswordCallback && _password != null) {
                    ((PasswordCallback)callback).setPassword(_password);
                }
            }
        }
    }

    public <T> CriteriaContainer<T> getCriteriaForPage(Class<? extends BaseCleanPage> page, Class<T> criteriaMetaModel) {
        LOG.debug("Returning criterias for: " + page.getCanonicalName() + ", metamodel: " + criteriaMetaModel);
        return criteria.getCriteriaForPage(page, criteriaMetaModel);
    }

    /**
     * @see AuthenticatedWebSession#invalidate()
     */
    @Override
    public void invalidate() {
        LOG.info(AuthLoggingConstatns.WEB_LOGGER_USER_LOGGED_OUT, (user != null) ? user.toString() : "Anonymous");
        super.invalidate();
    }

    /**
     * User roles
     * 
     * @see AuthenticatedWebSession#getRoles()
     */
    @Override
    public Roles getRoles() {
        if (isSignedIn()) {
            List<String> roles = user.getRoles();
            return new Roles(roles.toArray(new String[roles.size()]));
        }
        return null;
    }

    /**
     * Return roles
     */
    public boolean hasRole(String role) {
        if (isSignedIn()) {
            return user.getRoles().contains(role);
        }
        return false;
    }

    /**
     * Check role
     * 
     * @param role
     * @return
     */
    public boolean hasAnyRole(String... roles) {
        if (roles.length != 0 && isSignedIn()) {
            for (String role : roles) {
                if (user.getRoles().contains(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return user's object
     * 
     * @return
     */
    public User getUser() {
        return user;
    }

	public void setUser(User user) {
		this.user = user;
	}
}
