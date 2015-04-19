package controllers.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import service.UserServiceImp;
import utils.CryptSHA1;

public class ManagerLoggin extends Controller {

	private static Logger LOGGER = LoggerFactory.getLogger(ManagerLoggin.class
			.getName());

	public static Result login() {
		DynamicForm form = new DynamicForm().bindFromRequest();
		return ok(views.html.manager.login.render(form));
	}

	@Transactional(readOnly = true)
	public static Result logon() {
		DynamicForm form = new DynamicForm().bindFromRequest();
		String email = form.data().get("email");
		String pass = form.data().get("password");

		Optional<User> user = null;
		try {
			user = exist(email, CryptSHA1.cipher(pass).get());
		} catch (Exception e) {
			DynamicForm formError = form.fill(form.data());
			formError.reject("Login e/ou senha não existem");
			return forbidden(views.html.manager.login.render(formError));
		}

		session().put("email", email);
		return redirect(controllers.manager.routes.Manager.index());

	}

	public static Result logout() {
		session().clear();
		return redirect(controllers.manager.routes.ManagerLoggin.login());
	}

	private static Optional<User> exist(String email, String password)
			throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("email", email);
		param.put("password", password);
		Optional<List<User>> users = null;
		try {
			users = Optional.ofNullable(new UserServiceImp(User.class)
					.findByNamedQuery("usuario.findLogin", param));
			if (users.isPresent()) {
				return Optional.ofNullable(users.get().get(0));
			} else {
				throw new IllegalAccessError();
			}
		} catch (Exception e) {
			LOGGER.warn("Error on try find user");
			throw new Exception("", e);
		}
	}
}
