package cmd;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import uk.ac.ox.zoo.sparqlite.cmd.support.*;
import util.*;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
    <p>Command-line tool to weave RDF data into an HTML template file.</p>
    
    <pre>
    -from RDF data
    -template template file, looks like HTML
    </pre>
    
    <p>
    weave reads the HTML input and writes it out, modified according to the
    embedded directives. weave provides two interacting features: element 
    abstraction and query running.
    </p>
    
    <h2>ABSTRACTION</h2>
    
    <p>
    An element with attribute define="NAME" is a definition. The element is
    not copied to the output; instead it is remembered under the supplied NAME.
    </p>
    
    <p>
    An element with attribute insert="NAME" is an insertion. NAME must be the
    name of an earlier definition. The body of the element is not copied, but
    used to specify argument names & values: each of its elements should have
    an attribute arg=ANAME and a single child element which is the value of
    the argument ANAME.
    </p>
    
    <p>
    The named definition is copied to the output. Any elements with attribute
    argval=ANAME are replaced by the value of argument ANAME.
    </p>
    
    <h2>QUERY</h2>
    
    <p>
    An element with attribute for=QUERY is a trimmed SPARQL query consisting of
    a variable name and a query tailpiece (whatever can follow WHERE). It has
    all the prefixes from the RDF data model, plus some built-in ones. The
    element is repeated once for each binding of the variable.
    </p>
    
    <p>
    An element with attribute val="X.P1.P2...Pn|F" is replaced by the value
    obtained from the query variable X, following the property chain P1..Pn,
    and extracting the literal value from the resulting node. |F specifies
    processing by a filter class.
    </p>
    
    <p>
    Both for and val expand any expressions %!NAME! by replacing them with
    the value of abstraction arguments.
    </p>
     
    <h2>DEPENDENCIES</h2>
     
    <p>
    weave depends on the external cyberneko library. 
    </p>
     
    @author chris
