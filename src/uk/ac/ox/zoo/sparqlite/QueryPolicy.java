package uk.ac.ox.zoo.sparqlite;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.syntax.*;

public class QueryPolicy {

	
	public static final String DISALLOWQUERYFORMS = "sparqlite.policy.DisallowQueryForms";
	public static final String SELECTLIMITCEILING = "sparqlite.policy.SelectLimitCeiling";
	public static final String CONSTRUCTLIMITCEILING = "sparqlite.policy.ConstructLimitCeiling";
	public static final String DESCRIBELIMITCEILING = "sparqlite.policy.DescribeLimitCeiling";
	public static final String DISALLOWVARIABLEPREDICATES = "sparqlite.policy.DisallowVariablePredicates";
	public static final String DISALLOWFILTERS = "sparqlite.policy.DisallowFilters";
	
	public Map<String,String> rules;
	Log log = LogFactory.getLog(QueryPolicy.class);
	
	public QueryPolicy(InitParams params) {
		this.rules = params.asMap();
	}

	public QueryPolicy(Map<String,String> rules) {
		this.rules = rules;
	}
	
	public Query apply(Query query) throws QueryPolicyException, UnexpectedException {
		log.trace("apply query policy");
		
		applyQueryFormPolicy(query);
		
		applyDatasetDescriptionPolicy(query);
		
		applyLimitCeilingPolicy(query);
		
		applyVariablePredicatePolicy(query);

		applyFilterPolicy(query);

		return query;
	}

	public void applyQueryFormPolicy(Query query) throws QueryPolicyException {
		log.trace("apply query form policy");
		if (rules.containsKey(QueryPolicy.DISALLOWQUERYFORMS)) {
			String value = rules.get(QueryPolicy.DISALLOWQUERYFORMS);
			log.trace("found disallowed query forms: "+value);
			String[] disallowedForms = value.split(" ");
			for (int i=0; i<disallowedForms.length; i++) {
				if (disallowedForms[i].trim().equalsIgnoreCase("ASK") && query.isAskType()) {
					throw new QueryPolicyException("ASK queries are disallowed");
				} else if (disallowedForms[i].trim().equalsIgnoreCase("SELECT") && query.isSelectType()) {
					throw new QueryPolicyException("SELECT queries are disallowed");	
				} else if (disallowedForms[i].trim().equalsIgnoreCase("CONSTRUCT") && query.isConstructType()) {
					throw new QueryPolicyException("CONSTRUCT queries are disallowed");	
				} else if (disallowedForms[i].trim().equalsIgnoreCase("DESCRIBE") && query.isDescribeType()) {
					throw new QueryPolicyException("DESCRIBE queries are disallowed");	
				} 
			}
		}
		
	}

	public void applyDatasetDescriptionPolicy(Query query) throws QueryPolicyException {
		log.trace("apply dataset description policy");
		// blanket policy for now, reject all queries with FROM or FROM NAMED
		if (query.hasDatasetDescription()) {
			log.trace("query has dataset description");
			throw new QueryPolicyException("dataset description not allowed in query");
		}
	}

	public void applyLimitCeilingPolicy(Query query) throws UnexpectedException {
		try {
			log.trace("apply limit ceiling policy");
			if (query.isSelectType() && rules.containsKey(QueryPolicy.SELECTLIMITCEILING)) {
				log.trace("applying select limit ceiling");
				long ceiling = Long.parseLong(rules.get(QueryPolicy.SELECTLIMITCEILING));
				long limit = query.getLimit();
				if (limit == Query.NOLIMIT || limit > ceiling) {
					query.setLimit(ceiling);
				}
			}
			else if (query.isConstructType() && rules.containsKey(QueryPolicy.CONSTRUCTLIMITCEILING)) {
				log.trace("applying construct limit ceiling");
				long ceiling = Long.parseLong(rules.get(QueryPolicy.CONSTRUCTLIMITCEILING));
				long limit = query.getLimit();
				if (limit == Query.NOLIMIT || limit > ceiling) {
					query.setLimit(ceiling);
				}
			}
			else if (query.isDescribeType() && rules.containsKey(QueryPolicy.DESCRIBELIMITCEILING)) {
				log.trace("applying describe limit ceiling");
				long ceiling = Long.parseLong(rules.get(QueryPolicy.DESCRIBELIMITCEILING));
				long limit = query.getLimit();
				if (limit == Query.NOLIMIT || limit > ceiling) {
					query.setLimit(ceiling);
				}
			}
		} catch ( Exception e ) {
			throw new UnexpectedException("caught unexpected exception applying query limit ceiling: "+e.getLocalizedMessage());
		}
		
	}

