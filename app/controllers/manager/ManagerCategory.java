package controllers.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import models.Category;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import security.PlayAuthenticatedSecured;
import service.CategoryServiceImp;
import utils.CleanCache;

@With(PlayAuthenticatedSecured.class)
public class ManagerCategory extends Controller{

    private static List<Category>cats;
    private final static Logger LOGGER = LoggerFactory.getLogger(ManagerCategory.class.getName()); 
    private static Category cat;
    private static Form<Category> categoryForm = Form.form(Category.class);
    private static String msg1="lista de categorias";
    private static String msg2="Criar categoria";
    private static String path = "public/images/category";
    
    @Transactional(readOnly = true)
    public static Result categoryList(){
        cats = new CategoryServiceImp(Category.class).findAll();
    	return ok(views.html.manager.category.category_list.render(msg1, cats));
    }
   
    
    @Transactional(readOnly = true)
    public static Result saveCategory(){
    	return ok(views.html.manager.category.category_save.render(msg2, categoryForm));
    }
    
    @Transactional(readOnly=true)
    public static Result categoryUpdateFind(Long id){
    	cat = new CategoryServiceImp(Category.class).find(id).orElse(new Category());
    	return ok(views.html.manager.category.category_save.render(msg2, categoryForm.fill(cat)));
    }
    
    @Transactional
    public static Result categoryUpdate(){
        Form<Category> formFromRequest = categoryForm.bindFromRequest();
        
        if(formFromRequest.hasErrors())return badRequest(views.html.manager.category.category_save.render(msg2, formFromRequest));
        
        try{
        	Category cat = formFromRequest.get();
        	File category = saveCategoryImage();
        	cat.setImage(category.getName());
            cat = new CategoryServiceImp(Category.class).save(cat);
            flash("success", "update/save category OK");
            LOGGER.info("Success on update object {0}", cat.getName());
            CleanCache.invalidate("CatList");
            return redirect(routes.ManagerCategory.categoryList());
        } catch(HibernateException | IOException e){
            LOGGER.warn("Error on try Update {0}", cat.getName());
            flash("fail", "error trying update category");
            return badRequest(views.html.manager.category.category_save.render(msg2, formFromRequest));
        }
    }
    
    private static File saveCategoryImage() throws IOException{
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart filePart = body.getFile("image");
    	File category = filePart.getFile();
    	File destination = fileToDestination(filePart);
    	FileUtils.moveFile(category, destination);
    	return destination;
    }
    
    private static void deleteCategoryImage(String name){
    	File file = new File(path+"/"+name);
    	file.delete();
    }
    
    private static File fileToDestination(FilePart filePart) {
		return new File(path+"/", System.currentTimeMillis()+"_"+filePart.getFilename());
	}

	private static Category findCat(Long id){
        return new CategoryServiceImp(Category.class).find(id).orElse(new Category());
    }
    
    private static void catDelete(Category cat){
    	deleteCategoryImage(cat.getImage());
    	new CategoryServiceImp(Category.class).remove(cat);
    }
    
    @Transactional
    public static Result categoryDelete(Long id){
    	cat = findCat(id);
    	if(cat.getProducts().isEmpty()){
    		catDelete(cat);
    		findAll();
    		flash("success","Category deleted");
    		return ok(views.html.manager.category.category_list.render("", cats));
    	}else{
    		flash("fail","Cannot delete, exist one product saved on this category.");
    		findAll();
    		return badRequest(views.html.manager.category.category_list.render("", cats));
    	}
    }
    
    private static void findAll(){
    	cats = new CategoryServiceImp(Category.class).findAll();
    }
    
}