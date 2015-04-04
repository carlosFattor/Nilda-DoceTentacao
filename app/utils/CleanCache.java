package utils;

import play.cache.Cache;

public class CleanCache {

	public static void invalidate(String key){
		Cache.remove(key);
	}
	
}
