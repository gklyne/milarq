package propertyfunctions;

import com.hp.hpl.jena.query.larq.HitLARQ;
import com.hp.hpl.jena.sparql.lib.iterator.IteratorTruncate ;

public class ScoreTest implements IteratorTruncate.Test
	{
    private float scoreLimit ;
    public ScoreTest(float scoreLimit) { this.scoreLimit = scoreLimit ; }
    public boolean accept(Object object)
    	{
        HitLARQ hit = (HitLARQ)object ;
        return hit.getScore() >= scoreLimit ;
    	}
	}
