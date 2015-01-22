package pl.klimczakowie.cpublication2.web.auth;

public interface AuthLoggingConstatns {
    public static final String USER_INFO = " {} ";

    public static final String PREFIX = "WELLINGTON WEB APP ";
    public static final String WEB_LOGGER_USER_FAILED_LOGIN = PREFIX + "failed login attempt on [{}].";
    public static final String WEB_LOGGER_USER_LOGGED_IN = PREFIX + USER_INFO + "logged in.";
    public static final String WEB_LOGGER_USER_LOGGED_OUT = PREFIX + USER_INFO + "logged out.";
    public static final String WEB_LOGGER_VIEW_PAGE = PREFIX + USER_INFO + "viewing page: {} page Authorization: {}, host: {}";
}
