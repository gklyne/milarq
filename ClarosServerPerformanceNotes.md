

This table shows query times for the slowest 10 queries in the test original suite, plus the running times for the complete test suites:
  1. with the original un-optimised server
  1. with MILARQ indexes, shortcut properties and optimized queries for timerange values only
  1. with materialized properties for object types (no new indexes), and some other ad-hoc optimizations (reducing query search-space by adding keyword searches, removing spurious ordering clauses)
  1. as (3), but also with materialized find location name properties.  The performance improvement is marginal, but the results obtained are more complete by virtue of catching places and regions, rather than just directly annotated places.
  1. as (4), but adding a specialized subject index to avoid sorting results.  One of the original top-10 slow queries is improved, but otherwise the effect is marginally worse performance overall. A number of the original queries are actually slower.  I guess this to be an indication that the MILARQ sorted index is slower than Lucene for performing basic keyword searches, which would not be surprising.
  1. as (5), but using the specialized subject index /only/ where it can accelerate queries using ORDER BY result qualifiers.  (The new indexes are not used for COUNT queries, as these seem to be made slower as a result.)
  1. as above, but adding a "short-circuit" property for accessing an object's shape.  Some queries are significantly improved by this.  Some queries appear to be marginally slower, but I believe this to be caused by extra background load on the computer when these tests were run.  This test suite also uses an updated version of ARQ and TDB compared with earlier tests.
  1. as above, but with precalculated object counts for all combinations of object type, shape, find location and time-range; count queries are evaluated by summing over an appropriate selection of these values.

