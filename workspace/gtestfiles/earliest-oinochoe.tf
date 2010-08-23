!query original earliest item mentioning Oinochoe
-groups slow
-count 4
-larq lucene 
-tdb claros-tdb
-label original-Oinochoe

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX arqfn:  <java:uk.ac.ox.zoo.sparqlite.>
SELECT ?g ?s ?late WHERE
{ GRAPH ?g
  {
    { ?lit pf:textMatch  "Oinochoe"  . }
    { ?s claros:hasLiteral ?lit . }
    {
      ?s crm:P14I.was_classified_by
          [ rdf:type crm:E17.Type_Assignment ;
            crm:P42.assigned
              [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?lit ] ;
          ] .
    }
    { ?s rdf:type ?t . }
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
  }
} ORDER BY DESC (xsd:integer(?early)) LIMIT 1

