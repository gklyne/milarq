

# Introduction #

THis meeting is called to review progress, especially with respect to deploying MILARQ updates to implement an improved CLAROS query server, and to determine actions to be completed in winding up the project.

When: 30-Jul-2010

Where: Epimorphics' office, Portishead

Who: GK, EH

# Goals for the day #

  * Review progress achieved and use of MILARQ capabilities (did I miss any tricks?)
  * Outline remaining development and documentation activities to project completion
  * Identify possible useful follow-on activities

# Agenda #

  * Agenda bashing
  * Review previous meeting notes (10 mins)
  * Review work to date: are any obvious techniques being overlooked?
    * performance results (ClarosServerPerformanceNotes),
    * data preparation steps (`claros_demo_server/WEB_INF/WebContent/data/makecombineddata.sh`, etc.)
    * revised queries (`Claros_query_test/src/claros/tests/querydemoserver/TestDemoQueries-Milarq-8-Counting.py`)
  * Discussion with Andy about SPARQL queries
  * Review outstanding issues in issue tracker (30 mins)
  * Review outstanding issues list (OutstandingIssues); triage and allocate activities 1-2 hours
  * Possible follow-on activities: paper? new projects? incorporate into Jena codebase? multilingual search? more? (30 mins)
  * Synchronize with Martin
  * Wrap up: review meeting goals (10 mins)

(Where I've allowed extended periods for an agendum, I regard it's fair game to actually _do_ simple changes rather than just talk about them.)

# Details #

## Review previous meeting ##

  * Decide how to configure the property functions and indexes so that new Java code doesn't have to be written just to change a configuration
  * Suggest Java API for configuration which is invoked in the context (of a particular application?)
    * both done or covered
  * cmd.query to have prefixes file option and to allow query to come from a file or stdin (to facilitate scripting of new "rules" based on CONSTRUCT queries)
    * done, but so far not used
  * put new code in a shared MILARQ Google Code repository
    * done - all CLAROS server projects use all code directly from MILARQ project
  * fix name issues in test files
    * no longer an issue (done?)
  * review term extraction algorithm/interface
    * not done.  Probably not needed now - planning to reduce use of new indexes to controlled keyword fields (shape, type, material, etc.)
  * check year extraction code in process.sh
    * some work, but still outstanding issues
  * review use of ?g in queries
    * relevant features are working - no outstanding problems here
  * produce HOWTO for new config options
    * notes collected ( http://code.google.com/p/milarq/wiki/ServerConfiguration ) but still lacking coherent documentation.

Actions are substantially complete, overtaken by events, or remain on the current agenda.

## Review performance results and new queries ##

Generally, the results (20x overall on test suite) are very satisfactory, with some queries 100x faster.

A summary of the progress on performance is at http://milarq-announce.blogspot.com/2010/09/milarq-progress-update.html.

Evaluation to date has followed the evaluation plan to the extent of using the query test suite.  We have not yet evaluated a fully deployed CLAROS explorer application - this will require cooperation with other CLAROS project partners, and is planned to be done by the end of November.

The work has exposed the existence of a number of techniques for significantly improving query performance that do not depend on the new indexing structures.  However, some of the greatest gains on individual queries are due to the new indexes.  It remains unclear how important these individual gains are with respect to overall application performance.

We did not discover any important options for performance improvement that have been overlooked.

## Review SPARQL queries with AndyS ##

(See issue http://code.google.com/p/milarq/issues/detail?id=9 in MILARQ project)

Ask about query algebra - OPTIONAL and FILTER **are** affected by being enclosed in braces, because of the way that braces affect visibility of bindings from other graph patterns.

`Join(P,Ljoin({},Q)` is not the same as `Ljoin(P, Q)`.  In order to provide a result, the former case requires that if there is any match for `Q`, then at least one must be compatible (in the sense of variables binding to the same values) with `P`.  If `Q` matches, but only with incompatible variable bindings to `P`, then no result is returned.

Comparable concerns apply for FILTER enclosed in braces as opposed to not enclosed in braces.

Using BNODE in different rows (variable bindings) of a result causes each to have a new bnode value.  The query needs a structure that generates each bnode just once for each combination of values, and associates it with all appropriate subjects.

## Outstanding issues ##

See: http://code.google.com/p/milarq/wiki/OutstandingIssues

### Eliminate "server error" failures ###

See issue http://code.google.com/p/milarq/issues/detail?id=8 - intend to fix.

### Missing date values ###

See issue http://code.google.com/p/milarq/issues/detail?id=5 - intend to fix.

### Place names containing spaces ###

Not a MILARQ issue - CLAROS data ingest improvements required.

### Result order tracking ###

Allow inclusion of ORDER BY modifier without losing optimizations achieved by ordered index use, so the SPARQL query semantics match the operational semantics.

Not essential for MILARQ, but nice-to-have, and may progress (with Andy's co-operation) as a background activity for Jena/ARQ.

### MILARQ index format ###

No time to re-work within the MILARQ project.  May look to extract generic interface, then provide alternative implementations as part of ongoing efforts.

### Range queries and other specialized indexes ###

No time to address within MILARQ.  May look to implement as part of further work.

### Double-counting ###

This is critical to resolve, but after TDB update work during today's meeting, most of the required tooling seems to be in place.  Some further occasional assistance may be needed.  Also, there is a problem of SPARQL 1.1 facilities not being quite sufficient for the particular problem.  GK will formulate follow-up comment to the DAWG.

## Follow-on activities ##

When Jena becomes a free-standing OSS project, review incorporation of MILARQ code and/or functionality into Jena.

In future, possible discussion about new projects involving Jena enhancements

## Summary of actions ##

ACTION EH: fix two problems noted in issue list

ACTION EH: extract generic interface for keyword access to stream of values

ACTION GK: create CLAROS query server based on TDB 0.8.8. snapshot

ACTION GK: push through resolution to counting query problem (see issue list)

ACTION GK: message to DAWG about ORDER BY support in GROUP\_CONCAT

ACTION EH: discuss result ordering tracking to allow ORDER BY modifier in limited circumstances without loss of optimization

ACTION EH: produce coherent HOWTO documentation for index creation, configuration and use, based on existing notes

ACTION GK/EH: future discussion about and even preparation of a paper for publication somewhere (where?)

ACTION GK: consider LDA for CLAROS access