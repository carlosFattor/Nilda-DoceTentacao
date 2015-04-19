package controllers.manager;

import image.ScalePane;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import models.Gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import security.PlayAuthenticatedSecured;
import service.GalleryServiceImp;
import utils.CleanCache;
import utils.S3File;

@With(PlayAuthenticatedSecured.class)
public class ManagerGallery extends Controller {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ManagerGallery.class.getName());
	private static String msg1 = "Galeria";
	private static String msg2 = "Salvar item para galeria";
	private static List<Gallery> gals;
	private static Form<Gallery> galleryForm = Form.form(Gallery.class);
	private static String pathSmall = "public/images/gallery/small/";
	private static String pathLarge = "public/images/gallery/large/";

	@Transactional(readOnly = true)
	public static Result galleryList() {
		gals = new GalleryServiceImp(Gallery.class).findAll();
		return ok(views.html.manager.gallery.gallery_list.render(msg1, gals));
	}

	@Transactional
	public static Result save() {
		return ok(views.html.manager.gallery.gallery_save.render(msg2,
				galleryForm));
	}

	@Transactional
	public static Result update() 
			throws IOException, IllegalStateException {
		
		Form<Gallery> formFromRequest = galleryForm.bindFromRequest();

		if (formFromRequest.hasErrors())
			return badRequest(views.html.manager.gallery.gallery_save.render(
					msg2, formFromRequest));

		Gallery gal = formFromRequest.get();
		Http.MultipartFormData.FilePart uploadFilePart = request().body()
				.asMultipartFormData().getFile("image");

		final String name = System.currentTimeMillis() + "_"
				+ uploadFilePart.getFilename();
		S3File s3File = new S3File();
		gal.setImage(name);
		gal.setUrlLarge(s3File.getPartiaPath() + pathLarge + name);
		gal.setUrlSmall(s3File.getPartiaPath() + pathSmall + name);
		gal = new GalleryServiceImp(Gallery.class).save(gal);

		flash("success", "update/save category OK");

		LOGGER.info("Success on update object {0}", gal.getName());

		CleanCache.invalidate("GalList");

		Runnable task = () -> {	
				resizeImage(name, uploadFilePart.getFile()); 
			};
		new Thread(task).start();
		
		return redirect(routes.ManagerGallery.galleryList());
	}

	private static void resizeImage(String nameFile,final File file)
			throws IllegalStateException  {
		S3File s3FileLarge = new S3File()
				.setPath(pathLarge)
				.setNameFile(nameFile)
				.setFile(file).save();

		try {
			ScalePane scalePane = new ScalePane(file);
			File outputFile = File.createTempFile(nameFile, ".tmp");
			ImageIO.write(scalePane.getImage(), "jpg", outputFile);
			S3File s3FileSmall = new S3File()
				.setPath(pathSmall)
				.setNameFile(nameFile)
				.setFile(outputFile).save();

		} catch (IllegalStateException e) {
			LOGGER.warn("Impossible save image", e);
			throw new IllegalStateException("");
		} catch (IOException e) {
			LOGGER.warn("Impossible find file", e);
		}
	}

	@Transactional
	public static Result delete(Long id) {
		Optional<Gallery> gal = new GalleryServiceImp(Gallery.class).find(id);
		if (gal.isPresent()) {
			deleteGalleryImageSmall(gal.get().getImage());
			deleteGalleryImageLarge(gal.get().getImage());
			new GalleryServiceImp(Gallery.class).remove(gal.get());
			flash("success", "delete gallery OK");
		} else {
			flash("fail", "error trying delete gallery");
		}
		return redirect(routes.ManagerGallery.galleryList());
	}

	private static void deleteGalleryImageSmall(String name) {
		S3File s3FileSmall = new S3File()
		.setPath(pathSmall)
		.setNameFile(name)
		.delete();
	}

	private static void deleteGalleryImageLarge(String name) {
		S3File s3FileSmall = new S3File()
		.setPath(pathLarge)
		.setNameFile(name)
		.delete();
	}

	@Transactional
	public static Result findToUpdate(Long id) {

		Optional<Gallery> gal = new GalleryServiceImp(Gallery.class).find(id);
		if (gal.isPresent()) {
			Form<Gallery> formFromRequest = galleryForm.fill(gal.get());
			return ok(views.html.manager.gallery.gallery_save.render(msg2,
					formFromRequest));
		} else {
			flash("fail", "error trying find gallery");
			return redirect(routes.ManagerGallery.galleryList());
		}
	}
}
