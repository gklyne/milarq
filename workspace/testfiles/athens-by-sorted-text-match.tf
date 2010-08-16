!query athens by property function, forced ordering
-groups slow example
-count 4
-larq lucene 
-tdb claros-tdb
-label forced-order-textMatch

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

PREFIX cpf: <java:propertyfunctions.>

SELECT ?g ?s ?early ?late WHERE
{ GRAPH ?g
  { ?lit pf:textMatch "athens". ?s claros:hasLiteral ?lit. }
    ?s claros:subject-not-before ?early.
    ?s claros:subject-has-type "Pottery".
    ?s claros:subject-not-after ?late.
} 
ORDER BY ASC (?early) 
LIMIT 1

!query athens by property function, no ordering
-groups slow example
-count 4
-larq lucene
-tdb claros-tdb
-label no-order-textMatch

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>

PREFIX cpf: <java:propertyfunctions.>

SELECT ?g ?s ?early ?late WHERE
{ GRAPH ?g
  { ?lit pf:textMatch "athens". ?s claros:hasLiteral ?lit. }
  ?s claros:subject-not-before ?early.
  ?s claros:subject-has-type "Pottery".
  ?s claros:subject-not-after ?late.
}
LIMIT 1

