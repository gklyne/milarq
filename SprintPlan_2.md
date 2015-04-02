

# Introduction #

The main goals for this sprint are:

## Current milestone ##

Elements from original project plan not previously completed:

| Month | Activity | Deliverables |
|:------|:---------|:-------------|
| 1 | Discussion with Epimorphics about engagement with the open source Jena project. Analyze existing Jena/LARQ code and plan changes. | Reviewed implementation plan documents. |
| 2 | Agree initial functionality with Epimorphics. Outline test cases for multiple indexes. Code initial test cases. | Initial test cases run-able (not expected to pass). |

## Current sprint ##

| Sprint 2 | 12 Apr - 30 Apr | [sprint 2 plan](SprintPlan_2.md) |
|:---------|:----------------|:---------------------------------|

Previous sprint plan: [SprintPlan\_1](SprintPlan_1.md).

Project outline plan: [ProjectPlanOutline\_201003\_201010](ProjectPlanOutline_201003_201010.md).

# Activities #

## Project management ##

  * **Done**: Sprint plan: [SprintPlan\_2](SprintPlan_2.md)
  * **Done**: Meeting with Epimorphics: [20100416\_Meeting\_Project\_KickOff](20100416_Meeting_Project_KickOff.md)
  * Fix date for meeting to review experiments
  * Check payment schedule and details (noting that Epimorphic's effort is front-loaded in the schedule)
  * Check code copyright status

## Technical plan ##

Initial focus will be on two specific queries documented in project wiki [MILARQ\_evaluation\_testing](MILARQ_evaluation_testing.md)

This month, perform scoping experiments with the current data query service (CJD):
  * Update to latest versions of Jena/ARQ/LARQ/TDB
  * Query pruning and reorganization
  * Materialize indirect properties
  * Test for ordering in TDB indexes
  * Additional / specialized Lucene index
  * ARQ investigations; esp., w.r.t. tracing of ordering information
  * Composite keys, including use with GROUP BY, COUNT queries
  * Interplay between filtering and sorting - TDB count handling (?)
  * _etc._

Review application of techniques suggested by scoping experiments to other queries in test suite (GK)

Evaluate results of scoping experiments and make decisions on future work at a follow-up meeting.

## Implementation plan ##

To be determined when reviewing the scoping experiments.

  * Identify affected system components
  * Sketch proposed changes
  * Sketch additional test cases

## CLAROS test environment ##

Enhance test environment with latest LIMC data (GK):
  * Obtain latest partner RDF data
  * Reload data into CLAROS test environment
  * Re-run all test cases

NOTE: see http://code.google.com/p/sparqlite/wiki/SparqliteWithEclipse and other wiki pages at http://code.google.com/p/sparqlite/ for setup notes.

# Sprint review notes #

Productive meeting with Epimorphics: [20100416\_Meeting\_Project\_KickOff](20100416_Meeting_Project_KickOff.md).  Based on discussions, the focus for this sprint will be to perform a series of scoping experiments to better understand the options for query performance improvement.  We will meet when these are done to discuss further work.

## End-of-sprint review ##