

# Introduction #

A series of meetings held at Thornbury and Westbury to plan and initiate MILARQ project activities.

Participants over 2 days: GK, CJD, DER. MM, AS

# Goals for the 2 days #

Planning:
  * Outline plan for project
  * Project goals clear
  * Specific plans and goals for the next month

Project management:
  * We all understand what actions are expected to be performed by whom
  * We have mechanisms in place for coordination of activities
  * The process for payment to Epimorphics is understood, OR actions taken to ensure a process will be articulated soon

Technical work:
  * Local data query demo service installed
  * Test software installed and working against local demo service
  * Recreate databases from original RDF source data
  * Articulated technical plan for achieving project goals

# Details #

## 15-Apr-2010: Planning meeting: GK, CJD, DER ##

Brief recap of MILARQ goals and CLAROS system:  to improve CLAROS query performance through judicious use of indexing to the point that we can deploy CLAROS publicly.

Recap of CLAROS query service architecture

Review MILARQ project wiki:
  * ACTION GK: commit access for CJD
  * ACTION GK: prepare new sprint plan based on this meeting
  * ACTION GK: round off previous sprint plan

Review evaluation plan and mechanisms:
  * CLAROS query test suite, possibly enhanced for performance measurement
  * Manual operation of CLAROS explorer

Scope and requirements:
  * Full extent of required query capability is embodied in the test suite
  * Queries can be redesigned as long final answers do not change (or change is agreed as reasonable)
  * Aiming for sub-second responses for all queries.
  * For sustainability, the deployed run-time query-handling system should not embody any customized code (other than configuration files, application-specific inference rules, etc.)
  * ACTION: GK add this requirement to the wiki

Confidentiality:
  * Software will be open source, copyright ownership TBD
  * ACTION GK: check with Oxford if there is any problem with Epimorphics holding copyright of open source code
  * Data provided for testing/evaluation is confidential
  * ACTION  GK: draft and circulate words clarifying status of data

Technical work plan:
  * Initial focus on two specific queries documented in project wiki
  * First month: scoping experiments;  a series of experiments with the current data query service (CJD):
  * Update to latest versions of Jena/ARQ/LARQ/TDB
  * Query pruning and reorganization
  * Materialize indirect properties
  * Test for ordering in TDB indexes
  * Additional / specialized Lucene index
  * ARQ investigations; esp., w.r.t. tracing of ordering information
  * Composites keys, including use with GROUP BY, COUNT queries
  * Interplay bet6ween filtering and sorting - TDB count handling (?)
  * Review application of techniques suggested by scoping experiments to other queries in test suite (GK)
  * Evaluate results of scoping experiments and make decisions on future work at follow-up meeting in about a month's time.

## 15/16-Apr-2010: Test/evaluation environment setup: GK. CJD ##

Set up test environment from Subversion repositories.  See: http://code.google.com/p/milarq/wiki/CLAROS_server_setup_notes

The test data is not stored in the subversion repositories, so needed to be copied from the claros-public demo system, and re-built in the new environment. Preprocessed RDF ("rdfinf") data was used for this purpose, though later raw RDF data was successfully loaded and processed through the CLAROS inference rules.  Loading and processing the data took several hours, including an overnight run of TDBloader.

The target system was 32-bit only, though in the event this did not seem to impact performance too badly (except, perhaps, data loading).  The memory allocated for the JVM had to be reduced.

Ensure test suite runs and passes all tests.  Some configuration problems were noted, but with some judicious hand-edits to the test suite, all tests passed OK.

Rebuild databases from RDF sources (all steps performed and verified, but not quite a complete database build from original RDF sources.)

## 16-Apr-2010: ARQ discussions with Andy Seaborne: GK, CJD, AS ##

Technical discussion about internal structure of ARQ software.  CJD has notes.

We also discussed some other possible opportunities for future collaboration, including packaged ready-to-run Jena-based store and query engine - there seems to be a lot of interest in this in various quarters (which could displace SPARQLite with a more functional platform).

Shuffl-based explorer for data presented via SPARQL.

Building an open source community around Jena.
  * ACTION GK: introduce AS to Ross Gardler to explore ASF possibilities

## 16-Apr-2010: Review:  GK, CJD, DER, MM ##

Project goals clear: yes

Outline plan for project: scoping experiments, then decide on concrete developments

Specific plans and goals for the next month: scoping experiments, then meet again

Actions expected to be performed by whom: CJD will perform scoping experiments and query engine developments; GK will modify test suite with redesigned queries as required, and perform query service evaluation using this and a full CLAROS system.

Coordination of activities: ad-hoc email and electronic communication, with a second meeting to be held in about a month's time.

The process for payment to Epimorphics is understood, OR actions taken to ensure a process will be articulated soon
  * ACTION GK: need to make enquiries at Oxford
  * ACTION GK: initiate raising PO for Epimorphics' work

Local data query demo service installed: yes

Test software installed and working against local demo service: yes

Recreate databases from original RDF source data: yes (mostly)

Articulated technical plan for achieving project goals: yes

## Actions ##

ACTION GK: Google Code project commit access for CJD (**Done**).

ACTION GK: prepare new sprint plan based on this meeting (**Done** - see [SprintPlan\_2](SprintPlan_2.md))

ACTION GK: round off previous sprint plan (**Done** - see [SprintPlan\_1](SprintPlan_1.md))

ACTION GK: add no-custom-code requirement to the wiki (**Done** - see [MILARQ\_evaluation\_testing](MILARQ_evaluation_testing.md))

ACTION GK: check with Oxford if there is any problem with Epimorphics holding copyright of open source code - (question raised, awaiting response)

ACTION GK: draft and circulate words clarifying status of data (**Done - awaiting response**)

ACTION GK: introduce AS to Ross Gardler to explore ASF possibilities (**Done**)

ACTION GK: need to make enquiries at Oxford about Epimorphics payment schedule, noting that their work will be front-loaded in the project schedule. (**Done**)

ACTION GK: initiate raising PO for Epimorphics' work (**Done**)