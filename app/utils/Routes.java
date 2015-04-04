package utils;

import java.util.Map;

public enum Routes {
	
	HOME("home") {
		@Override
		public Map<String, String> getRoutes() {
			
			return null;
		}
	},
	PRODUTOS("Produtos") {
		@Override
		public Map<String, String> getRoutes() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	GALERIA("Galeria") {
		@Override
		public Map<String, String> getRoutes() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	CONTATO("Contato") {
		@Override
		public Map<String, String> getRoutes() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	SAIBA_MAIS("Saiba Mais") {
		@Override
		public Map<String, String> getRoutes() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	private String description;
	
	Routes(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
	public abstract Map<String, String> getRoutes();
}