	public void applyVariablePredicatePolicy(Query query) throws QueryPolicyException {
		log.trace("apply variable predicates policy");
		if (rules.containsKey(QueryPolicy.DISALLOWVARIABLEPREDICATES)) {
			boolean disallow = Boolean.parseBoolean(rules.get(QueryPolicy.DISALLOWVARIABLEPREDICATES));
			log.trace("disallow variable predicates: "+disallow);
			if (disallow) {
				log.trace("variable predicates are not allowed, checking for variable predicates in query");
				Element pattern = query.getQueryPattern();
				if (pattern != null) {
					log.trace("query pattern: "+query.getQueryPattern().toString());				
					VariablePredicateFinder finder = new VariablePredicateFinder();	
					query.getQueryPattern().visit(finder);
					if (finder.foundVariablePredicate) {
						throw new QueryPolicyException("variable predicates disallowed in query pattern");
					}
				}
			}
		} else {
			log.trace("no variable predicate policy found");
		}
		
	}
	
	class VariablePredicateFinder implements ElementVisitor {
		boolean foundVariablePredicate = false;
		
		@SuppressWarnings("unchecked")
		public void visit(ElementTriplesBlock arg0) {
			// TODO Auto-generated method stub
			log.trace("ElementTriplesBlock: "+arg0.toString());
			BasicPattern pattern = arg0.getPattern();
			log.trace("pattern of ElementTriplesBlock: "+pattern.toString());
			Iterator it = arg0.patternElts();
			while (it.hasNext()) {
				Object ob = it.next();
				log.trace("pattern element of ElementTriplesBlock: "+ob.getClass());
				log.trace(ob.toString());
				if (ob instanceof Triple) {
					Triple t = (Triple) ob;
					Node s = t.getSubject();
					Node p = t.getPredicate();
					Node o = t.getObject();
					log.trace("triple, subject: "+s+" ["+s.getClass()+"] predicate: "+p+" ["+p.getClass()+"] object: "+o+" ["+o.getClass()+"]");
					if (p instanceof com.hp.hpl.jena.sparql.core.Var) {
						log.trace("found variable predicate");
						foundVariablePredicate = true;
					}
				}
			}
		}

		public void visit(ElementPathBlock arg0) {
			log.trace("ElementPathBlock: "+arg0.toString());
		}

		public void visit(ElementFilter arg0) {
			log.trace("ElementFilter: "+arg0.toString());
		}

		public void visit(ElementAssign arg0) {
			log.trace("ElementAssign: "+arg0.toString());
		}

		public void visit(ElementUnion arg0) {
			log.trace("ElementUnion: "+arg0.toString());
			for (Object o : arg0.getElements()) {
				log.trace("Element: "+o.getClass());
				Element e = (Element) o;
				e.visit(this);
			}
		}

		public void visit(ElementOptional arg0) {
			log.trace("ElementOptional: "+arg0.toString());
			arg0.getOptionalElement().visit(this);
		}

		public void visit(ElementGroup arg0) {
			log.trace("ElementGroup: "+arg0.toString());
			for (Object o : arg0.getElements()) {
				log.trace("Element: "+o.getClass());
				Element e = (Element) o;
				e.visit(this);
			}
		}

		public void visit(ElementDataset arg0) {
			log.trace("ElementDataset: "+arg0.toString());	
		}

