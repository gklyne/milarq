/**
 * 
 */
package util;

import java.util.*;

import com.hp.hpl.jena.util.iterator.Filter;

public class CommandArgs
	{
	private final Map<String, List<String>> map = new HashMap<String, List<String>>();
	private final Set<String> messages = new HashSet<String>();
	
	public boolean ok()
	    { return messages.size() == 0; }
	
	public boolean bad()
	    { return messages.size() > 0; }
	
	public Set<String> messages()
	    { return messages; }

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

    public static CommandArgs parse( String allowed, String [] args )
        { return parse( allowOnly( allowed ), args, false ); }
    
    public static CommandArgs parse( String[] args ) 
        { return parse( Filter.<String>any(), args, false ); }
    
    public static CommandArgs parse( String allowed, String [] args, boolean failIfErrors )
        { return parse( allowOnly( allowed ), args, failIfErrors ); }
    
    public static CommandArgs parse( Filter<String> allowed, String[] args, boolean failIfErrors ) 
        {
        String key = "-";
        CommandArgs result = new CommandArgs();
        for (String arg: args)
        	{
        	if (arg.startsWith("-"))
        		{
        		String trimmed = arg.replaceFirst( "^-+", "-" );
        		if (!allowed.accept( trimmed )) 
        		    result.messages.add( trimmed + " is not a legal option for this command: " + allowed );
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
        if (result.bad())
            throw new RuntimeException( "illegal options: " + result.messages.toString() );
        return result;
        }          
    
    private void put(String key, String arg) 
        { 
        List<String> present = map.get(key);
        if (present == null) map.put(key, present = new ArrayList<String>() );
        present.add(arg);
        }
    
    private static Filter<String> allowOnly( String allowed )
        {
        final Set<String> elements = new HashSet<String>();
        for (String a: allowed.split( " +" )) elements.add( a );
        return new Filter<String>() 
            {
            @Override public boolean accept( String o )
                { return elements.contains( o ); }  
            
            @Override public String toString()
                { return "allowed: " + elements.toString(); }
            };
        }

	}