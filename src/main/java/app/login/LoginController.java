package app.login;

import app.util.Path;
import app.util.ViewUtil;
import ml.miron.captcha.image.Captcha;
import ml.miron.captcha.image.background.GradiatedBackground;
import ml.miron.captcha.image.producer.CurvedLineNoiseProducer;
import ml.miron.captcha.image.renderer.ColoredEdgesWordRenderer;
import util.Logger;
import app.user.UserController;

import org.apache.commons.codec.binary.Base64;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.Application.userDao;
import static app.util.RequestUtil.*;


public class LoginController {

    static final boolean hasCaptcha = true;

    public static Route serveLoginPage = (Request request, Response response) -> {
        return showLoginPage(request, null);
    };
    public static Route handleLoginPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();

        boolean isCaptchaCorrect = true;

        if (hasCaptcha) {
            isCaptchaCorrect = false;
            String captchaAnswer = request.session().attribute("captchaAnswer");
            if (captchaAnswer != null) {
                String captchaText = request.queryParams("captcha");
                isCaptchaCorrect = captchaAnswer.equalsIgnoreCase(captchaText);
            }
        }

        if (!UserController.authenticate(getQueryUsername(request), getQueryPassword(request)) || !isCaptchaCorrect) {
            model.put("authenticationFailed", true);
            model.put("isCaptchaCorrect", isCaptchaCorrect);
            Logger.log("Authentication Failed.");
            Logger.log("isCaptchaCorrect: " + isCaptchaCorrect);
            return showLoginPage(request, model);
        }

        model.put("authenticationSucceeded", true);
        Logger.log("Authentication Succeeded.");

        request.session().attribute("currentUser", getQueryUsername(request));
        request.session().attribute("currentUserType",
                userDao.getUserByUsername(getQueryUsername(request)).getType().toString());
        if (getQueryLoginRedirect(request) != null) {
            response.redirect(getQueryLoginRedirect(request));
        } else {
            response.redirect(Path.Web.ROOT);
        }
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };
    public static Route handleLogoutPost = (Request request, Response response) -> {
        request.session().removeAttribute("currentUser");
        request.session().removeAttribute("currentUserType");
        request.session().attribute("loggedOut", true);
        response.redirect(Path.Web.LOGIN);
        return null;
    };

    private static String showLoginPage(Request request, Map<String, Object> model) throws IOException {
        if (model == null) {
            model = new HashMap<>();
        }
        model.put("loggedOut", removeSessionAttrLoggedOut(request));
        model.put("loginRedirect", removeSessionAttrLoginRedirect(request));
        if (model.get("loginRedirect") == null) {
            model.put("loginRedirect", getQueryLoginRedirect(request));
        }

        if (hasCaptcha) {

            Captcha captcha = getNewCaptcha();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(captcha.getImage(), "png", outputStream);
            request.session().attribute("captchaAnswer", captcha.getAnswer());

            model.put("captchaImg", new String(Base64.encodeBase64(outputStream.toByteArray())));
        }

        return ViewUtil.render(request, model, Path.Template.LOGIN);
    }

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static boolean ensureUserIsLoggedIn(Request request, Response response) {
        if (request.session().attribute("currentUser") == null) {
            request.session().attribute("loginRedirect", request.pathInfo());
            response.redirect(Path.Web.LOGIN);
            return false;
        }
        if (!UserController.hasAccess(request.session().attribute("currentUser"),
                request.session().attribute("currentUserType"), request.pathInfo())) {
            response.redirect(Path.Web.LOGIN);
            return false;
        }
        return true;
    }

    ;

    private static Captcha getNewCaptcha() {
        List<Color> wordRendererColors = new ArrayList<Color>();
        List<Font> wordRendererFonts = new ArrayList<Font>();

        wordRendererFonts.add(new Font("Arial", Font.PLAIN, 40));
        wordRendererFonts.add(new Font("Arial", Font.ITALIC, 40));
        wordRendererFonts.add(new Font("Arial", Font.BOLD, 37));

        wordRendererColors.add(Color.BLACK);
        wordRendererColors.add(Color.RED);
        wordRendererColors.add(Color.BLUE);
        wordRendererColors.add(Color.YELLOW);
        wordRendererColors.add(Color.MAGENTA);
        wordRendererColors.add(Color.GREEN);
        wordRendererColors.add(Color.PINK);
        wordRendererColors.add(Color.WHITE);
        wordRendererColors.add(Color.CYAN);
        wordRendererColors.add(Color.ORANGE);

        Captcha captcha = new Captcha.Builder(250, 50).addBackground(new GradiatedBackground())
                .addText(new ColoredEdgesWordRenderer(wordRendererColors, wordRendererFonts, 1.0F))
                .addNoise(new CurvedLineNoiseProducer(Color.BLACK, 3.0F)).build();
        return captcha;
    }

}
