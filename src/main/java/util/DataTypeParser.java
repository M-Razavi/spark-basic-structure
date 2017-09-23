package util;

import org.apache.commons.lang3.StringUtils;

public class DataTypeParser {

	public static Integer tryParseInteger(String stringVal, Integer defaultValue) {
		int result = defaultValue;
		if (StringUtils.isNotBlank(stringVal)) {
			try {
				result = Integer.parseInt(stringVal);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
