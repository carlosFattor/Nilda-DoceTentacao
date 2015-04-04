package controllers.manager;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;

@With(PlayAuthenticatedSecured.class)
public class Manager extends Controller{

	public static Result index(){
		return ok(views.html.manager.index.render("Nilda - Doce & Tentação"));
	}	
}
