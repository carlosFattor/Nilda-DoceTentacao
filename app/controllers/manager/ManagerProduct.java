package controllers.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.Category;
import models.Product;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;
import service.CategoryServiceImp;
import service.ProductServiceImp;
import utils.CleanCache;


@With(PlayAuthenticatedSecured.class)
public class ManagerProduct extends Controller {

	private static Optional<Product> prod;
	private static Optional<List<Product>> prods;
	private static List<Category>cats;
	private static Form<Product> productForm = Form.form(Product.class);
	private static String msg1 = "Lista de produtos.";
	private static String msg2 = "Criar produto.";

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ManagerProduct.class.getName());

	@Transactional(readOnly = true)
	public static Result productFind(Long id) {
		prod = findProd(id);
		return TODO;
	}

	@Transactional(readOnly = true)
	public static Result productList() {
		prods = Optional.ofNullable(new ProductServiceImp(Product.class).findAll());
		return ok(views.html.manager.product.product_list.render(msg1, prods.get()));
	}
	
	@Transactional(readOnly = true)
	public static Result saveProduct(){
		cats = new CategoryServiceImp(Category.class).findAll();
		Map<String, String>map = new HashMap<>();
		for(Category cat : cats){
			map.put(String.valueOf(cat.getId()), cat.getName());
		}
		CleanCache.invalidate("FeatureList");
		return ok(views.html.manager.product.product_save.render(msg2, productForm, map));
	}

	@Transactional(readOnly=true)
    public static Result productUpdateFind(Long id){
    	prod = findProd(id);
    	cats = new CategoryServiceImp(Category.class).findAll();
    	Map<String, String>map = new HashMap<>();
    	for(Category cat : cats){
			map.put(String.valueOf(cat.getId()), cat.getName());
		}
    	return ok(views.html.manager.product.product_save.render(msg2, productForm.fill(prod.get()), map));
    }
	
	@Transactional
	public static Result productUpdate() {
		Form<Product> formFromRequest = productForm.bindFromRequest();
		Product prod = new Product();
		Map<String, String>map = new HashMap<>();
		
		if (formFromRequest.hasErrors()){
			cats = new CategoryServiceImp(Category.class).findAll();
    		
    		for(Category cat : cats){
    			map.put(String.valueOf(cat.getId()), cat.getName());
    		}
			return badRequest(views.html.manager.product.product_save.render(msg2, productForm, map));
		}
		try{
			prod = formFromRequest.get();
			new ProductServiceImp(Product.class).save(prod);
			session("success", "Success on update product");
			return redirect(routes.ManagerProduct.productList());
		} catch (HibernateException e) {
			LOGGER.warn("Error on try update ");
			flash("fail", "error on trying update ");
			cats = new CategoryServiceImp(Category.class).findAll();
    		for(Category cat : cats){
    			map.put(String.valueOf(cat.getId()), cat.getName());
    		}
			return badRequest(views.html.manager.product.product_save.render(msg2, productForm, map));
		}
	}

	@Transactional
	public static Result productDelete(Long id) {
		Optional<Product> prod = findProd(id);
		if(prod.isPresent()){
			prodDelete(prod.get());
			flash("success","Product deleted");
		} else {
			flash("fail","Error on try delete product "+prod.get().getName());
		}
		return redirect(routes.ManagerProduct.productList());
	}

	private static Optional<Product> findProd(Long id) {
		return new ProductServiceImp(Product.class).find(id);
	}

	private static void prodDelete(Product prod) {
		try{
			new ProductServiceImp(Product.class).remove(prod);
		}catch(HibernateException e){
			LOGGER.warn("Error on try delete this product"+prod.getName());
		}
	}

}
