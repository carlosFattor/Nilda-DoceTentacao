package controllers.manager;

import image.ScalePane;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.UIManager;
import models.Gallery;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;
import service.GalleryServiceImp;
import utils.CleanCache;

@With(PlayAuthenticatedSecured.class)
public class ManagerGallery extends Controller{

	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerGallery.class.getName()); 
	private static String msg1="Galeria";
	private static String msg2 = "Salvar item para galeria";
	private static List<Gallery> gals;
	private static Form<Gallery> galleryForm = Form.form(Gallery.class);
	private static String path = "public/images/gallery";
	
	@Transactional(readOnly = true)
	public static Result galleryList(){
		gals = new GalleryServiceImp(Gallery.class).findAll();
		return ok(views.html.manager.gallery.gallery_list.render(msg1, gals));
	}
	
	@Transactional
	public static Result save(){
		return ok(views.html.manager.gallery.gallery_save.render(msg2, galleryForm));
	}
	
	@Transactional
	public static Result update(){
		Form<Gallery> formFromRequest = galleryForm.bindFromRequest();
		
		if(formFromRequest.hasErrors()) return badRequest(views.html.manager.gallery.gallery_save.render(msg2, formFromRequest));
		
		File gallery;
		Gallery gal = formFromRequest.get();
		
		try {
			gallery = saveCategoryImage();
			
		} catch (IOException e) {
			LOGGER.warn("Error on try Update {0}", gal.getName());
            flash("fail", "error trying update gallery");
            return badRequest(views.html.manager.gallery.gallery_save.render(msg2, formFromRequest));
		}
		gal.setImage(gallery.getName());
		gal = new GalleryServiceImp(Gallery.class).save(gal);
		flash("success", "update/save category OK");
        LOGGER.info("Success on update object {0}", gal.getName());
        CleanCache.invalidate("GalList");
        resizeImage(gal.getImage());
		return redirect(routes.ManagerGallery.galleryList());
	}
	
	private static File saveCategoryImage() throws IOException{
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart filePart = body.getFile("image");
    	File category = filePart.getFile();
    	File destination = fileToDestination(filePart);
    	FileUtils.moveFile(category, destination);
    	
    	return destination;
    }
	
	private static void resizeImage(String nameFile){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				JFrame frame = new JFrame("Testing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new ScalePane(path.concat("/large/").toString(), nameFile));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
			}
		});
	}
	
	private static File fileToDestination(FilePart filePart) {
		return new File(path+"/large/", System.currentTimeMillis()+"_"+filePart.getFilename());
	}
	
	@Transactional
	public static Result delete(Long id){
		Optional<Gallery> gal = new GalleryServiceImp(Gallery.class).find(id);
		if(gal.isPresent()){
			deleteGalleryImageSmall(gal.get().getImage());
			deleteGalleryImageLarge(gal.get().getImage());
			new GalleryServiceImp(Gallery.class).remove(gal.get());
			flash("success", "delete gallery OK");
		}else{
			flash("fail", "error trying delete gallery");
		}
		return redirect(routes.ManagerGallery.galleryList());
	}
	
	private static void deleteGalleryImageSmall(String name){
    	File file = new File(path+"/small/"+name);
    	file.delete();
    }
	
	private static void deleteGalleryImageLarge(String name){
    	File file = new File(path+"/large/"+name);
    	file.delete();
    }
	
	@Transactional
	public static Result findToUpdate(Long id){
		
		Optional<Gallery> gal = new GalleryServiceImp(Gallery.class).find(id);
		if(gal.isPresent()){
			Form<Gallery> formFromRequest = galleryForm.fill(gal.get());
			return ok(views.html.manager.gallery.gallery_save.render(msg2, formFromRequest));
		}else{
			flash("fail", "error trying find gallery");
			return redirect(routes.ManagerGallery.galleryList());
		}
	}
}
