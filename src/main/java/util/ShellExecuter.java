package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Mahdi on 1/2/2017.
 */
public class ShellExecuter {

    public static String executeCommand(String commandWindows, String commandLinux) {
	Process result = null;
	String command = null;
	try {
	    if (OsValidator.isWindows()) {
		command = commandWindows;
		Logger.log("command=" + command);

	    } else if (OsValidator.isUnix()) {
		command = commandLinux;
		Logger.log("command=" + command);
	    } else {
		Logger.log("result=Error: Not supported OS.");
		return "Error: Not supported OS.";
	    }

	    if (command != null && !command.trim().isEmpty()) {
		result = Runtime.getRuntime().exec(command);

		StringBuilder lines = new StringBuilder();

		if (result != null) {

		    try (BufferedReader error = new BufferedReader(new InputStreamReader(result.getErrorStream()))) {
			String thisLine;
			while ((thisLine = error.readLine()) != null) {
			    lines.append(thisLine + "\n");
			}
			Logger.log("error=" + lines.toString());
		    }

		    try (BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()));) {
			String thisLine;

			while ((thisLine = output.readLine()) != null) {
			    lines.append(thisLine + "\n");
			}
			Logger.log("result=" + lines.toString());
		    }
		    
		    return lines.toString();
		}
	    }
	} catch (IOException e) {
	    Logger.log(e);
	    e.printStackTrace();
	}
	return null;
    }
    
    public static String executeCommand(String[] commandWindows, String[] commandLinux) {
      Process result = null;
      String[] command = null;
      try {
          if (OsValidator.isWindows()) {
          command = commandWindows;
          Logger.log("command=" + Arrays.toString(command));

          } else if (OsValidator.isUnix()) {
          command = commandLinux;
          Logger.log("command=" + Arrays.toString(command));
          } else {
          Logger.log("result=Error: Not supported OS.");
          return "Error: Not supported OS.";
          }

          if (command != null && command.length >0) {
          result = Runtime.getRuntime().exec(command);

          StringBuilder lines = new StringBuilder();

          if (result != null) {

              try (BufferedReader error = new BufferedReader(new InputStreamReader(result.getErrorStream()))) {
              String thisLine;
              while ((thisLine = error.readLine()) != null) {
                  lines.append(thisLine + "\n");
              }
              Logger.log("error=" + lines.toString());
              }

              try (BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()));) {
              String thisLine;

              while ((thisLine = output.readLine()) != null) {
                  lines.append(thisLine + "\n");
              }
              Logger.log("result=" + lines.toString());
              }
              
              return lines.toString();
          }
          }
      } catch (IOException e) {
          Logger.log(e);
          e.printStackTrace();
      }
      return null;
      }

}
