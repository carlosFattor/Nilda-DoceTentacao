package controllers;

import java.util.List;
import java.util.Optional;

import models.Product;
import play.cache.Cached;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import service.ProductServiceImp;
import utils.Breadcrumb;

public class Application extends Controller {

	private static Optional<List<Product>> prods;
	
	@Cached(key="FeatureList", duration=3600)
	@Transactional(readOnly = true)
    public static Result index() {
    	prods = new ProductServiceImp(Product.class).findBooleanCond("feature", Boolean.TRUE);
        return ok(views.html.index.render("Nilda - Doce&Tentação.", Breadcrumb.getBreadcrumbs("Home"), prods.get()));
    }
}
