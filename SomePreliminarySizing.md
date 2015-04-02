# Introduction #

Taking the "earliest pottery" query as a basis,
experimented with tweaking it to get some idea
of how much room for manoeuver we have.

# Details #

Take the pottery query and run it on a 4G machine
running 64bit Fedora Core 12, using the claros
demo server inside Eclipse 3.5.2 with Tomcat 6.0.20.

With the cache warmed, this query takes about 17s
elapsed time.

The "?s rdf:type ?t" triple at the end is not really useful,
because of the "?s rdf:type crm:E22.Man-Made\_Object" at
the top, hence we know at least one value for ?t. Eliminating
the triple drops the time to about 15s.

Temporarily suppressing the ORDER BY clause drops the
time to 2s, t-w-o seconds. So it looks like the sort,
or the absence of ordering, is costing a significant
part of the time -- just as well if that's what we're
hoping to optimise.

(Put the ORDER BY clause back now.)

We assume that the query is not being reordered by
the underlying engine, so that triples patterns are
used in more-or-less the order they appear. On that
basis, the rdf:value term with its literal value
"Pottery" should come earlier, to eliminate non-matching
candidates as soon as possible.

Make the pottery term first in its bnode. Query time
drops to about 13s.

On spec, move the test for Man-Made\_Object down and
eliminated superfluous braces (apparently they can
inhibit optimisations). However, query time remained
at about 13s and may have increased a little.

The has\_broader\_term constraint probably isn't doing
useful work. (We need to test that hypothesis against
the ontology & the dataset.) Eliminating it leaves
the query time at about 13s, maybe reduced a little.

Again given plausible ontological hypotheses, eliminate
the test for Time\_Primitive; takes time down to about
12s.

Eliminating the similar test for Time-Span doesn't make
much difference; still about 12s, maybe dropped a little.

Again given plausible ontological hypotheses, eliminate
the type test for Production. Query time drops to about
11s.

At this point the query looks like (dropping prefixes):


```
SELECT ?g ?s ?early ?late WHERE
{ GRAPH ?g
  {
  ?s crm:P2.has_type
    [ rdf:value "Pottery" ; rdf:type crm:E55.Type ;
    ].
  ?s rdf:type crm:E22.Man-Made_Object .
  ?s crm:P108I.was_produced_by
    [crm:P4.has_time-span
      [crm:P82.at_some_time_within
        [ claros:not_before ?early ;
          claros:not_after  ?late ;
        ]
      ]
    ].
  }
} 
ORDER BY ASC (xsd:integer(?early)) 
LIMIT 1
```

Eliminating the Man-Made\_Object completely
doesn't make a significant time difference.

It occurs to me that it might be more efficient
in general if the P2.has\_type object was a URI
and not a literal string. Does TDB intern strings?
If not, using a URI would allow it to do an indexed
lookup. Check with Andy at a suitable moment.

At this point, switching off the ORDER BY allows
the query to run in about 1.5s.
