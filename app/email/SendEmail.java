package email;

import models.Contact;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.twirl.api.Html;

public class SendEmail {
	
	private static String msg = "Enviando de email-. Contato";
	private static String emailAdmin = "nilda.docetentacao@gmail.com";
	
	public static void newContact(Contact contact){
		final Email email = new Email();
		
		email.setSubject("Email de contato");
		email.setFrom(contact.getEmail());
		email.addTo(emailAdmin);
		Html render = views.html.email.contact.render(msg, contact);
		email.setBodyText(contact.getName());
		email.setBodyHtml(render.body());

		MailerPlugin.send(email);
	}
	
	public static void newContact2Sender(Contact contact){
		final Email email = new Email();
		
		email.setSubject("Status de Contato");
		email.setFrom(emailAdmin);
		email.addTo(contact.getEmail());
		Html render = views.html.email.contact_sender.render(msg, contact);
		email.setBodyText(contact.getName());
		email.setBodyHtml(render.body());

		MailerPlugin.send(email);
	}

}
