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

public class ManagerLoggin extends Controller{

	private static Logger LOGGER = LoggerFactory.getLogger(ManagerLoggin.class.getName());
	
	public static Result login(){
		DynamicForm form = new DynamicForm().bindFromRequest();
		return ok(views.html.manager.login.render(form));
	}
	
	@Transactional(readOnly = true)
	public static Result loga(){
		DynamicForm form = new DynamicForm().bindFromRequest();
		String email = form.data().get("email");
		String pass = form.data().get("password");
		
		Optional<User> user = exist(email, CryptSHA1.cipher(pass).get());
				
		
		if(user.isPresent()){
			session().put("email", email);
			return redirect(controllers.manager.routes.Manager.index());
		}else{
			DynamicForm formError = form.fill(form.data());
			formError.reject("Login e/ou senha não existem");
			return forbidden(views.html.manager.login.render(formError));
		}
	}
	
	public static Result logout(){
		session().clear();
		return redirect(controllers.manager.routes.ManagerLoggin.login());
	}
	
	private static Optional<User> exist(String email, String password){
		Map<String, Object> param = new HashMap<>();
		param.put("email", email);
		param.put("password", password);
		List<User> user = null;
		try {
			user = new UserServiceImp(User.class).findByNamedQuery("user.findLogin", param);
		} catch (Exception e) {
			LOGGER.warn("Error on try find user");
		}
		if(!user.isEmpty()){
			return Optional.ofNullable(user.get(0));
		} else{
			return Optional.empty();
		}
	}
}
