

# Introduction #

This page contains notes for ourtstanding issues and further work relating to the MILARQ project.  Not all of these are intended to be addressed within the current project.

# Outstanding issues #

## Eliminate "server error" failures ##

Known problems here are covered in the issues list.  Currently a query to a genericIndex in which a query object variable is bound causes a server error.

## Missing date values ##

Handle missing date values in the order-by-date indexes, and don't bind query variables in these cases.

## Place names containing spaces ##

Consider removing spurious spaces on data ingest/export. (Not MILARQ issue)

## Result order tracking ##

Currently, in order to benefit from MILARQ ordered indexing performance, ORDER BY clauses have to be omitted from the queries, which means the desired effect is not in line with actual SPARQL result specifications.  It would be good to alloew appropriate ORDER BY lclauses to be present without causing unnecessary re-sorting of the result data.  I think the analysis required for this can be reasonably simple, but it has to be in the heart of the query processor.

## MILARQ index format ##

Currently, MILARQ ordered indexes are stored as directories with separate files for each keyword.  This works well enough, but results in very large directories, which in turn greatly slow down normal file handling operations (e.g. an Eclipse refresh operation can take several minutes).

I'm wondering if the directories might reasonably be replaced by a simple key-value store, such as REDIS.

Another alternative might be to use SOLR, but that may suffer from the same sorting-scalability problem as Lucene.

## Range queries and other specialized indexes ##

Currently, date-range queries are handled quite inefficiently through FILTER expressions.  Although this is a filter on a linear value, the query has two degrees of freedom (start, end), and might b better considered a form of spatial query.

The index configuration facilities built into MILARQ should make it relatively easy to incorporate new index classes, such as R-tree for spatial searches.

Note: currently, the additional overhead of filtering for date-range constrains does not appear to be a dominant performance factor.

## Source data handling ##

Numerous problems, including:
  * handling zero years
  * handling wrongly formatted years
  * location names containing spaces (trim for short-circuit properties)

# Other notes #

## OPTIONAL in braces doesn't work as might be expected ##

Moved to: [SparqlNotes#OPTIONAL\_in\_braces\_doesn't\_work\_as\_might\_be\_expected](SparqlNotes#OPTIONAL_in_braces_doesn't_work_as_might_be_expected.md)