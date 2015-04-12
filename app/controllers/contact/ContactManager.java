package controllers.contact;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Contact;
import models.EmailNews;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import service.NewsServiceImp;
import utils.Breadcrumb;
import email.SendEmail;

public class ContactManager extends Controller {
	
	private static String msg = "Contato";
	private static String msg1= "Saiba mais";
	private static Form<Contact> contForm = Form.form(Contact.class);
	private static Promise<Result> result;

	private static Pattern pattern;
	private static Matcher matcher;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static Result learnMore(){
		return ok(views.html.contact.learn.render(msg1, Breadcrumb.getBreadcrumbs("Learn")));
	}
	
	@Transactional
	public static Result news(){
		if (Form.form().bindFromRequest().get("news") != null) {
			String email = Form.form().bindFromRequest().get("news");
			if(validateEmail(email)){
				new NewsServiceImp(EmailNews.class).save(new EmailNews(email, java.time.LocalDate.now()));
				flash("success", "Cadastro efetuado com sucesso!");
			} else {
				flash("successfail", "Email incorreto!");
			}
		}
		return redirect(controllers.routes.Application.index());
	}
	
	private static boolean validateEmail(String email){
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public static Result create(){
		return ok(views.html.contact.contact.render(msg, contForm, Breadcrumb.getBreadcrumbs("Contact")));
	}
	
	public static Promise<Result> send() throws IOException{
		Form<Contact> frmFromReq = Form.form(Contact.class).bindFromRequest();

		if(frmFromReq.hasErrors()){
			return F.Promise.pure(badRequest(views.html.contact.contact.render(msg, frmFromReq, Breadcrumb.getBreadcrumbs("Contact"))));
		}
		Contact contact = frmFromReq.get();
		if(validateEmail(contact.getEmail())){
			return F.Promise.pure(badRequest(views.html.contact.contact.render(msg, frmFromReq, Breadcrumb.getBreadcrumbs("Contact"))));
		}
		sendEmail2Sender(contact);
		
		flash("success", "Menssagem enviada com sucesso!");
		return result;
	}

	private static void sendEmail2Sender(Contact contact){
		Promise<Void> sendEmail2Sender = Promise.promise(new Function0<Void>() {
			@Override
			public Void apply() throws Throwable {
				SendEmail.newContact2Sender(contact);
				SendEmail.newContact(contact);
				return null;
			}
		});
		
		result = sendEmail2Sender.map(new Function<Void, Result>() {

			@Override
			public Result apply(Void arg0) throws Throwable {
				return redirect(controllers.contact.routes.ContactManager.create());
			}
		});
	}
}
