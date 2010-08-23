package uk.ac.ox.zoo.sparqlite;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestParams {

	Map<String,List<String>> params = new Hashtable<String,List<String>>();
	
	public RequestParams() {
		// do nothing
	}
	
	@SuppressWarnings("unchecked")
	public RequestParams(HttpServletRequest request) {
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String[] values = request.getParameterValues(name);
			for (int i=0; i<values.length; i++) {
				this.add(name, values[i]);
			}
		}
	}
	
	public RequestParams add(String name, String value) {
		String key = new String(name.trim().toLowerCase());
		List<String> values;
		if (params.containsKey(key)) {
			values = params.get(key);
		} else {
			values = new ArrayList<String>();
			params.put(key, values);
		}
		values.add(value.trim());
		return this;
	}
	
	public List<String> get(String name) {
		String key = new String(name.trim().toLowerCase());
		if (params.containsKey(key)) {
			List<String> values = params.get(key);
			List<String> valuesCopy = new ArrayList<String>(values);
			return valuesCopy;
		} else {
			return null;
		}
	}
	
	public String getFirst(String name) {
		String key = new String(name.trim().toLowerCase());
		if (params.containsKey(key)) {
			List<String> values = params.get(key);
			String value = values.get(0);
			String valueCopy = new String(value);
			return valueCopy;
		} else {
			return null;
		}
	}
	
}
