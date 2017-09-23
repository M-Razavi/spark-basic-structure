package app.user;

import app.util.Path;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import util.Logger;
import util.SystemConfiguration;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static app.util.Path.System.CONFIGURATION_FILES_PATH;


public class UserDao {

	private Map<String, User> userMap = new HashMap<>();
	private Properties userConfigurationFile;

	public UserDao() {
		try {
			userConfigurationFile = SystemConfiguration.getInstance(Path.Configuration.USER);
		} catch (Exception e) {
			Logger.log(e);
			e.printStackTrace();
		}
		userMap.put("admin",
				new User("admin", "",
						"$6$1Tfbn3AZ$qmpiP2fhq562C1DJR5Ag7oxcdwzoqBC0MaFhGqS3zwBR8OJcXZq0azR4nJIRGQWHDzqLWW3bUd6mZd4ZSfHrX/",
						UserType.ADMIN, new ArrayList<>()));
		readSettingsFromFile();
	}

	// private List<User> users = ImmutableList.of(
	// // Username Salt for hash Hashed password (the password is "password" for
	// all users)
	// new User("mahdi", "$2a$10$h.dl5J86rGH7I8bD9bZeZe",
	// "$2a$10$h.dl5J86rGH7I8bD9bZeZeci0pDt0.VwFTGujlnEaZXPf/q7vM5wO"),
	// new User("admin", "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe",
	// "$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEMLT83E1KDmUhCy"),
	// new User("root", "$2a$10$E3DgchtVry3qlYlzJCsyxe",
	// "$2a$10$E3DgchtVry3qlYlzJCsyxeSK0fftK4v0ynetVCuDdxGVl1obL.ln2"));

	public Iterable<User> getAllUsers() {
		try {
			userConfigurationFile = SystemConfiguration.getInstance(Path.Configuration.USER);
		} catch (Exception e) {
			Logger.log(e);
			e.printStackTrace();
		}
		readSettingsFromFile();

		return userMap.values();
	}

	public User getUserByUsername(String username) {
		getAllUsers();
		return userMap.values().stream().filter(b -> b.getUsername().equalsIgnoreCase(username)).findFirst()
				.orElse(null);
	}

	public Iterable<String> getAllUserNames() {
		getAllUsers();
		return userMap.values().stream().map(User::getUsername).collect(Collectors.toList());
	}

