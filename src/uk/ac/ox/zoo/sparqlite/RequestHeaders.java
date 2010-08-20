package uk.ac.ox.zoo.sparqlite;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestHeaders {

	private Map<String,List<String>> headers = new Hashtable<String,List<String>>();

	public RequestHeaders() {
		// do nothing
	}
	
	@SuppressWarnings("unchecked")
	public RequestHeaders(HttpServletRequest request) {
		Enumeration names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			Enumeration values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				this.add(name, value);
			}
		}
	}
	
	public RequestHeaders add(String name, String value) {
		String key = new String(name.trim().toLowerCase());
		List<String> values;
		if (headers.containsKey(key)) {
			values = headers.get(key);
		} else {
			values = new ArrayList<String>();
			headers.put(key, values);
		}
		values.add(value.trim());
		return this;
	}
	
	public List<String> get(String name) {
		String key = new String(name.trim().toLowerCase());
		if (headers.containsKey(key)) {
			List<String> values = headers.get(key);
			List<String> valuesCopy = new ArrayList<String>(values);
			return valuesCopy;
		} else {
			return null;
		}
	}
	
	public String getFirst(String name) {
		String key = new String(name.trim().toLowerCase());
		if (headers.containsKey(key)) {
			List<String> values = headers.get(key);
			String value = values.get(0);
			String valueCopy = new String(value);
			return valueCopy;
		} else {
			return null;
		}
	}

}