*/
public class weave
    {    
    public static void main( String [] args ) throws SAXException, IOException
        { new weave( args ).print( System.out ); }
    
    final CommandArgs a;
    
    final Map<String, Node> definitions = new HashMap<String, Node>();
    
    Map<String, Node> arguments = new HashMap<String, Node>();
    
    public weave( String [] args ) 
        { a = CommandArgs.parse( args ); }
    
    public void print( PrintStream out ) throws SAXException, IOException
        {
        String tName = a.getOnly( "-template" );
        DOMParser parser = new DOMParser();
        parser.parse( tName );
        Context c = new Context( loadDataModel() );
        new StripKnownPrefixes();
        print( c, System.out, parser.getDocument(), "" );
        }

    private Model loadDataModel()
        {
        Model result = FileManager.get().loadModel( a.getOnly( "-from" ) );
        result.setNsPrefix( "rdf", RDF.getURI() );
        result.setNsPrefix( "rdfs", RDFS.getURI() );
        return result;
        }
    
    interface Bush
        {
        final Map<String, String> NO_ATTRIBUTES = new HashMap<String, String>();
        
        final List<Bush> NO_CHILDREN = new ArrayList<Bush>();

        final Bush EMPTY_CHILD = new Bush() 
            {
            @Override public Map<String, String> attributes()
                { return NO_ATTRIBUTES; }

            @Override public List<Bush> children()
                { return NO_CHILDREN; }

            @Override public String elementName()
                { return ""; }

            @Override public int oneLineLength()
                { return 0; }

            @Override public String textContent()
                { return "";}
            };
        
        String elementName();
        Map<String, String> attributes();
        String textContent();
        List<Bush> children();
        int oneLineLength();
        }
    
    static class Bushy implements Bush
        {
        final String elementName;
        final String textContent;
        final Map<String, String> attributes;
        final List<Bush> children;
        final int width;
        
        public Bushy( String eName, Map<String, String> attributes, String textContent, List<Bush> children )
            {
            if (eName.equals("")) throw new IllegalArgumentException( "empty string for tag!" );
            this.elementName = eName;
            this.textContent = textContent.trim();
            this.children = children;
            this.attributes = attributes;
            this.width = 
                eName.length() + 2
                + this.textContent.length()
                + length(attributes)
                + length(children)
                ;
            }

        private int length( Map<String, String> attributes )
            {
            int result = 0;
            for (String key: attributes.keySet())
                result += key.length() + attributes.get( key ).length() + 3;
            return result;
            }

        private int length( List<Bush> children )
            {
            int result = 0;
            for (Bush child: children) result += child.oneLineLength();
            return result;
            }

        @Override public Map<String, String> attributes()
            { return attributes; }

        @Override public List<Bush> children()
            { return children; }

        @Override public String elementName()
            { return elementName; }

        @Override public String textContent()
            { return textContent; }

        @Override public int oneLineLength()
            { return width; }

        public static Bush string( String s )
            {
            return new Bushy( "!!", Bush.NO_ATTRIBUTES, s, NO_CHILDREN );
            }
        }
    
    public void print( Context c, PrintStream out, Node node, String indent)
        {
        int WIDTH = 80;
        Bush b = toBush( c, node );
        printBush( false, out, b, indent, WIDTH );
        }
    
    private void printBush( boolean multiLine, PrintStream out, Bush b, String indent, int width )
        {
        if (b.elementName().equals( "!!" ))
            { // plain text
            if (b.textContent().length() > 0)
                {
                // if (multiLine) out.print( indent );
                out.print( b.textContent() );
                // if (multiLine) out.println();
                }
            }
        else if (b.elementName().equals( "!transparent" ))
            for (Bush child: b.children())
                printBush( multiLine, out, child, indent, width );
        else if (b.oneLineLength() < width - indent.length())
            { // sufficiently short
            if (multiLine) out.print( indent );
            out.print( "<" );
            out.print( b.elementName() );
            out.print( ">" );
            out.print( b.textContent() );
            for (Bush child: b.children()) printBush( false, out, child, indent + " ", width );
            out.print( "</" );
            out.print( b.elementName() );
            out.print( ">" );
            if (multiLine) out.println();
            }
        else
            { // too long for one line
            if (multiLine) out.print( indent );
            out.print( "<" );
            out.print( b.elementName() );
            out.print( ">" );
            if (multiLine) out.println();
            out.print( b.textContent() );
            for (Bush child: b.children()) 
                {
                printBush( true, out, child, indent + " ", width );
                // out.println();
                }
            if (multiLine) out.print( indent );
            out.print( "</" );
            out.print( b.elementName() );
            out.print( ">" );
            if (multiLine) out.println();
            }
        }

    public Bush toBush( Context c, Node node )
        {
        String tag = node.getNodeName().toLowerCase();
        if (tag.equals( "#document" ))
            {
            return toBush( c, node.getFirstChild() );
            }
        else if (tag.equals( "#text" ))
            {
            String text = node.getNodeValue().trim();
            return Bushy.string( text ); 
            }
        else
            {
            NamedNodeMap attributes = node.getAttributes();
            String define = getValueFor( attributes, "define" );
            String insert = getValueFor( attributes, "insert" );
            String argval = getValueFor( attributes, "argval" );
            if (define != null)
                {
                attributes.removeNamedItem( "define" );
                definitions.put( define, node );
                return Bush.EMPTY_CHILD;
                }
            else if (insert != null)
                {
                return insertDefinitionWithArguments( c, node, insert );
                }
            else if (argval != null)
                {
                return toBush( c, arguments.get( argval ) );
                }
            else
                {
                String forx = getValueFor( attributes, "for" );
                String valx = getValueFor( attributes, "val" );
                if (forx == null && valx == null)
                    return toBush( c, attributes, node, tag );
                else if (forx == null)
                    return Bushy.string( c.evalToString(valx) );
                else
                    {
                    List<Bush> children = new ArrayList<Bush>();
                    Repeat r = c.buildFor( c, forx );
                    List<RDFNode> values = r.resultsAsNodes();
                    for (RDFNode value: values)
                        {
                        c.putBinding( r.name, value );
                        Bush xxx = toBush( c, attributes, node, tag );
                        if (xxx != Bush.EMPTY_CHILD) children.add( xxx );
                        c.removeBinding( r.name );
                        }
                    return new Bushy( "!transparent", asMap( attributes ), "", children );
                    }
                }
            }
        }

    private Map<String, String> asMap( NamedNodeMap attributes )
        {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); i += 1)
            {
            Node item = attributes.item( i );
            String name = item.getLocalName();
            if (name.equals( "for" ) || name.equals( "val" ))
                {}
            else
                result.put( name, attributes.getNamedItem( name ).getNodeValue() );
            }
        return result;
        }

    private Bush insertDefinitionWithArguments( Context c, Node node, String insert )
        {
        Map<String, Node> save = arguments;
        arguments = new HashMap<String, Node>( arguments );
        Node toCopy = definitions.get( insert );
        Node child = node.getFirstChild();
        while (child != null)
            {
            NamedNodeMap attributes = child.getAttributes();
            if (attributes != null) 
                {               
                String argName = getValueFor( attributes, "arg" );
                arguments.put( argName, child.getFirstChild() );
                }
            child = child.getNextSibling();
            }
        Bush result = toBush( c, toCopy );
        arguments = save;
        return result;
        }

    private String getValueFor( NamedNodeMap attributes, String name )
        {
        Node node = attributes.getNamedItem( name );
        return node == null ? null : applyArguments( node.getNodeValue() );
        }

    private String applyArguments( String s )
        {
        for (String arg: arguments.keySet())
            s = s.replaceAll( "%!" + arg + "!", arguments.get( arg ).getNodeValue() );
        return s;
        }

    private Bush toBush( Context c, NamedNodeMap attributes, Node node, String tag )
        {
        List<Bush> children = new ArrayList<Bush>();
        Node child = node.getFirstChild();
        while (child != null)
            {
            children.add( toBush( c, child ) );
            child = child.getNextSibling();
            }
        return new Bushy( tag, asMap(attributes), "", children );
        }
    }