	private void readSettingsFromFile() {
		try {
			int counter = 0;
			while (true) {
				String keyUserName = "userName-" + (++counter);
				String keyUserSalt = "salt-" + (counter);
				String keyUserHashedPassword = "hashedPassword-" + (counter);
				String keyUserType = "type-" + (counter);
				String keyUserEmails = "emails-" + (counter);
				String userName, userSalt, userHashedPassword, userEmails;
				UserType userType = null;
				if (userConfigurationFile.containsKey(keyUserName)) {
					userName = userConfigurationFile.getProperty(keyUserName);
					userSalt = userConfigurationFile.getProperty(keyUserSalt);
					userHashedPassword = userConfigurationFile.getProperty(keyUserHashedPassword);
					try {
						userType = parseUserTypeString(userConfigurationFile.getProperty(keyUserType));
					} catch (Exception e) {
						Logger.log(e);
						e.printStackTrace();
					}
					userEmails = userConfigurationFile.getProperty(keyUserEmails);
					userEmails = userEmails.replace(" ", "");
					List<String> tmpEmailList = null;
					if (userEmails != null) {
						tmpEmailList = Arrays.asList(userEmails.split(","));
					}
					User tmpUser = new User(userName.toLowerCase(), userSalt, userHashedPassword, userType,
							tmpEmailList);
					userMap.put(userName, tmpUser);
				} else {
					break;
				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}

	}

	private void saveSettingsToFile() {
		SystemConfiguration.save(userConfigurationFile, Path.Configuration.USER);

		generateFtpUserForLanSide();
	}

	public void addUserConfig(User user) {
		userMap.put(user.getUsername(), user);
		int counter = 0;
		for (User userElement : userMap.values()) {
			String keyUserName = "userName-" + (++counter);
			String keyUserSalt = "salt-" + (counter);
			String keyUserHashedPassword = "hashedPassword-" + (counter);
			String keyUserType = "type-" + (counter);
			String keyUserEmails = "emails-" + (counter);

			userConfigurationFile.setProperty(keyUserName, userElement.getUsername());
			userConfigurationFile.setProperty(keyUserSalt, userElement.getSalt());
			userConfigurationFile.setProperty(keyUserHashedPassword, userElement.getHashedPassword());
			userConfigurationFile.setProperty(keyUserType, userElement.getType().toString());
			userConfigurationFile.setProperty(keyUserEmails, Joiner.on(',').join(userElement.getEmails()));
		}
		saveSettingsToFile();

	}

	public void addUser(String userName, String salt, String hashedPassword, UserType userType, String emails) {
		List<String> tmpEmailList = null;
		if (emails != null) {
			tmpEmailList = Arrays.asList(emails.split(","));
		}
		User user = new User(userName.toLowerCase(), salt, hashedPassword, userType, tmpEmailList);
		userMap.put(user.getUsername(), user);
		addUserConfig(user);

	}

	public void delUserConfig(String userName) {
		User user = getUserByUsername(userName);
		if (user != null) {
			userMap.remove(user.getUsername());
			userConfigurationFile.clear();

			int counter = 0;
			for (User userElement : userMap.values()) {
				String keyUserName = "userName-" + (++counter);
				String keyUserSalt = "salt-" + (counter);
				String keyUserHashedPassword = "hashedPassword-" + (counter);
				String keyUserType = "type-" + (counter);
				String keyUserEmails = "emails-" + (counter);

				userConfigurationFile.setProperty(keyUserName, userElement.getUsername());
				userConfigurationFile.setProperty(keyUserSalt, userElement.getSalt());
				userConfigurationFile.setProperty(keyUserHashedPassword, userElement.getHashedPassword());
				userConfigurationFile.setProperty(keyUserType, userElement.getType().toString());
				userConfigurationFile.setProperty(keyUserEmails, Joiner.on(',').join(userElement.getEmails()));
			}
			saveSettingsToFile();

		}
	}

	public UserType parseUserTypeString(String userTypeString) throws Exception {
		if (userTypeString.equalsIgnoreCase(UserType.ADMIN.toString())) {
			return UserType.ADMIN;
		} else if (userTypeString.equalsIgnoreCase(UserType.USER.toString())) {
			return UserType.USER;
		} else {
			throw new Exception("Error: undefined user type=" + userTypeString);
		}
	}

	private void generateFtpUserForLanSide() {

		String lanFtpPasswdFilePath = CONFIGURATION_FILES_PATH + File.separator + "lan" + File.separator + "etc"
				+ File.separator + "pure-ftpd" + File.separator + "pureftpd.passwd";

		File configFile = new File(lanFtpPasswdFilePath);
		if (!configFile.exists()) {
			System.err.println("Error: Configuration file is not exist;");
			try {
				Files.createParentDirs(configFile);
			} catch (IOException e) {
				Logger.log(e);
				e.printStackTrace();
			}
		}

		StringBuilder stringBuilderFtpPasswd = new StringBuilder();
		for (User user : userMap.values()) {
			stringBuilderFtpPasswd.append(user.getUsername());
			stringBuilderFtpPasswd.append(":");
			stringBuilderFtpPasswd.append(user.getHashedPassword());
			stringBuilderFtpPasswd.append(":1001:1002::/home/diode/content/ftps/./:::::::10485760:::::\n");
		}

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(lanFtpPasswdFilePath, false)))) {
			writer.write(stringBuilderFtpPasswd.toString());
		} catch (Exception e) {
			Logger.log(e);
			e.printStackTrace();
		}
	}
}
