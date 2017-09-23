package app.util;

import lombok.*;

import java.io.File;

public class Path {

    public static class System {
        @Getter
        @Setter
        public static String CONFIGURATION_FILES_PATH;
    }

    // The @Getter methods are needed in order to access
    // the variables from Velocity Templates
    public static class Web {
        @Getter public static final String ROOT = "/";
        @Getter public static final String INDEX = "/index/";
        @Getter public static final String LOGIN = "/login/";
        @Getter public static final String LOGOUT = "/logout/";
        @Getter public static final String BOOKS = "/books/";
        @Getter public static final String ONE_BOOK = "/books/:isbn/";
    }

    public static class Template {
        public final static String INDEX = "/velocity/index/index.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public final static String BOOKS_ALL = "/velocity/book/all.vm";
        public static final String BOOKS_ONE = "/velocity/book/one.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
        public static final String CONFIG_USER = "/velocity/configs/user.vm";
        public static final String CONFIG_CHANGEPASSWORD =  "/velocity/configs/changepassword.vm";
        public static final String SERVER_ERROR = "/velocity/serverError.vm";
    }

    public static class Configuration {
        public static final String USER = System.CONFIGURATION_FILES_PATH + File.separator + "userConfig.conf";
    }

}
