package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidator {

    public static boolean isIpV4Valid(String ipAddress) {
	final String regex = "((^|\\.)((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]?\\d))){4}$";
	final Pattern pattern = Pattern.compile(regex);

	final Matcher matcher = pattern.matcher(ipAddress);
	return matcher.find();
    }

    public static boolean isRefresh4Valid(int value) {
	if (value < 0 || value > 99999)
	    return false;
	else
	    return true;
    }

    public static boolean isPort4Valid(String value) {
	if (Integer.parseInt(value) < 0 || Integer.parseInt(value) > 65535)
	    return false;
	else
	    return true;
    }

}
