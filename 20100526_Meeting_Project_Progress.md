

# Introduction #

Meetings with Epimorphics to discuss MILARQ progress and plan next steps

Participants: GK, CJD, DER. MM

# Goals for the day #

Planning:
  * Review scoping experiments, and understand the lessons
  * Decide next steps for MILARQ development
  * Learn enough about the upgrade to more recent Jena to replicate in Oxford

# Details #

## 26-May-2010: Technical meeting: GK, CJD ##

As planned, CJD has been working on a series of experiments to evaluate query performance bottlenecks, and to quantify potential improvements.

As part of this work, Jena libraries were updated to the most recent version.  The process of upgrading the Jena libraries and changing the development system to run a 64-bit environment led to a number of problems which have slowed progress, whose exact cause is not known, but which were resolved by the day of the meeting.  Possible culprits here are the OpenJDK Java environment (as opposed to the Sun Java environment), and Eclipse failing to keep all files properly synchronized.

CJD has written a simple software test environment to facilitate query performance testing and profiling without incurring network traffic and other related server overheads.

### Observations ###

Redundant rdf:type tests are more expensive that I had anticipated (have to retrieve value from index) but still not accounting for big performance factors.

  * TDB stats optimizations do improve query, but not by much (15-20%) (15/18)
  * TDB optimizations on slimmed query were more effective (20-30%) (8/10)
  * TDB optimizations on promoted-values query

With stats optimizer:
  * "original" = original query form against original data
  * "slim" = original query form against original data with redundant elements removed
  * "promoted" = new query form against enhanced data in which relevant values are promoted to appear with properties of the top-level object node.

| | sorted | unsorted | simple-sort (not conversion function) |
|:|:-------|:---------|:--------------------------------------|
| original | 15s | 18ms |  11s |
| slim | 6s | 5ms |
| promoted | 3.5s | 3ms |

Without TDB optimizer:

| | sorted | unsorted | simple-sort (not conversion function) |
|:|:-------|:---------|:--------------------------------------|
| original | 16s | 1.8s | 12.5s |
| slim | 8.5s | 1.5s |
| promoted | 3.5s | 3ms |

Timings do not include query-build or network transmission times.

Additional observations (from previous experiments:
  * profiling - seems to confirm what we already think

### Analysis ###

Redundant filter BGPs do have a significant cost.

"Joins" (etc.): significant cost, improved by using TDB statistical optimizer.
Similar improvement (to optimizer) can be achieved by hand-optimization of queries.

Sorting/ordering: this is the dominant cost (for which we don't yet have an engineering solution) for the expensive queries.

There are some queries that do not involve ordering thatr also do not meet the performance criteria articulated at the project outset, for which solutions should be deployed.

NOTE: this is all for non-LARQ queries

### Decisions - next steps ###

The way forward appears to ensuring that we can provide indexes for the queries that deliver results in appropriate order, and take steps to ensure that ARQ will respect and/or exploit the index ordering.

Next steps:
  * look at current external index mechanisms used by ARQ (currently Lucene plus property function hooks).
  * as a priority, examine ARQ processing with a view to understanding how index ordering info can be used
  * consider how additional indexes may be provided (e.g. Lucene?)
  * consider how additional indexes may be accessed (including in order)
  * decide how extra properties and values are to be generated (preference for extending the existing inference rules)
  * decide how to create new queries to use any new structures (automated query processing not a requirement for this project)

Be aware of:
  * composite keys (e.g, object type "pottery" and date of manufacture)
  * queries involving keyword searches (pots having an association with "Hercules" and date of manufacture)

Coming out of this, we hope for a well-understood and easily implemented plan for software enhancements to deliver the required query performance.

Not addressed above:
  * synchronizing software modules, etc...
  * discuss financial arrangements with Martin
  * rate of burn of allocated effort

ACTION GK: update working copy of CLAROS software to use more recent version of Jena, ARQ, etc. (see notes above), and commit these to Subversion.  This to be basis of new changes.

### Other activities in meeting ###

  * Copy JAR files to GK system
  * Copy "extras" (performance test utility)

### JAR files used in Epimorphics' deployment ###

New Jena Jars:
  * Jena-2.6.2-sources
  * Jena-2.6.2
  * Jena-2.6.2-test-sources
  * Jena-2.6.2-tests
  * arq-2.8.3
  * arq-2.8.3-tests
  * tdb-0.8.5
  * tdb-0.8.5-tests

Also seen in Chris' version of sparqlite > webapp > WEB-INF > lib:
  * antlr-2.7.5
  * commons-logging-1.1.1
  * icu4j\_3\_4
  * iri.jar
  * json
  * junit-4.5
  * log4j-1.2.15
  * lucene-core-2.3.1
  * slf4j-api-1.5.6
  * slf4j-log4j12-1.5.6
  * stax-api-1.0
  * wstx-asl-3.0.0
  * xercesimpl
  * xml-apis

### Other technical notes ###

In original software configuration, JAR files are located:
  * in the SPARQLite project webapp/WEB\_INF/lib directory
  * in the utils directory trees for claro\_test\_server and claros\_demo\_server, (not part of runtime).

Eclipse has dialog box to control deployment as copy to tmp0, or just use in situ.

## 26-May-2010: Review with DER ##

We reviewed outcomes from the technical meeting with DER, who was generally satisfied with the results and next steps.

## 26-May-2010: Review with MM ##

Technical progress and next steps noted.

MM confirmed that the P.O. from Oxford had been received.  We agreed that invoices would be submitted as work progressed.  GK was not aware of any scheduling constraints on the release of payments.

# Actions #

ACTION GK: Write up meeting notes in project wiki, and circulate

ACTION GK: Update version of Jena libraries in CLAROS software running in Oxford, and commit revised code to subversion for Epimorphics to align software versions.