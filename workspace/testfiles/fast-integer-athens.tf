!query faster athens pottery query
-groups fast
-count 4
-larq ../claros_demo_server/WebContent/WEB-INF/data/combined/lucene/ 
-tdb augmented-tdb
-label faster-athens-pottery

PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
PREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>
PREFIX crm:    <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
PREFIX arqfn:  <java:uk.ac.ox.zoo.sparqlite.>

SELECT ?g ?s ?early ?late WHERE
{ 
  ?s  
      claros:subject-not-before ?early
      ; claros:subject-has-type "Pottery"
      ; claros:subject-not-after ?late
      .
  GRAPH ?g { ?s rdf:type ?ignored }
} 
LIMIT 1
