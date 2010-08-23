!query pottery query with fast dates
-groups slow
-count 4
-larq lucene 
-tdb claros-tdb
-label pottery-fast-dates

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX arqfn:  <java:uk.ac.ox.zoo.sparqlite.>

SELECT ?g ?s ?t ?early ?late WHERE
{ GRAPH ?g
  {
    {  
      ?s claros:subject-not-before ?early.

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
                  claros:not_before ?early2 ;
                  claros:not_after  ?late ;
                ]
            ]
        ] .
    }
    { ?s rdf:type ?t . }
  }
} 
LIMIT 1
