package app.util;

import spark.Request;

public class RequestUtil {

    public static String getQueryLocale(Request request) {
        return request.queryParams("locale");
    }

    public static String getParamIsbn(Request request) {
        return request.params("isbn");
    }

    public static String getQueryUsername(Request request) {
        return request.queryParams("username");
    }

    public static String getQueryPassword(Request request) {
        return request.queryParams("password");
    }

    public static String getQueryLoginRedirect(Request request) {
        return request.queryParams("loginRedirect");
    }

    public static String getSessionLocale(Request request) {
        return request.session().attribute("locale");
    }

    public static String getSessionCurrentUser(Request request) {
        return request.session().attribute("currentUser");
    }

    // ============================ Config User
    public static String getQueryConfigUserName(Request request) {
        return request.queryParams("userName");
    }

    public static String getQueryConfigUserPassword(Request request) {
        return request.queryParams("password");
    }

    public static String getQueryConfigUserPasswordConfirm(Request request) {
        return request.queryParams("passwordConfirm");
    }

    public static String getQueryConfigUserType(Request request) {
        return request.queryParams("type");
    }

    public static String getQueryConfigUserEmails(Request request) {
        return request.queryParams("emails");
    }

    public static String getQueryConfigDeleteCmd(Request request) {
        return request.queryParams("delete");
    }


    public static boolean removeSessionAttrLoggedOut(Request request) {
        Object loggedOut = request.session().attribute("loggedOut");
        request.session().removeAttribute("loggedOut");
        return loggedOut != null;
    }

    public static String removeSessionAttrLoginRedirect(Request request) {
        String loginRedirect = request.session().attribute("loginRedirect");
        request.session().removeAttribute("loginRedirect");
        return loginRedirect;
    }

    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

}
