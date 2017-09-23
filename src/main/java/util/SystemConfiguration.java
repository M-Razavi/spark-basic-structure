package util;

import com.google.common.io.Files;

import java.io.*;
import java.util.Properties;


public class SystemConfiguration {

    static String filePathMain;
    static boolean isFileExist = true;

    public static Properties getInstance() {
	return SingletonLazyHolder.INSTANCE;
    }

    public static Properties getInstance(String configFilePath) throws Exception {
	isFileExist = true;
	SingletonLazyHolder.getNewInstanceByParam(configFilePath);
	return SingletonLazyHolder.INSTANCE;
    }

    public static void save(Properties configurationFile, String configPath) {
	FileOutputStream os;
	try {
	    os = new FileOutputStream(configPath, false);
	    configurationFile.store(os, "Configuration File");
	    os.close();

	} catch (FileNotFoundException e) {
	    Logger.log(e);
	    e.printStackTrace();
	} catch (IOException e) {
	    Logger.log(e);
	    e.printStackTrace();
	}
    }

    private static class SingletonLazyHolder {
	private static Properties INSTANCE;

	public static void getNewInstanceByParam(String filePath) throws Exception {
	    filePathMain = filePath;

	    InputStream is = null;

	    File configFile = new File(filePathMain);
	    if (!configFile.exists()) {
		System.err.println("Error: Configuration file is not exist;");
		Files.createParentDirs(configFile);
		// configFile.mkdir();
		configFile.createNewFile();
		isFileExist = false;
	    } else {
		is = new FileInputStream(filePathMain);
	    }
	    Logger.log("Config file loaded: " + configFile.getAbsolutePath());
	    INSTANCE = new Properties();
	    if (isFileExist) {
		INSTANCE.load(is);
		is.close();
	    }
	}
    }

}
