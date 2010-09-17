!query dated athens pottery using multiple Lucene index
-groups fast example
-count 4
-larq lucene 
-tdb claros-tdb
-label multiple-lucene-index

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

PREFIX cpf: <java:propertyfunctions.>

SELECT ?s ?earliest ?latest WHERE
{  
  ?s cpf:multipleIndex ("athens" ?earliest ?latest).
  ?s claros:subject-has-type "Pottery".
} 
LIMIT 1
