

# Introduction #

This meeting was arranged at relatively short notice (i.e. unplanned) as we were facing some technical questions which we thought would benefit from face-to-face contact.  But we also took the opportunity to treat the meeting as a mini sprint review.

The technical issues concerned (a) some identified optimizations which, while effective on a limited test query, we felt might not be effective across the range of queries CLAROS needed to perform, and (b) difficulties in replicating the updated server environment at Oxford.

When: 11-Jun-2010

Where: Epimorphics' office, Thornbury

Who: GK, EH (with brief appearances by DER, MM and BM)

# Goals for the day #

  * GK to better understand outcomes of query performance investigations
  * Understand advantages and disadvantages of new proposed query patterns w.r.t. CLAROS application
  * Understand how to ensure repeatable results, esp. w.r.t. database build process and data provenance
  * Articulate status and next steps
  * Review GK's problem porting to recent Jena

# Agenda #

  * Review query performance experiments
  * Test queries that may act as counterexamples to results so far
  * Review running of test suite on new data
  * Discuss source of data used to build database
  * Review database build process and scripts
  * Articulate status of project to date
  * Articulate next steps
  * Wrap: review meeting goals and confirm actions
  * Review problems with GK's test installation

# Details #

## Articulate status ##

  * The hypothesis that specialized indexes can resolve query performance problems is supported by some concrete evidence
  * Substantial performance gains, to the extent that sub-second query performance may be achievable on current data, can be realized by materializing query results and appropriate arrangement of the queries used, but will probably not scale well to even larger data volumes (probably linear).  Isolating values from about 66,000 object records takes over 0.5s

  * EH has been able to significantly improve TDB-only query performance using (a) integer indexes (storing gYear values as direct integer properties of the nodes for the described objects), (b) approximating Lucene pf:textMatch by attaching keyword terms directly to the nodes for the described objects.
  * currently, using ORDER BY result modifier can cause severe query degradation where the graph pattern yields many results.  If we can rely on index ordering, this cost can be avoided.
  * query ordering can affect performance, but in ways and directions that may depend on the actual data queried.
  * some of the biggest wins to date come from sub-query result materialization via new properties
  * potential worst-performance cases we examined were running in 0.5-1s duration on the current data using re-arranged queries and materialized sub-query results.

  * with minor changes, the demo server test suite runs successfully against the Chris' database
  * GK has not yet been able to replicate Chris database and test suite results (probably due to source data inconsistencies)
  * we recognize some problems arising from inconsistent use of source data for building the database
  * we believe we have a reasonably straightforward and repeatable process for building the database

## Articulate next steps ##

### Higher priorities ###

  * Assemble exemplar optimized system based on currently identified optimizations - in the near term, this may be  enough for us to go public with CLAROS. (GK to lead)
    * ACTION EH: enhance/augment/replace inference process to generate additional materialized views
    * ACTION GK: establish consistent source dataset, and lodge in svn
    * ACTION EH: create new inferred RDF data set (rdfinf)
    * ACTION EH, GK (separately): create new database from rdfinf data and deploy query service for testing
    * ACTION EH, GK (separately): ensure nee database passes current test suite (with adjustments as agreed)
    * ACTION GK: create new test suite with simplied the query set and test - versions of the existing queries that use the optimized forms against new data

  * Investigate composite indexing (for scaling performance)
    * ACTION EH: investigate use of composite-key indexes via ARQ property functions
    * ACTION EH: investigate existing index machinery options for composite-key indexes (e.g. Lucene, TDB, ...)

  * ACTION EH: update technical investigation notes in wiki pages

  * ACTION EH: get "extras" into version control (Google code for MILARQ suggested)

### Lower priorities ###

  * ACTION EH: get reverse-ordered TDB index working (to give latest dates first)

  * ACTION EH: use Lucene term analysis in place of current crude mechanism for extracting terms

  * ACTION EH: Optimise: ORDER BY ... LIMIT 1 to MIN/MAX query evaluation

## Replicating the new system ##

Problems encountered by GK had already been identified as being at least partially caused by inconsistent source data.  Further investigations showed that the data used for this purpose was not using some of the more recently introduced design patterns, underscoring issues raised earlier about the need to ensure a consistent source of  data for development and testing.  In view of this, GK took a copy of data being used by EH, with a view to using this to replicate the results to date, before moving on to continue testing with updated data.  We agreed that a copy of the source data used for full system and performance testing should be maintained under version control.