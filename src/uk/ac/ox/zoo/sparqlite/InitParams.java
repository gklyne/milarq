package uk.ac.ox.zoo.sparqlite;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletConfig;

public class InitParams {
	
	private Map<String,String> params = new Hashtable<String,String>();

	public InitParams() {
		// do nothing
	}
	
	@SuppressWarnings("unchecked")
	public InitParams(ServletConfig config) {
		Enumeration names = config.getInitParameterNames();
		while ( names.hasMoreElements() ) {
			String name = (String) names.nextElement();
			String value = config.getInitParameter(name);
//			log.trace("putting rule: "+name+" = "+value);
			this.add(name, value);
		}
	}
	
	public InitParams add(String name, String value) {
		String key = new String(name.trim());
		String valueCopy = new String(value);
		params.put(key, valueCopy);
		return this;
	}
	
	public String get(String name) {
		String key = new String(name.trim());
		if (params.containsKey(key)) {
			String value = params.get(key);
			String valueCopy = new String(value);
			return valueCopy;
		} else {
			return null;
		}
	}
	
	public Map<String,String> asMap() {
		Map<String,String> map = new Hashtable<String,String>();
		for (String key : params.keySet()) {
			String keyCopy = new String(key);
			String valueCopy = new String(params.get(key));
			map.put(keyCopy, valueCopy);
		}
		return map;
	}
}
