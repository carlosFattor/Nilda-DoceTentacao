package controllers.manager;

import java.util.List;
import java.util.Optional;

import models.User;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;
import service.UserServiceImp;
import utils.CryptSHA1;

@With(PlayAuthenticatedSecured.class)
public class ManagerUser extends Controller{
	
	private static Logger LOGGER = LoggerFactory.getLogger(ManagerUser.class.getName());
	private static List<User> users;
	private static String msg1="Lista de usuários";
	private static String msg2="Cadastro de usuários";
	private static Form<User> userForm = Form.form(User.class);
	private static User user;	
	
	@Transactional(readOnly = true)
	public static Result list(){
		users = new UserServiceImp(User.class).findAll();
		return ok(views.html.manager.user.User_list.render(msg1, users));
	}
	
	@Transactional(readOnly = true)
	public static Result save(){
		return ok(views.html.manager.user.user_save.render(msg2, userForm));
	}
	
	@Transactional(readOnly = true)
	public static Result updateFind(Long id){
		user = userFind(id);
		return ok(views.html.manager.user.user_save.render(msg2, userForm.fill(user)));
	}
	
	@Transactional
	public static Result update(){
		Form<User> formFromRequest = userForm.bindFromRequest();
		
		if(formFromRequest.hasErrors())return badRequest(views.html.manager.user.user_save.render(msg2, formFromRequest));
		
		User user = formFromRequest.get();
		Optional<String> pass = CryptSHA1.cipher(user.getPassword());
		
		if(pass.isPresent()){
			user.setPassword(CryptSHA1.cipher(user.getPassword()).get());
			try {
				user = new UserServiceImp(User.class).save(user);
				flash("success", "Success on save/update user");
			} catch (HibernateException e) {
				LOGGER.warn("Error on try update/save {0}", user.getName());
				flash("fail","error trying save/update user");
				return badRequest(views.html.manager.user.user_save.render(msg2, formFromRequest));
			}
		} else{
			flash("fail","error trying save/update user");
		}
		return redirect(routes.ManagerUser.list());
	}
	
	@Transactional
	public static Result delete(Long id){
		user = userFind(id);
		try {
			new UserServiceImp(User.class).remove(user);
			flash("success", "delete user ok");
		} catch (Exception e) {
			flash("fail", "error on try delete user");
		}
		return list();
	}
	
	private static User userFind(Long id){
		return new UserServiceImp(User.class).find(id).orElse(new User());
	}
}
