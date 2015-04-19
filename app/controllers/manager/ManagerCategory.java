package controllers.manager;

import java.util.List;

import models.Category;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;
import service.CategoryServiceImp;
import utils.CleanCache;
import utils.S3File;

@With(PlayAuthenticatedSecured.class)
public class ManagerCategory extends Controller {

	private static List<Category> cats;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ManagerCategory.class.getName());
	private static Category cat;
	private static Form<Category> categoryForm = Form.form(Category.class);
	private static String msg1 = "lista de categorias";
	private static String msg2 = "Criar categoria";
	private static String path = "public/images/category/";

	@Transactional(readOnly = true)
	public static Result categoryList() {
		cats = new CategoryServiceImp(Category.class).findAll();
		return ok(views.html.manager.category.category_list.render(msg1, cats));
	}

	@Transactional(readOnly = true)
	public static Result saveCategory() {
		return ok(views.html.manager.category.category_save.render(msg2,
				categoryForm));
	}

	@Transactional(readOnly = true)
	public static Result categoryUpdateFind(Long id) {
		cat = new CategoryServiceImp(Category.class).find(id).orElse(
				new Category());
		return ok(views.html.manager.category.category_save.render(msg2,
				categoryForm.fill(cat)));
	}

	@Transactional
	public static Result categoryUpdate() {
		Form<Category> formFromRequest = categoryForm.bindFromRequest();

		if (formFromRequest.hasErrors())
			return badRequest(views.html.manager.category.category_save.render(
					msg2, formFromRequest));
		Category cat = new Category();

		try {
			cat = formFromRequest.get();
			
			S3File s3File = saveS3();
			
			cat.setImage(s3File.getOnlyFileName());
			cat.setUrl(s3File.getUrl().toString());
			cat = new CategoryServiceImp(Category.class).save(cat);
			
			CleanCache.invalidate("CatList");
			
			flash("success", "update/save category OK");
			LOGGER.info("Success on update object {0}", cat.getName());
			
			return redirect(routes.ManagerCategory.categoryList());

		} catch (HibernateException e) {
			LOGGER.warn("Error on try Update ");
			flash("fail", "error trying update category");
			return badRequest(views.html.manager.category.category_save.render(
					msg2, formFromRequest));
		} catch (IllegalStateException e) {
			LOGGER.warn("Error on upload file ");
			flash("fail", "error trying upload file");
			return badRequest(views.html.manager.category.category_save.render(
					msg2, formFromRequest));
		} 
	}

	private static S3File saveS3() throws IllegalStateException {
		Http.MultipartFormData.FilePart uploadFilePart = request().body().asMultipartFormData().getFile("image");
		
		try {
			S3File s3File = new S3File()
						.setPath(path)
						.setNameFile(System.currentTimeMillis() + "_" + uploadFilePart.getFilename())
						.setFile(uploadFilePart.getFile())
						.save();

			return s3File;
		} catch (Exception e) {
			throw new IllegalStateException("Impossible save image.");
		}
	}

	private static Category findCat(Long id) {
		return new CategoryServiceImp(Category.class).find(id).orElse(
				new Category());
	}

	private static void catDelete(Category cat) {
		S3File s3File = new S3File()
						.setPath(path)
						.setNameFile(cat.getImage())
						.delete();
		new CategoryServiceImp(Category.class).remove(cat);
	}

	@Transactional
	public static Result categoryDelete(Long id) {
		cat = findCat(id);
		if (cat.getProducts().isEmpty()) {
			catDelete(cat);
			findAll();
			flash("success", "Category deleted");
			return ok(views.html.manager.category.category_list
					.render("", cats));
		} else {
			flash("fail",
					"Cannot delete, exist one product saved on this category.");
			findAll();
			return badRequest(views.html.manager.category.category_list.render(
					"", cats));
		}
	}

	private static void findAll() {
		cats = new CategoryServiceImp(Category.class).findAll();
	}

}