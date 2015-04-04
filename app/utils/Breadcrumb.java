package utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Breadcrumb {
	
	public Breadcrumb() {
	}

	public static Map<String, String> getBreadcrumbs(String path) {
		Map<String, String> breadcrumbs = new LinkedHashMap<String, String>();
		switch (path) {
		case "Home":
			breadcrumbs.put("Home", "/");
			break;
		case "Category":
			breadcrumbs.put("Home", "/");
			breadcrumbs.put("Categoria", "/category");
			break;
		case "Product":
			breadcrumbs.put("Home", "/");
			breadcrumbs.put("Category", "/category");
			breadcrumbs.put("Produtos", "/category");
			break;
		case "Gallery":
			breadcrumbs.put("Home", "/");
			breadcrumbs.put("Galeria", "/gallery");
			break;
		case "Contact":
			breadcrumbs.put("Home", "/");
			breadcrumbs.put("Contato", "/contact");
			break;
		case "Learn":
			breadcrumbs.put("Home", "/");
			breadcrumbs.put("Saiba Mais", "/learn");
			break;

		default:
			break;
		}

		return breadcrumbs;
	}

}
