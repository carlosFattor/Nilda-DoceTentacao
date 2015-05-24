package controllers;

import java.util.List;
import java.util.Optional;

import models.EmailNews;
import models.Product;
import play.Routes;
import play.cache.Cached;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import service.NewsServiceImp;
import service.ProductServiceImp;
import utils.Breadcrumb;
import utils.Email;

public class Application extends Controller {

	private static Optional<List<Product>> prods;
	
	@Cached(key="FeatureList", duration=3600)
	@Transactional(readOnly = true)
    public static Result index() {
    	prods = new ProductServiceImp(Product.class).findBooleanCond("feature", Boolean.TRUE);
        return ok(views.html.index.render("Nilda - Doce&Tentação.", Breadcrumb.getBreadcrumbs("Home"), prods.get()));
    }
	
	@Transactional
	public static Result news(String email) {
		System.out.println(email);
		if (Email.validateEmail(email)) {
			new NewsServiceImp(EmailNews.class).save(new EmailNews(email, java.time.LocalDate.now()));
			flash("success", "E-mail enviado com sucesso!");
			return ok();
		}
		flash("fail", "Erro ao tentar enviar e-mail!");
		return badRequest();
	}

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(Routes.javascriptRouter("myJsRoutes",
				routes.javascript.Application.news()));
	}
}
