/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandArgs
	{
	private final Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public void put(String key, String arg) 
		{ 
		List<String> present = map.get(key);
		if (present == null) map.put(key, present = new ArrayList<String>() );
		present.add(arg);
		}
    
    public String getOptional(String key, String ifAbsent) 
        {
        List<String> present = map.get(key);
        return present == null || present.size() == 0 ? ifAbsent : present.get(0);
        }
	
	public String getOptional(String key) 
		{ return getOptional( key, null ); }
	
	public String getOnly(String key) 
		{
		List<String> present = map.get(key);
		if (present == null || present.size() != 1) throw new RuntimeException( "must be exactly one value present for " + key );
		return present.get(0);
		}
	
	public boolean getPresent(String key) 
		{ return map.containsKey(key); }
	
	public String declare(String key) 
		{
		List<String> present = map.get(key);
		if (present == null) map.put(key, new ArrayList<String>());
		return key;
		}

	public int getInteger(String key, int ifAbsent) 
		{
		List<String> present = map.get(key);
		return present == null || present.size() == 0
			? ifAbsent
			: Integer.parseInt(present.get(0))
			;
		}

    public List<String> getAll( String key )
        { 
        List<String> present = map.get( key ), result = new ArrayList<String>();
        if (present != null) result.addAll( present );
        return result;
        }

    public static CommandArgs parse(String[] args) 
        {
        String key = "-";
        CommandArgs result = new CommandArgs();
        for (String arg: args)
        	{
        	if (arg.startsWith("-"))
        		{
        		String trimmed = arg.replaceFirst( "^-+", "-" );
        		int eq = trimmed.indexOf('=');
        		if (eq < 0)
        			key = result.declare(trimmed);
        		else
        			{
        			String k = trimmed.substring(0, eq);
        			String v = trimmed.substring(eq + 1);
        			result.put(k, v);
        			}        			
        		}
        	else
        		result.put( key, arg );
        	}
        return result;
        }        
	}