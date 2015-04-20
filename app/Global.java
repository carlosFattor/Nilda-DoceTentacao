import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.GlobalSettings;
import play.api.Application;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import controllers.manager.ManagerLoggin;

public class Global extends GlobalSettings {

	private static Logger LOGGER = LoggerFactory.getLogger(ManagerLoggin.class.getName());

	@SuppressWarnings("unchecked")
	public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{GzipFilter.class};
    }
	
	public void onStart(Application app) {
		LOGGER.info("Application has started");
	}

	public void onStop(Application app) {
		LOGGER.info("Application shutdown...");
	}

	public Promise<Result> onError(RequestHeader request, Throwable t) {
		return Promise.<Result> pure(Results.internalServerError(views.html.errorPage.render()));
	}

	public Promise<Result> onHandlerNotFound(RequestHeader request) {
		return Promise.<Result> pure(Results.notFound(views.html.notfound.render()));
	}

	public Promise<Result> onBadRequest(RequestHeader request, String error) {
		return Promise.<Result> pure(Results.badRequest(views.html.badrequest.render()));
	}

	@SuppressWarnings("rawtypes")
	public Action onRequest(Request request, Method actionMethod) {
		return new ActionWrapper(super.onRequest(request, actionMethod));
	}
	
	private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

		@Override
		public Promise<Result> call(Context ctx) throws Throwable {
			Promise<Result> result = this.delegate.call(ctx);
			Http.Response response = ctx.response();
			response.setHeader("Access-Control-Allow-Origin","*");
            response.setHeader("Access-Control-Allow-Methods", "POST, HEAD, PATCH, GET, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            return result;
		}
    }
}
