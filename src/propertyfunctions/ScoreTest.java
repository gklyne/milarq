package propertyfunctions;

import com.hp.hpl.jena.query.larq.HitLARQ;
import org.openjena.atlas.iterator.IteratorTruncate ;

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
