

# Introduction #

This is the final progress report for the MILARQ project, following the template at http://code.google.com/p/jiscri/wiki/ProjectDocumentation.

The initial scoping experiments to analyze performance of the original queries, and test possible ways to improve them, took somewhat longer than allowed in the original plan.  As a result, deployment of the updated final deployment started later than planned, and is still (as of 2 November) being undertaken by the CLAROS project partners.  Thus, the concrete results from this project are currently technical in nature (i.e. how to make faster SPARQL queries), rather than deployed end-user benefits (i.e. a faster deployed CLAROS Explorer).  Although the project has ended, the CLAROS partners are committed and actively working to deploy the improved query service, along with a general update of the source data to address other problems noted during the MILARQ project work.

# MILARQ: Improving SPARQL query performance in CLAROS VRE #

## Title of Primary Project Output ##

MILARQ: Improving SPARQL query performance in CLAROS VRE

## Screenshots or diagram of prototype ##

The CLAROS Explorer application looks like this:

![http://milarq.googlecode.com/hg/docs/20101102-CLAROS-Annotated-Screenshot.png](http://milarq.googlecode.com/hg/docs/20101102-CLAROS-Annotated-Screenshot.png)

The illustrated application allows researchers to explore objects described in data from all the contributing CLAROS partners.  Most of the panes in the application display are populated using SPARQL queries to a triple store that contains selected data extracted from all the data-providing partners.  Some of the quereis are quite complex and slow, causing the user application to be sluggish or worse.  The Goal of MILARQ has been to improve the performance of queries underpinning this application, to raise the level of the user experience.

## Description of Prototype ##

CLAROS (Classical Art Research Online Services; http://www.clarosnet.org/) provides search and discovery services over datasets that have been integrated, using Semantic Web technologies, from leading independent European research centres.  The data web created from these datasets includes descriptions of classical art objects (pots, gems, statues, etc.) located in museums worldwide.

The outcome of this project has been a revised query server that improves perfotrmance of the underlying SPARQL queries so that we can deploy a publicly accessible Virtual Research Environment, the CLAROS Explorer (see diagram above), to be used by academic researchers, educators, students and members of the public to access information about objects from classical antiquity, and their relationships with classical mythology.

The project developments relate to Jena, an existing, widely used, open source Semantic Web data management platform.  These include creation of multiple indexes over the underlying RDF triple store, Jena TDB, and other optimizations relating to query performance. Overall, we have been able to achieve a factor of 20 performance improvement over a test suite of queries used in the CLAROS Explorer. Details of the techniques used, and results achieved, are reported separately (http://milarq-announce.blogspot.com/2010/09/milarq-progress-update.html) .

We are now working with CLAROS project partners to deploy the changes into the CLAROS system.

## End User of Prototype ##

When deployed, we expect the CLAROS system to be used by academic researchers, educators, students and members of the public to access information about objects from classical antiquity, and their relationships with classical mythology.

## Link to working prototype ##

  * http://www.clarosnet.org/xdb/ASP/clarosPost.asp

NOTE: due to performance and server load problems, this prototype is often unavailable.  The improved version, when available, should be very much more stable.

  * http://imageweb.zoo.ox.ac.uk/pub/2009/presentations/claros-screencast.mov - a screencast of the prototype in action.

## Link to end user documentation ##

  * http://www.clarosnet.org/about/default.htm

NOTE: this links to the CLAROS project home page, of which MILARQ is a small component to improve performance of the intended public CLAROS explorer service.

## Link to code repository or API ##

The main source code is a refactored and enhanced version of SPARQlite.  Additional code provided includes utilities for building and testing the test database.

  * http://code.google.com/p/milarq/source/checkout (page with details for mercurial checkout of source code)
  * http://code.google.com/p/milarq/source/browse/ (browse the source code)
    * http://code.google.com/p/milarq/source/browse/#hg/src (browse MILARQ query server code)
      * http://code.google.com/p/milarq/source/browse/#hg/src/cmd (browse utilities used for building specialized indexes)
    * http://code.google.com/p/milarq/source/browse/#hg/webapp (browse deployable test server with sample data from SPARQLite)
      * http://code.google.com/p/milarq/source/browse/#hg/webapp/WEB-INF/lib (browse Jena and other support libraries needed for a deployable SPARQL query servlet)
    * http://code.google.com/p/milarq/source/browse/#hg/workspace (browse workspace used for query performance experiments)
      * http://code.google.com/p/milarq/source/browse/#hg/workspace/tools (browse shell scripts and related code used for building tripe store and indexes and running test queries - makes extensive use of TDB command line utilities, and some hand)

NOTE: these link to the code repository for the query system to which changes have been made as part of this project, not to the full CLAROS VRE application.

Links to non-public CLAROS code repositories (it is hoped that in due course these will be made public, but there is a lot of CLAROS partner raw data that we do not yet have permission to release):
  * http://code.google.com/p/milarq/wiki/CodeRepositories

## Link to technical documentation ##

  * http://code.google.com/p/milarq/w/list

## Date prototype was launched ##

The CLAROS partners are aiming to have the improved prototype launched by the end of November 2010.   The earlier prototype, which was deemed to be too slow for public use, has been deployed since before the MILARQ project started.

## Project Team Names, Emails and Organisations ##

  * Graham Klyne (PM) <graham.klyne@zoo.ox.ac.uk>, Oxford University, Zoology Department
  * Chris Dollin <chris.dollin@epimorphics.com>, Epimorhics Ltd
  * David Shotton (PI) <david.shotton@zoo.ox.ac.uk>, Oxford University, Zoology Department

## Project Website ##

  * http://code.google.com/p/milarq/

## PIMS entry ##

  * https://pims.jisc.ac.uk/projects/view/1592

## Table of Content for Project Posts ##

  * Summary of query performance improvement results:
    * http://code.google.com/p/milarq/wiki/ClarosServerPerformanceNotes
  * User documentation
    * [milarq.googlecode.com/hg/docs/20101102-CLAROS-Annotated-Screenshot.png](http://milarq.googlecode.com/hg/docs/20101102-CLAROS-Annotated-Screenshot.png) - annotated screenshot
    * _The project itself was focused on improved use of the underlying technologies for an existimng system, so has not produced any new end-user documentation_
  * Requirements and evaluation  criteria:
    * http://code.google.com/p/milarq/wiki/MILARQ_evaluation_testing
  * Technical documentation:
    * http://code.google.com/p/milarq/wiki/ServerConfiguration - how to configure a MILARQ server, including additional specialized index configuration
    * http://code.google.com/p/milarq/source/browse/docs/howto.text - more notes about servlet configuration
  * Technical design notes:
    * http://code.google.com/p/milarq/wiki/SparqlNotes - notes about using SPARQL
    * http://code.google.com/p/milarq/wiki/SomePreliminarySizing - early results from the initial scoping experiments
    * http://code.google.com/p/milarq/wiki/FreeTextProblems - results from an initial investigation of free-text queries with result sorting, reinforcing the hypothesis that some queries need additional indexing to achieve the desired performance levels.
    * http://code.google.com/p/milarq/wiki/EffectsOfJudiciousIndexing - further results from the initial scoping investigations of query performance.
    * http://code.google.com/p/milarq/wiki/LuceneNotes - early notes about the Lucene query engine.  (I originally assumed that additional queries would be build using Lucene, but Lucene itself turned out to have linear (O(N)) performance for sorting by secondary fields, so in the end we used a very simple custom indexing technique: a file-per-keyword with results pre-sorted by the secondary date field.
  * Reflections:
    * http://code.google.com/p/milarq/wiki/ClarosServerPerformanceNotes - summary of techniques for improving query performance, and results
    * http://milarq-announce.blogspot.com/2010/05/progress-review-with-epimorphics.html - summary of early results from scoping experiments
    * http://milarq-announce.blogspot.com/2010/06/milarq-technical-review-and-planning.html - further conclusions from scoping experiments
  * Futures:
    * Unfinished business:
      * http://code.google.com/p/milarq/wiki/OutstandingIssues
  * Project management and administration
    * Project proposal:
      * http://vreri.googlecode.com/files/Bid30%20MILARQ.pdf
    * Project kick-off meeting with Epimorphics: this set the tone for much of the project to follow.  In particular, much more emphasis on an initial investigation (scoping experiments) of query performance and possible mechanisms for improvement:
      * http://code.google.com/p/milarq/wiki/20100416_Meeting_Project_KickOff
      * See also: http://milarq-announce.blogspot.com/2010/04/kick-off-meeting-with-epimorphics.html
      * It's worth noting that we were unable to schedule this until 1.5 months after the original project start.  Something to be taken into account when working with external developers.
    * Project plan and progress (see also the linked sprint plans):
      * http://code.google.com/p/milarq/wiki/ProjectPlanOutline_201003_201010
    * Reflections
      * http://milarq-announce.blogspot.com/2010/06/jisc-project-working-with-outside.html - on working with outside contractors - the value of being exposed to different project management approaches as well as technical skills.
      * http://milarq-announce.blogspot.com/2010/06/at-last-test-cases-pass-with-new-jena.html - remember that data needs configuration management as well as code (reminded the hard way)!
      * http://milarq-announce.blogspot.com/2010/08/milarq-technical-review-and-planning.html - first indications of slippage of deployment of new software in the CLAROS application.
      * http://milarq-announce.blogspot.com/2010/09/milarq-progress-update.html - announcement of query test suite running with new software, but still need to deploy in running CLAROS application.  As I write this, I realize we could have deployed this in a public CLAROS service, but that an additional reason delaying deployment of the public CLAROS application is that there have been changes in the CIDOC/CRM vocabulary which will affect the queries, so we are rolling two sets of changes into a new deployment, which is adding to the time taken to complete the work.
  * Project announcements and discussion:
    * http://milarq-announce.blogspot.com// - project blog

# Tags #

progressPosts, rapidInnovation, JISCRI, JISC, VRERI, finalProgressPost, output, prototype, product, demonstrator, MILARQ, RDF, SPARQL, Performance