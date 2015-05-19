package controllers.category;

import java.util.*;

import models.Category;
import models.Gallery;
import models.Product;
import play.cache.Cached;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import service.CategoryServiceImp;
import service.GalleryServiceImp;
import service.ProductServiceImp;
import utils.Breadcrumb;

public class CategoryManager extends Controller {

	private static String msg1 = "Categorias";
	private static String msg2 = "Produtos";
	private static String msg3 = "Galeria";
	private static String msg4 = "Pesquisa de produtos";
	private static Optional<List<Category>> cats;
	private static Optional<List<Product>> prods;
	private static Optional<Category> cat;
	private static Optional<Product> prod;
	private static Optional<List<Gallery>> gal;
	private static Map<String, Object> param;

	
	@Cached(key="CatList", duration=3600)
	@Transactional(readOnly = true)
	public static Result list() {
		cats = Optional.of(new CategoryServiceImp(Category.class).findAll());
		return ok(views.html.category.list_category.render(msg1, cats.orElse(new ArrayList<Category>()), Breadcrumb.getBreadcrumbs("Category")));
	}

	@Transactional(readOnly = true)
	public static Result products(long idCategory){
		cat = new CategoryServiceImp(Category.class).find(idCategory);
		prods = Optional.of(cat.get().getProducts());
		return ok(views.html.product.list_products.render(msg2, prods.orElse(new ArrayList<Product>()), Breadcrumb.getBreadcrumbs("Product")));
	}
	
	@Transactional(readOnly = true)
	public static Result product(long idProduct){
		prod = new ProductServiceImp(Product.class).find(idProduct);
		return ok(views.html.product.product.render(prod.get().getName(), prod.orElse(new Product()), Breadcrumb.getBreadcrumbs("Product")));
	}

	@Cached(key="GalList", duration=3600)
	@Transactional(readOnly = true)
	public static Result gallery() {

		gal = Optional.of(new GalleryServiceImp(Gallery.class).findAll());


		Collections.shuffle(gal.get());


		return ok(views.html.gallery.gallery.render(msg3, gal.orElse(new ArrayList<Gallery>()), Breadcrumb.getBreadcrumbs("Gallery")));
	}
	
	@Transactional(readOnly = true)
	public static Result findProducts() {
		if (Form.form().bindFromRequest().get("search") != null) {
			String value = Form.form().bindFromRequest().get("search").toString().replace("'", " ");
			param = new HashMap<>();
			param.put("name", "%"+value+"%");
			
			try {
				prods = Optional.ofNullable(new ProductServiceImp(Product.class).findByNamedQuery("product.FindAllByName", param));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return ok(views.html.product.list_products.render(msg4, prods.orElse(new ArrayList<Product>()), Breadcrumb.getBreadcrumbs("Home")));
	}
}