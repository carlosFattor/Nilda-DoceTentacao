package utils;

import play.cache.Cache;

public class CleanCache {

	public static void invalidate(String key){
		try {
			Cache.remove(key);
		} catch (Exception e) {
			throw new RuntimeException("Don't find any object with this name!");
		}
	}
	
}
