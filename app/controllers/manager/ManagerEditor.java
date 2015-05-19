package controllers.manager;

import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;
import security.PlayAuthenticatedSecured;

@With(PlayAuthenticatedSecured.class)
public class ManagerEditor extends Controller {

	public static Result create(){
		return ok(views.html.manager.ckeditor.cooking_tips_edit.render("teste", ""));
	}
}
