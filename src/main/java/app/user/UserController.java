package app.user;

import app.util.Path;
import app.util.ViewUtil;
import util.Logger;
import app.login.LoginController;

import org.apache.commons.codec.digest.Crypt;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

import static app.Application.userDao;
import static app.util.RequestUtil.*;

public class UserController {

	public static Route handleUserConfigPost = (Request request, Response response) -> {
		if (LoginController.ensureUserIsLoggedIn(request, response)) {
			Map<String, Object> model = new HashMap<>();

			String deleteId = getQueryConfigDeleteCmd(request);
			String command = null;
			String Pass = null;
			String PasswordConfirm = null;
			Boolean keyHashed = true;
			try {
				if (deleteId == null) {
					command = request.queryParams("command");
					Pass = request.queryParams("password").trim();
					PasswordConfirm = request.queryParams("passwordConfirm").trim();
					if (command.equals("Edit") && Pass.equals("****")) {
						Pass = request.queryParams("passwordtmp").trim();
						PasswordConfirm = Pass;
						keyHashed = false;
					}
				}
				if (deleteId != null) { // Del mode
					delUserConfig(deleteId);

				} else {// Add/Edit mode
					UserController.addUser(getQueryConfigUserName(request), Pass, PasswordConfirm,
							getQueryConfigUserType(request), getQueryConfigUserEmails(request), keyHashed);
				}
				model.put("actionSucceeded", true);
			} catch (Exception e) {
				Logger.log(e);
				model.put("actionFailed", true);
			}

			model.put("userConfigs", userDao.getAllUsers());
			return ViewUtil.render(request, model, Path.Template.CONFIG_USER);
		}
		return ViewUtil.serverError.handle(request, response);
	};

	public static Route serveUserConfigPage = (Request request, Response response) -> {
		if (LoginController.ensureUserIsLoggedIn(request, response)) {
			Map<String, Object> model = new HashMap<>();
			model.put("userConfigs", userDao.getAllUsers());
			model.put("passwordtmp", "");
			return ViewUtil.render(request, model, Path.Template.CONFIG_USER);
		}
		return ViewUtil.serverError.handle(request, response);
	};

	public static Route handleChangePasswordConfigPost = (Request request, Response response) -> {
		if (LoginController.ensureUserIsLoggedIn(request, response)) {
			Map<String, Object> model = new HashMap<>();
			String currentUserName = request.session().attribute("currentUser");
			User user = userDao.getUserByUsername(currentUserName);
			try {
				String oldPassword = user.getHashedPassword();
				String oldpass = generateHashedPassword(request.queryParams("oldpass"), oldPassword);

				if (!oldPassword.equals(oldpass)) {
					throw new Exception("Invalid Old Password");
				}
				UserController.addUser(getQueryConfigUserName(request), getQueryConfigUserPassword(request),
						getQueryConfigUserPasswordConfirm(request), getQueryConfigUserType(request),
						getQueryConfigUserEmails(request), true);

				model.put("actionSucceeded", true);
				model.put("message", "");
				model.put("userConfigs", user);
			} catch (Exception e) {
				model.put("message", e.getMessage());
				model.put("actionFailed", true);
			}
			model.put("userConfigs", user);
			return ViewUtil.render(request, model, Path.Template.CONFIG_CHANGEPASSWORD);
		}
		return ViewUtil.serverError.handle(request, response);
	};

	public static Route serveChangePasswordConfigPage = (Request request, Response response) -> {
		if (LoginController.ensureUserIsLoggedIn(request, response)) {
			String currentUserName = request.session().attribute("currentUser");
			User user = userDao.getUserByUsername(currentUserName);
			Map<String, Object> model = new HashMap<>();
			model.put("userConfigs", user);
			model.put("passwordtmp", "");
			return ViewUtil.render(request, model, Path.Template.CONFIG_CHANGEPASSWORD);
		}
		return ViewUtil.serverError.handle(request, response);

	};

	// Authenticate the user by hashing the inputted password using the stored
	// salt,
	// then comparing the generated hashed password to the stored hashed
	// password
	public static boolean authenticate(String username, String password) {
		if (username.isEmpty() || password.isEmpty()) {
			return false;
		}
		User user = userDao.getUserByUsername(username);
		if (user == null) {
			return false;
		}

		return checkPassword(password, user.getHashedPassword());
	}

	private static void delUserConfig(String deleteId) {

		User userToDelete = userDao.getUserByUsername(deleteId);
		userDao.delUserConfig(deleteId);

	}

	public static boolean hasAccess(String userName, String page) {
		User user = userDao.getUserByUsername(userName);
		if (user.getType().equals(UserType.ADMIN)) {
			// return AccessPath.ADMIN_ACCESS.contains(page);
			return true;
		} else if (user.getType().equals(UserType.USER)) {
			return AccessPath.USER_ACCESS.contains(page);
		}
		return false;
	}

	public static boolean hasAccess(String userName, String userType, String page) {
		if (userType.equalsIgnoreCase(UserType.ADMIN.toString())) {
			return true;
		} else if (userType.equalsIgnoreCase(UserType.USER.toString())) {
			if (AccessPath.USER_ACCESS.contains(page)) {
				return true;

			} else {
				String[] urlParts = page.trim().split("/");
				// check for path with parameter
				for (String path : AccessPath.USER_ACCESS) {
					if (path.contains("/" + urlParts[1] + "/:") && urlParts.length == 3) {
						return true;
					}
				}

			}
		}
		return false;
	}

	/**
	 * Check that entered password is equal with stored password
	 *
	 * @param password
	 * @param user
	 * @return
	 */
	private static boolean checkPassword(String password, String hashedPassword) {
		String tmpHashedPassword = generateHashedPassword(password, hashedPassword);
		return hashedPassword.equalsIgnoreCase(tmpHashedPassword);
	}

	/**
	 * Encrypt password with Sha2Crypt.sha512Crypt mix by random salt
	 * (B64.getRandomSalt(8))
	 * 
	 * @param password
	 * @return
	 */
	private static String generateHashedPassword(String password) {
		return Crypt.crypt(password);
	}

	/**
	 * Encrypt password with Sha2Crypt.sha512Crypt mix by given salt
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	private static String generateHashedPassword(String password, String salt) {
		return Crypt.crypt(password, salt);
	}

	/**
	 * Add new user or update existing user
	 *
	 * @param userName
	 * @param password
	 * @param confirmPassword
	 * @throws Exception
	 */
	public static void addUser(String userName, String password, String confirmPassword, String type, String emails,
			Boolean keyHashed) throws Exception {
		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("Error: password and confirm are not same.");
		}
		String hashedPassword = password;
		if (keyHashed) {
			hashedPassword = generateHashedPassword(password);
		}
		userDao.addUser(userName, "", hashedPassword, userDao.parseUserTypeString(type), emails);
	}

}
