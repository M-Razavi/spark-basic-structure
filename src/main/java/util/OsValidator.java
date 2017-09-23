package util;


/**
 * Author: Domenico Monaco, Yong Mook Kim
 * <p>
 * Source: https://gist.github.com/kiuz/816e24aa787c2d102dd0
 * <p>
 * License: GNU v2 2014
 * <p>
 * Fork / Learned: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 */

public class OsValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {

	Logger.log(OS);

	if (isWindows()) {
	    Logger.log("This is Windows");
	} else if (isMac()) {
	    Logger.log("This is Mac");
	} else if (isUnix()) {
	    Logger.log("This is Unix or Linux");
	} else if (isSolaris()) {
	    Logger.log("This is Solaris");
	} else {
	    Logger.log("Your OS is not support!!");
	}
    }

    public static boolean isWindows() {
	return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
	return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
	return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
	return (OS.indexOf("sunos") >= 0);
    }

    public static String getOS() {
	if (isWindows()) {
	    return "win";
	} else if (isMac()) {
	    return "osx";
	} else if (isUnix()) {
	    return "uni";
	} else if (isSolaris()) {
	    return "sol";
	} else {
	    return "err";
	}
    }

}