package uk.ac.ox.zoo.sparqlite;

import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.ExprEvalException;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;
import com.hp.hpl.jena.sparql.util.FmtUtils;

/**
 * Cast literal node with year value to a date/time string
 */
public class YearToDateString extends FunctionBase1 {
	
	private Log log = LogFactory.getLog(YearToDateString.class);

	/**
	 * This function casts a literal year value to a date string form suitable for use
	 * as the literal value of an xsd:dateTime literal node.
	 */
	@Override
	public NodeValue exec(NodeValue v) {
        Node n = v.asNode() ;
        if ( ! n.isLiteral() )
            throw new ExprEvalException("Not a Literal: "+FmtUtils.stringForNode(n)) ;
        String str = n.getLiteralLexicalForm();

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(4);
        nf.setGroupingUsed(false);
        Number year = 0;
        try {
			year = nf.parse(str);
		} catch (ParseException e) {
            throw new ExprEvalException("Not a valid year: "+str) ;
		}
        String datestr = nf.format(year) + "-01-01T00:00:00" ; 
		//log.debug("exec: datestr: '"+datestr+"'") ;
        return NodeValue.makeString(datestr) ;
        
        /* This doesn't work for formatting BC dates
        SimpleDateFormat gyear = new SimpleDateFormat("yyyy");
        Date date;
		try {
			date = gyear.parse(str);
		} catch (ParseException e) {
            throw new ExprEvalException("Not a valid year: "+str) ;
		}        
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String datestr = datetime.format( date ); 
        return NodeValue.makeString(datestr) ;
        */

	}
}