The results in the table show results for the 10 individual tests that were slowest in the original query suite, and also the total over the full suite of 33 queries.  All results are evaluated with the cache "warmed" (i.e. the query suite was run at least once immediately prior to gathering these results.  The results were observed to vary by up to 20% between runs (due in part, I believe, to other load on the computer used).


| Test                                               |     (1)  |     (2)  |     (3) |     (4) |     (5) |     (6) |     (7) |     (8) |
|:---------------------------------------------------|:---------|:---------|:--------|:--------|:--------|:--------|:--------|:--------|
| testGetTimelineEnd                                 |   1.633s |   0.025s |  0.023s |  0.021s |  0.020s |  0.031s |  0.023s |  0.015s |
| testGetTimelineBegin                               |   1.696s |   0.018s |  0.014s |  0.018s |  0.015s |  0.013s |  0.015s |  0.012s |
| testGetFindLocationCountByType                     |   4.263s |   4.325s |  0.331s |  0.264s |  0.229s |  0.229s |  0.257s |  0.133s |
| testGetTimerangeFindLocationCountByType            |   6.400s |   1.633s |  0.295s |  0.226s |  0.219s |  0.210s |  0.341s |  0.136s |
| testGetShapetimeFindLocationObjectTypeCount        |   7.275s |   7.420s |  0.286s |  0.308s |  0.290s |  0.309s |  0.298s |  0.193s |
| testGetDetailsForShapetimeFindLocationObjectType   |   7.529s |   7.248s |  0.308s |  0.279s |  0.031s |  0.285s |  0.035s |  0.015s |
| testGetObjectCountByType                           |   8.115s |   8.557s |  2.207s |  2.141s |  2.178s |  2.394s |  2.299s |  0.504s |
| testGetShapetimeObjectTypeCount                    |   8.629s |   3.837s |  2.989s |  2.672s |  2.886s |  2.773s |  2.793s |  0.563s |
| testGetObjectTypeCountForShapetimeByFindLocation   |  11.839s |   8.661s |  4.236s |  3.244s |  3.293s |  3.253s |  3.264s |  0.733s |
| testGetDetailsForShapetimeObjectType               |  15.113s |  10.523s |  0.021s |  0.017s |  0.017s |  0.017s |  0.020s |  0.017s |
|                                                    |          |          |         |         |         |         |         |         |
| Total                                              | 96.575s  |  66.695s | 25.114s | 22.661s | 25.314s | 18.261s | 14.412s |  4.384s |


## Detailed results ##

### Original unoptimized queries ###

```
testGetDetailsForShapetimeObjectType                  : duration: 14.394s
testGetObjectTypeCountForShapetimeByFindLocation      : duration: 12.283s
testGetShapetimeObjectTypeCount                       : duration:  8.981s
testGetObjectCountByType                              : duration:  8.743s
testGetShapetimeFindLocationObjectTypeCount           : duration:  7.931s
testGetDetailsForShapetimeFindLocationObjectType      : duration:  7.864s
testGetTimerangeFindLocationCountByType               : duration:  6.869s
testGetFindLocationCountByType                        : duration:  4.534s
testGetDetailsForShapeObjectType                      : duration:  1.138s
testGetTimelineEnd                                    : duration:  1.107s
testGetTimelineBegin                                  : duration:  1.106s
testGetShapeTimerangeObjectTypeCount                  : duration:  0.993s
testGetDetailsForShapeTimerange                       : duration:  0.861s
testGetShapeForTimerangeObjectTypeCountByFindLocation : duration:  0.852s
testGetDetailsForShapeTimerangeObjectType             : duration:  0.851s
testGetShapeForObjectTypeCountByFindLocation          : duration:  0.832s
testGetShapeForTimerangeCountByType                   : duration:  0.828s
testGetShapeObjectTypeCount                           : duration:  0.827s
testGetShapeForTimerangeCountByLocation               : duration:  0.822s
testGetShapeForTimerangeFindLocationCountByType       : duration:  0.797s
testGetShapeTimerangeFindLocationCount                : duration:  0.791s
testGetDetailsForShapeTimerangeFindLocation           : duration:  0.783s
testGetTimelineRangeCount                             : duration:  0.770s
testGetShapeCountByLocation                           : duration:  0.730s
testGetShapeForFindLocationCountByType                : duration:  0.693s
testGetDetailsForShapeFindLocationObjectType          : duration:  0.688s
testGetShapeCount                                     : duration:  0.673s
testGetDetailsForShapeFindLocation                    : duration:  0.663s
testGetShapeFindLocationCount                         : duration:  0.657s
testGetShapeFindLocationObjectTypeCount               : duration:  0.650s
testGetShapeDetails                                   : duration:  0.013s

Total                                                 : duration: 89.723s
```

### TestDemoQueries-Milarq-8-Counting ###

```
testGetObjectTypeCountForShapetimeByFindLocation      : duration:  0.733s
testGetShapetimeObjectTypeCount                       : duration:  0.563s
testGetObjectCountByType                              : duration:  0.504s
testGetShapetimeFindLocationObjectTypeCount           : duration:  0.193s
testGetShapeForTimerangeObjectTypeCountByFindLocation : duration:  0.150s
testGetShapeFindLocationObjectTypeCount               : duration:  0.139s
testGetShapeForTimerangeCountByLocation               : duration:  0.138s
testGetTimerangeFindLocationCountByType               : duration:  0.136s
testGetShapeForObjectTypeCountByFindLocation          : duration:  0.134s
testGetShapeForTimerangeFindLocationCountByType       : duration:  0.134s
testGetShapeTimerangeFindLocationCount                : duration:  0.133s
testGetFindLocationCountByType                        : duration:  0.133s
testGetShapeForFindLocationCountByType                : duration:  0.132s
testGetShapeFindLocationCount                         : duration:  0.132s
testGetShapeCountByLocation                           : duration:  0.128s
testGetShapeTimerangeObjectTypeCount                  : duration:  0.128s
testGetTimelineRangeCount                             : duration:  0.127s
testGetShapeForTimerangeCountByType                   : duration:  0.126s
testGetShapeCount                                     : duration:  0.111s
testGetShapeObjectTypeCount                           : duration:  0.085s
testGetDetailsForShapeFindLocationObjectType          : duration:  0.053s
testGetDetailsForShapeFindLocation                    : duration:  0.048s
testGetDetailsForShapeTimerangeFindLocation           : duration:  0.048s
testGetDetailsForShapeObjectType                      : duration:  0.044s
testGetDetailsForShapeTimerange                       : duration:  0.026s
testGetDetailsForShapeTimerangeObjectType             : duration:  0.020s
testGetDetailsForShapetimeObjectType                  : duration:  0.017s
testGetTimelineEnd                                    : duration:  0.015s
testGetDetailsForShapetimeFindLocationObjectType      : duration:  0.015s
testGetShapeDetails                                   : duration:  0.014s
testGetTimelineBegin                                  : duration:  0.012s
testGetShapeDetails-thumbnail                         : duration:  0.011s

Total                                                 : duration:  4.384s
```

## Problem queries ##

The general pattern of queries used seems to be a "generator" which selects a number of candidate result entities (objects, people or places) based on a keyword or simple triple match, followed by a number of patterns and filters that eliminated unwanted results, followed by additional patterns that extract additional values associated with the selected entities from the database.

Problem queries seen to date have fallen into the following categories:
  * searching queries using multiple joins (property paths) and/or UNION patterns.
  * finding the earliest and latest dates associated with some criteria.
  * initial selection on object type rather than something more selective such as find location
  * use of ORDER BY clause to get results consistently ordered
  * counting queries

Searches over property paths and UNION patterns are simplified by adding "short circuit" properties to the data, and simplifying the query accordingly.

Earliest and latest dates are obtained by using MILARQ sorted indexes so that the first matching result is the minimum or maximum value as required.

The initial selection by object type is easily arranged by starting the query with a more selective pattern.

Use of ORDER BY in queries has been replaced by use of MILARQ sorted indexes to return results in order based on the objects' URI.

Counting queries are discussed below.

### Select by shape and find location ###

This query is slow (about 0.5s):
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT * WHERE
{ GRAPH ?g
  {
    ?s claros:s-term-bysubject (  "oinochoe"  ) .
    ?s crm:P14I.was_classified_by
        [ rdf:type crm:E17.Type_Assignment ;
          crm:P42.assigned
            [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?lit ]
        ] .
    FILTER regex( ?lit,  "oinochoe" , 'i')
    ?s claros:subject-findLocationName ?nam .
    FILTER regex( ?nam,  "CORINTH" , 'i')
  }
} OFFSET 5 LIMIT 5
```

This query, which uses the location index as the initial value generator, is better (about 0.125s):
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT * WHERE
{ GRAPH ?g
  {
    { ?s claros:s-term-bysubject (  "CORINTH"  ) . }
    {
      { ?s claros:subject-findLocationName ?nam . }
      FILTER regex( ?nam,  "CORINTH" , 'i')
    }
    {
      ?s crm:P14I.was_classified_by
          [ rdf:type crm:E17.Type_Assignment ;
            crm:P42.assigned
              [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?shape ] ;
          ] .
      FILTER regex( ?shape,  "oinochoe" , 'i')
    }
    OPTIONAL {
      { ?s claros:subject-not-before ?early ; claros:subject-not-after ?late . }
    }
    { ?s rdf:type ?t . }
    { ?s claros:subject-objectType ?typ ; }
    {
      { ?s crm:P102.has_title [ rdf:type crm:E35.Title ; rdf:value ?desc ] }
      { ?s crm:P70I.is_documented_in ?link }
    }
  }
} OFFSET 5 LIMIT 5
```

This query, which uses the Lucene index to select references to the location, and then filters specifically for the desired location and shape, is slightly slower at 0.2s:
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT * WHERE
{ GRAPH ?g
  {
    {
      ?nam pf:textMatch  "CORINTH"  .
      ?s claros:subject-findLocationName ?nam .
    }
    {
      ?s crm:P14I.was_classified_by
          [ rdf:type crm:E17.Type_Assignment ;
            crm:P42.assigned
              [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?shape ] ;
          ] .
      FILTER regex( ?shape,  "oinochoe" , 'i')
    }
    OPTIONAL {
      { ?s claros:subject-not-before ?early ; claros:subject-not-after ?late . }
    }
    { ?s rdf:type ?t . }
    { ?s claros:subject-objectType ?typ ; }
    {
      { ?s crm:P102.has_title [ rdf:type crm:E35.Title ; rdf:value ?desc ] }
      { ?s crm:P70I.is_documented_in ?link }
    }
  }
} ORDER BY ?s OFFSET 5 LIMIT 5
```

### Counting queries ###

Counting queries are all slow, typically about 0.5s, but with the very slowest taking over 3 seconds.  This appears to be due to a need to read through large numbers of entries and count them.

For example, this simple query is taking 2.2s:
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT ?typ (COUNT (?s) AS ?c) WHERE
{ GRAPH ?g
  {
    { ?s claros:subject-objectType ?typ ; }
  }
} GROUP BY ?typ
```


My plan for handling counting queries is based on precalculating sub-totals and then using SPARQL SUM aggregation query to add up the relevent sub-totals.

Currently, all count queries are combinations of objecttype, shape, early,  late,  findlocation.  It appears that there are upwards of 17000 combinations of these values.  If the dates are excluded, there are about 12000 combinations of type, shape and find location.

The plan is to add new data to each graph (i.e. per data source), thus:

```
[ a claros:Counter
  ; claros:subject-objectType  ?type
  ; claros:subject-objectShape ?shape
  ; claros:subject-not-before  ?early 
  ; claros:subject-not-after   ?late
  ; claros:subject-findLocationName ?nam
  ; claros:count               ?subtotal
  .
```

And then query with something like this:
```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
SELECT ?nam (SUM (?subtotal) AS ?totalcount) WHERE
{ GRAPH ?g
  { ?sc claros:subject-objectShape "oinochoe"
    ; claros:subject-objectType  "Pottery"
    ; claros:subject-not-before ?early 
    ; claros:subject-not-after ?late
    ; claros:subject-findLocationName ?nam
    ; claros:count ?subtotal
    .
    FILTER ( ?early <= -0525 ) .
    FILTER ( ?late  >= -0575 ) .
  }
} GROUP BY ?nam ORDER BY ?nam
```

### Avoiding double-counting ###

Objects can have multiple recorded find locations, such as a city and a region.  The original counting query used a UNION to capture these alternatives, which led to such objects being double-counted:

```
# Query to generate claros:hasLiteral statements.
# NOTE: the string %graph% must be replaced with the graph URI being processed. 

prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#>
prefix xsd:     <http://www.w3.org/2001/XMLSchema#> 
prefix crm:     <http://purl.org/NET/crm-owl#> 
prefix claros:  <http://purl.org/NET/Claros/vocab#> 
prefix graph:   <http://purl.org/NET/Claros/graph/> 
prefix arqfn:   <java:uk.ac.ox.zoo.sparqlite.> 
prefix pf:      <http://jena.hpl.hp.com/ARQ/property#>
prefix tf:      <http://epimorphics.com/vocabularies/tf/> 

CONSTRUCT 
  { ?s claros:subject-findLocationName ?lit }
WHERE
  { GRAPH <%graph%>
    {
      ?s     rdf:type             crm:E22.Man-Made_Object
      {
        ?s     crm:P16I.was_used_for ?find
        .
        ?find  rdf:type             crm:E7.Activity
        ;      crm:P2.has_type      claros:Event_FindObject
        ;      crm:P7.took_place_at ?place
        .
        ?place rdf:type             crm:E53.Place
        .
        {
          {
            ?place crm:P87.is_identified_by ?plnam
            .
            ?plnam rdf:type             crm:E48.Place_Name
            ;      rdf:value            ?lit
          }
        UNION
          {
            ?place crm:P89.falls_within ?reg
            .
            ?reg   rdf:type             crm:E53.Place
            ;      crm:P87.is_identified_by ?rnam
            .
            ?rnam  rdf:type             crm:E48.Place_Name
            ;      rdf:value            ?lit
          }
        }
      }
    }
  }
```

Used with:
```
PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm:     <http://purl.org/NET/crm-owl#>
PREFIX claros:  <http://purl.org/NET/Claros/vocab#>
PREFIX pf:      <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>

CONSTRUCT
    {
      [ a claros:Counter
      ; claros:c-subject-objectType       ?type
      ; claros:c-subject-objectShape      ?shape
      ; claros:c-subject-findLocationName ?location
      ; claros:c-subject-not-before       ?early 
      ; claros:c-subject-not-after        ?late
      ; claros:count                      ?c
      ]
    }
WHERE
    {
      SELECT ?type ?shape ?location ?early ?late (COUNT (*) AS ?c) WHERE
      { GRAPH <%graph%>
        { ?s rdf:type crm:E22.Man-Made_Object
          ; claros:subject-objectType ?type
          .
          OPTIONAL { ?s claros:subject-objectShape      ?shape }
          OPTIONAL { ?s claros:subject-not-before       ?early } 
          OPTIONAL { ?s claros:subject-not-after        ?late }
          OPTIONAL { ?s claros:subject-findLocationName ?location }
        }
      } GROUP BY ?type ?shape ?location ?early ?late
    }
```

I some situations, this problem can be tackled by replacing the UNION with multiple OPTONAL sections, and binding different variables within each such section, so that at most one result set is generated for each object.

## Probe queries ##

These are queries used to explore properties of the database.

### Count distinct combinations for count sub-totals ###

As given, excludes objects without date information.

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

SELECT (COUNT (*) as ?c) WHERE
{ SELECT ?g ?type ?shape ?location ?early ?late (COUNT (*) AS ?c) WHERE
  { GRAPH ?g
    { ?s crm:P14I.was_classified_by
        [ rdf:type crm:E17.Type_Assignment 
          ; crm:P42.assigned
            [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?shape ]
        ]
        ; claros:subject-not-before ?early 
        ; claros:subject-not-after  ?late
        ; claros:subject-objectType ?type
        ; claros:subject-findLocationName ?location 
        .
    }
  } GROUP BY ?g ?type ?shape ?location ?early ?late
}
```

### Return counts for attribute combinations ###

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

SELECT ?g ?type ?shape ?location ?early ?late (COUNT (*) AS ?c) WHERE
{ GRAPH ?g
  { ?s crm:P14I.was_classified_by
      [ rdf:type crm:E17.Type_Assignment 
        ; crm:P42.assigned
          [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?shape ]
      ]
      ; claros:subject-not-before ?early 
      ; claros:subject-not-after  ?late
      ; claros:subject-objectType ?type
      ; claros:subject-findLocationName ?location 
      .
  }
} GROUP BY ?g ?type ?shape ?location ?early ?late

```

### Count distinct combinations not including dates for count sub-totals ###

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX crm: <http://purl.org/NET/crm-owl#>
PREFIX claros: <http://purl.org/NET/Claros/vocab#>
PREFIX pf:  <http://jena.hpl.hp.com/ARQ/property#>
PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>

SELECT (COUNT (*) as ?c) WHERE
{ SELECT ?g ?type ?shape ?location (COUNT (*) AS ?c) WHERE
  { GRAPH ?g
    { ?s crm:P14I.was_classified_by
        [ rdf:type crm:E17.Type_Assignment 
          ; crm:P42.assigned
            [ a crm:E55.Type ; crm:P127.has_broader_term claros:Shape ; rdfs:label ?shape ]
        ]
        ; claros:subject-objectType ?type
        ; claros:subject-findLocationName ?location 
        .
    }
  } GROUP BY ?g ?type ?shape ?location
}
```

