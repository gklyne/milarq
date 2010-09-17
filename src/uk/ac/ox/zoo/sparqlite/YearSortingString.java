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
public class YearSortingString extends FunctionBase1 {
	
	private Log log = LogFactory.getLog(YearSortingString.class);

	/**
	 * This function casts a literal year value to a string form suitable for
	 * comparing year values which may be earlier than 1AD.
	 * (Add 10000 to year and format as 5-digit number)
	 */
	@Override public NodeValue exec(NodeValue v) {
        Node n = v.asNode() ;
        if ( ! n.isLiteral() )
            throw new ExprEvalException("Not a Literal: "+FmtUtils.stringForNode(n)) ;
        String str = n.getLiteralLexicalForm();

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(5);
        nf.setGroupingUsed(false);
        Number year = 0;
        try {
			year = nf.parse(str);
		} catch (ParseException e) {
            throw new ExprEvalException("Not a valid year: "+str) ;
		}
        String datestr = nf.format(year.intValue()+10000) ; 
		// log.debug("exec: datestr: '"+datestr+"'") ;
        return NodeValue.makeString(datestr) ;
 
	}
}
