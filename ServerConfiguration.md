# Introduction #

These notes based on configuration of claros-test-server on 9-Sep-2010

See file [docs/howto.test](http://code.google.com/p/milarq/source/browse/docs/howto.text) in the source code area

# Notes #

  * `WebContent/WEB-INF/web.xml` - need to configure Init servlet
  * `WebContent/WEB_INF/tdb/combined.ttl` - need to configure indexed property directories in dataset assembler description

# Property index directory configuration via assembler file #

For clues, see:
  * `src/cmd/generic_indexing_example.java`
  * `src/uk/ac/ox/zoo/sparqlite/config/Sparqlite.java`, line 66 _et seq_, 153 _et seq_
  * `src/propertyfunctions/genericIndex.java`, line 43 _et seq_
  * `src/cmd/generic_indexing_example.java`, line 114 _et seq_ (`constructAssemblerDescription`) is particularly relevant

In what follows, the `sparqlite:mapIf` statements indicates a regular expression that is mapped against the incoming URI after the server has removed its prefix (e.g. for URI `http://localhost:8080/claros_test_server/foo/bar`, just `/foo/bar` is presented for matching ???)  to yield a name for a corresponding dataset description.   This name is used to find an RDF model describing the dataset. If the string does not "look like" an HTTP URL, then it is used as a filename and the RDF file named by that filename is loaded. If the replacement string does look like a URL, then it is taken to refer to a dataset defined in the assembler model.  See section (3) of [docs/howto.test](http://code.google.com/p/milarq/source/browse/docs/howto.text) for detail.

I think something like this is called for (these examples from `claros_test_server/WebContent`)...

Sparqlite description in `WEB_INF/sparqlite-assembly.ttl`:
```
# Sparqlite assembly file.
#
@prefix tdb:       <http://jena.hpl.hp.com/2008/tdb#>.
@prefix rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.
@prefix ja:        <http://jena.hpl.hp.com/2005/11/Assembler#>.
@prefix sparqlite: <http://purl.org/net/sparqlite/vocab#>.
@prefix claros:    <http://purl.org/NET/Claros/vocab#>.

sparqlite:root a sparqlite:Sparqlite
    ; sparqlite:defaultGraphIsUnion true
    ; sparqlite:mapIf
      [ sparqlite:matches ".*"
      ; sparqlite:replacement "WEB-INF/tdb/%s.ttl"
      ]
    ; sparqlite:register
      [ sparqlite:forPredicate "http://purl.org/NET/Claros/vocab#subject-not-before" 
      ; sparqlite:useClass "propertyfunctions.genericIndex"
      ]
    ; sparqlite:register
      [ sparqlite:forPredicate "http://purl.org/NET/Claros/vocab#subject-not-after"
      ; sparqlite:useClass "propertyfunctions.genericIndex"
      ]
    ; sparqlite:indexDirectory 
      [ sparqlite:forPredicate "http://purl.org/NET/Claros/vocab#subject-not-before" 
      ; sparqlite:useDirectory "claros_test_server/WebContent/WEB-INF/data/combined/indexes/term-nb"
      ]
    ; sparqlite:indexDirectory
      [ sparqlite:forPredicate "http://purl.org/NET/Claros/vocab#subject-not-after" 
      ; sparqlite:useDirectory "claros_test_server/WebContent/WEB-INF/data/combined/indexes/term-na"
      ]
    .
```

Dataset descriptor in `WEB_INF/tdb/combined.ttl`):
```
@prefix tdb:       <http://jena.hpl.hp.com/2008/tdb#> .
@prefix rdfs:	   <http://www.w3.org/2000/01/rdf-schema#> .
@prefix rdf:       <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix ja:        <http://jena.hpl.hp.com/2005/11/Assembler#> .
@prefix sparqlite: <http://purl.org/net/sparqlite/vocab#> .

[] ja:loadClass "com.hp.hpl.jena.tdb.TDB" .

<#dataset> rdf:type tdb:DatasetTDB
    # file and directory paths relative to Eclipse workspace directory:
    ; tdb:location           "claros_test_server/WebContent/WEB-INF/data/combined/tdb"
    ; sparqlite:larqLocation "claros_test_server/WebContent/WEB-INF/data/combined/literals"
    .
```