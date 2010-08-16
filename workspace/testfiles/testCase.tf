!query original athens query
-groups slow
-count 4
-larq ../claros_demo_server/WebContent/WEB-INF/data/combined/lucene/ 
-tdb augmented-tdb
-label orig-athens-query

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

SELECT ?g ?s ?t ?early ?late WHERE
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

!query athens with composite index property functions
-groups fast example
-count 4
-loc augmented-tdb
-label composite-adhoc-index

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

PREFIX cpf: <java:propertyfunctions.>

SELECT ?g ?s ?early ?late WHERE
{
  ?s cpf:compositeIndex ("athens" ?early ?late).
  ?s claros:subject-has-type "Pottery".
  GRAPH ?g { ?s rdf:type ?ignored }
} 
LIMIT 1
