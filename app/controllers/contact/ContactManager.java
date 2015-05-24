package controllers.contact;

import models.Contact;
import play.data.Form;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Breadcrumb;
import utils.Email;
import email.SendEmail;

public class ContactManager extends Controller {

	private static String msg = "Contato";
	private static String msg1 = "Saiba mais";
	private static Form<Contact> contForm = Form.form(Contact.class);
	private static Promise<Result> result;

	public static Result learnMore() {
		return ok(views.html.contact.learn.render(msg1,
				Breadcrumb.getBreadcrumbs("Learn")));
	}

	public static Result create() {
		return ok(views.html.contact.contact.render(msg, contForm,
				Breadcrumb.getBreadcrumbs("Contact")));
	}

	public static Promise<Result> send() {
		Form<Contact> frmFromReq = Form.form(Contact.class).bindFromRequest();
		if (frmFromReq.hasErrors()) {
			flash("fail", "Erro ao tentar enviar o email!!");
			return F.Promise.pure(badRequest(views.html.contact.contact.render(
					msg, frmFromReq, Breadcrumb.getBreadcrumbs("Contact"))));
		}
		Contact contact = frmFromReq.get();
		if (!Email.validateEmail(contact.getEmail())) {
			flash("fail", "E-mail inválido!!");
			return F.Promise.pure(badRequest(views.html.contact.contact.render(
					msg, frmFromReq, Breadcrumb.getBreadcrumbs("Contact"))));
		}

		Runnable task = () -> {
			sendEmail2Sender(contact);
		};
		new Thread(task).start();

		flash("success", "Menssagem enviada com sucesso!");
		return F.Promise
				.pure(redirect(controllers.contact.routes.ContactManager
						.create()));
	}

	private static void sendEmail2Sender(final Contact contact) {
		SendEmail.newContact2Sender(contact);
		SendEmail.newContact(contact);
	}
}
