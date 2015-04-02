# Introduction #

From the [project plan](http://milarq.googlecode.com/hg/docs/MILARQ_Projectplan_VRERI_JISC.pdf), section 9, _Success plan_:

<blockquote>
Testing and evaluation will be test-led, based on the existing CLAROS test suite, which will ensure that required functionality is maintained and also provide a baseline for measuring performance improvements.<br>
<br>
Success criteria are simple: that the existing CLAROS VRE provides the existing capabilities with greatly improved performance for certain key queries. Each of the queries in the test framework, suitably modified to use the additional indexes, should produce the same results with sub-second response times. A secondary, desirable but non-essential, success criterion for CLAROS is that the timeline data can be retrieved using a single query rather than a series of queries as at present.<br>
</blockquote>

# Non-functional requirements #

  * **No custom code:** For software sustainability, we wish to avoid the inclusion of any CLAROS-specific code in the deployed query service - that is, to take existing software, deployed "out of the box", add configuration and data, and have it just work.  This is reflected in the evaluation suite in having no source code, just configuration and test cases, in the CLAROS-specific Eclipse projects.

# Some useful keyword queries #

  * "athens" - appears very frequently in the data
  * "paestan" - appears relatively late in the overall timeline (-400 .. -300)

# Initial development: test cases #

For the initial development, I propose to focus on two test cases:

  1. find the earliest known instance of an object of type "pottery".  This is chosen as a query that runs slowly under the present regime, but does not involve free-text keyword discovery.
  1. find the earliest known instance of an object of an object associated with "Herakles".  This is chosen as a query that runs slowly under the present regime, and involves a commonly used keyword in free text.

Use this link for submitting the queries: http://localhost:8080/claros_demo_server/query.html

## Earliest known pottery object ##

Current query:
```
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

Expected result:
```
{
  "head": {
    "vars": [ "g" , "s" , "t" , "early" , "late" ]
  } ,
  "results": {
    "bindings": [
      {
        "g": { "type": "uri" , "value": "http://purl.org/NET/Claros/graph/beazley-pottery" } ,
        "s": { "type": "uri" , "value": "http://www.beazley.ox.ac.uk/record/01397849-226B-4085-AAF3-86B37C0FCD2F" } ,
        "t": { "type": "uri" , "value": "http://purl.org/NET/crm-owl#E22.Man-Made_Object" } ,
        "early": { "datatype": "http://www.w3.org/2001/XMLSchema#gYear" , "type": "typed-literal" , "value": "-800" } ,
        "late": { "datatype": "http://www.w3.org/2001/XMLSchema#gYear" , "type": "typed-literal" , "value": "-700" }
      }
    ]
  }
}
```

Execution time on 2.4GHz MacBook: 103s (with cache primed)

## Earliest pottery association with Athens ##

Current query:
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

Expected result:
```
{
  "head": {
    "vars": [ "g" , "s" , "t" , "type" , "early" , "late" ]
  } ,
  "results": {
    "bindings": [
      {
        "g": { "type": "uri" , "value": "http://purl.org/NET/Claros/graph/beazley-pottery" } ,
        "s": { "type": "uri" , "value": "http://www.beazley.ox.ac.uk/record/7494DB4E-8610-412D-9C74-01068D44A06C" } ,
        "t": { "type": "uri" , "value": "http://purl.org/NET/crm-owl#E22.Man-Made_Object" } ,
        "early": { "datatype": "http://www.w3.org/2001/XMLSchema#gYear" , "type": "typed-literal" , "value": "-725" } ,
        "late": { "datatype": "http://www.w3.org/2001/XMLSchema#gYear" , "type": "typed-literal" , "value": "-675" }
      }
    ]
  }
}
```

Execution time on 2.4GHz MacBook: 79s (with cache primed)

# Final evaluation #

The final evaluation will consist of:

  1. run the existing test suite against the current CLAROS system, , noting the results and execution time for each query
  1. deploy an updated CLAROS system and running a full test suite with updated queries against it, noting the results and execution time for each query, comparing these with the baseline tests
  1. updating the CLAROS Explorer query templates, and evaluating results and performance for a number of known difficult queries ("Herakles", "Athens", "Amphora", etc.).

It is anticipated that new inference rules may be needed to preprocess the data to use the new indexes, and the database will be reloaded and re-indexed with this new data.