package security;

import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class PlayAuthenticatedSecured extends Action.Simple{

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		String email = ctx.session().get("email");
		if(email != null){
			return delegate.call(ctx);
		}
		ctx.flash().put("nao_logado", "Para continuar, precisa estar logado.");
		
		return F.Promise.pure(redirect(controllers.manager.routes.ManagerLoggin.login()));
	}
}
