So I've been looking at the prototypical free-text query,

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT ?g ?s ?t ?type ?early ?late WHERE
{ GRAPH ?g
  {
    { ?lit pf:textMatch  "athens"  . }
    { ?s claros:hasLiteral ?lit . }
    {
      ?s rdf:type crm:E22.Man-Made_Object ;
        crm:P2.has_type
          [ rdf:type crm:E55.Type ;
            crm:P127.has_broader_term claros:ObjectType ;
            rdf:value  "Pottery"  ;
          ] .
    }
    {
      ?s crm:P108I.was_produced_by
        [ rdf:type crm:E12.Production ;
          crm:P4.has_time-span
            [ rdf:type crm:E52.Time-Span ;
              crm:P82.at_some_time_within
                [ rdf:type crm:E61.Time_Primitive ;
                  claros:not_before ?early ;
                  claros:not_after  ?late ;
                ]
            ]
        ] .
    }
    { ?s rdf:type ?t . }
  }
} 
ORDER BY ASC (xsd:integer(?early)) 
LIMIT 1
```

Initial investigation suggests that the time-critical components
are the sorting and the textMatch/hasLiteral search. Using the
fast date and type predicates improves things but not by very much.
The sorting takes many seconds. If we re-order the triples (in the
same way as suggested in yesterday's "judicious optimisation" note),
so that the sorting is unnecessary, we get a combinatorial explosion
with the literal generation -- things take not many but manymany
seconds.

It occurred to me that because we only want one answer (one of the
earliest subjects) a full sort is unnecessary: instead we can just
sift through the elements as they arrive and keep only the smallest.
This could be done as an ARQ algebra optimisation -- LIMIT 1 of
ORDER BY ASC X becomes a new operator SMALLEST X. As a quick test
of this, I ran this skeleton query

```
    ?lit pf:textMatch  "athens" . 
    ?s claros:hasLiteral ?lit .
    ?s fast:integer-not-before ?early . 
    ?s fast:type "Pottery" .
```

with no sort or limit. It still takes about 3s, so SMALLEST
doesn't seem to be a solution to our immediate problem.