		public void visit(ElementNamedGraph arg0) {
			log.trace("ElementNamedGraph: "+arg0.toString());
			arg0.getElement().visit(this);
		}

		public void visit(ElementService arg0) {
			log.trace("ElementService: "+arg0.toString());				
		}

		public void visit(ElementSubQuery arg0) {
			log.trace("ElementSubQuery: "+arg0.toString());				
		}

		public void visit(ElementFetch arg0) {
			log.trace("ElementFetch: "+arg0.toString());				
		}

        @Override public void visit( ElementExists arg0 )
            { log.trace("ElementExists: "+arg0.toString());}

        @Override public void visit( ElementNotExists arg0 )
            { log.trace("ElementExists: "+arg0.toString());}

        @Override public void visit( ElementMinus arg0 )
            { log.trace("ElementExists: "+arg0.toString());}

	}

	public void applyFilterPolicy(Query query) throws QueryPolicyException {
		log.trace("apply filter policy");
		if (rules.containsKey(QueryPolicy.DISALLOWFILTERS)) {
			boolean disallow = Boolean.parseBoolean(rules.get(QueryPolicy.DISALLOWFILTERS));
			log.trace("disallow filters: "+disallow);
			if (disallow) {
				log.trace("filters are not allowed, checking for filters in query");
				Element pattern = query.getQueryPattern();
				if (pattern != null) {
					log.trace("query pattern: "+query.getQueryPattern().toString());				
					FilterFinder finder = new FilterFinder();	
					query.getQueryPattern().visit(finder);
					if (finder.foundFilter) {
						throw new QueryPolicyException("filters are disallowed in query pattern");
					}
				}
			}
		} else {
			log.trace("no variable predicate policy found");
		}
	}

	class FilterFinder implements ElementVisitor {
		boolean foundFilter = false;
		
		@SuppressWarnings("unchecked")
		public void visit(ElementTriplesBlock arg0) {
			log.trace("ElementTriplesBlock: "+arg0.toString());
		}

		public void visit(ElementPathBlock arg0) {
			log.trace("ElementTriplesPath: "+arg0.toString());
		}

		public void visit(ElementFilter arg0) {
			log.trace("ElementFilter: "+arg0.toString());
			log.trace("found filter");
			foundFilter = true;
		}

		public void visit(ElementAssign arg0) {
			log.trace("ElementAssign: "+arg0.toString());
		}

		public void visit(ElementUnion arg0) {
			log.trace("ElementUnion: "+arg0.toString());
			for (Object o : arg0.getElements()) {
				log.trace("Element: "+o.getClass());
				Element e = (Element) o;
				e.visit(this);
			}
		}

		public void visit(ElementOptional arg0) {
			log.trace("ElementOptional: "+arg0.toString());
			arg0.getOptionalElement().visit(this);
		}

		public void visit(ElementGroup arg0) {
			log.trace("ElementGroup: "+arg0.toString());
			for (Object o : arg0.getElements()) {
				log.trace("Element: "+o.getClass());
				Element e = (Element) o;
				e.visit(this);
			}
		}

		public void visit(ElementDataset arg0) {
			log.trace("ElementDataset: "+arg0.toString());	
		}

		public void visit(ElementNamedGraph arg0) {
			log.trace("ElementNamedGraph: "+arg0.toString());
			arg0.getElement().visit(this);
		}

		public void visit(ElementService arg0) {
			log.trace("ElementService: "+arg0.toString());				
		}

		public void visit(ElementSubQuery arg0) {
			log.trace("ElementSubQuery: "+arg0.toString());				
		}

		public void visit(ElementFetch arg0) {
			// TODO Auto-generated method stub
			log.trace("ElementFetch: "+arg0.toString());				
		}

        @Override public void visit( ElementExists arg0 )
            {log.trace("ElementExists: "+arg0.toString());}

        @Override public void visit( ElementNotExists arg0 )
            {log.trace("ElementNotExists: "+arg0.toString());}

        @Override public void visit( ElementMinus arg0 )
            {log.trace("ElementMinus: "+arg0.toString());}

	}

}
