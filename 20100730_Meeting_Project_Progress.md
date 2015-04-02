

# Introduction #

This meeting was arranged primarily to provide technical coordination between Epimorphics and Oxford.  Progress over the preceding period had been distracted by a number of issues, and synchronization of activities and goals had become somewhat de-focused.

The original intent from our previous meeting was that by the time we met in July, we would have software and supporting structures in place to allow GK to start work on trialling the accelerated query service within the CLAROS query service and test suite.  In the event, though much of the technical work underpinning accelerated queries had been completed, the goal of having components ready for deployment in the CLAROS query service had not been achieved.

The meeting took place just ahead of GK being away on vacation for 2 weeks.

When: 30-Jul-2010

Where: Epimorphics' office, Portishead

Who: GK, EH

# Goals for the day #

  * To start deploying the query acceleration tools within the CLAROS query service, _or_ to articulate what additional steps were needed to achieve this, with the goal thatr GK could start dep
  * To articulate additional activities that we might wish to undertake during the final stages of the project.

# Agenda #

Having articulated our goals, we did not formulate a detailed agenda in advance, which in hindsight might have been sub-optimal.  Roughly, the course of the day was intended to be:
  * review the technical progress to date
  * start pair-working on applying the technical solutions to the CLAROS query service

# Details #

Much of the day was spent reviewing technical progress, during which a number of technical problems were uncovered and some of which were fixed.  This meant the technical review took longer than anticipated, and no time was available for making a start on applying the technical solutions to the CLAROS query service.

Despite the slower than anticipated  progress of our meeting, the review session was very useful, as it served to provide an opportunity to GK to gain some more detailed insight into the elements of the proposed query acceleration mechanisms.

## Progress since June ##

As noted above, recent progress has been impeded by a number of distractions.

Since our previous meeting on 11 June 2010:

EH had created a suite of query enhancement and testing tools:
  * use of tdbquery and tdbloader to augment the CLAROS data with "fast" properties that could be queried using fewer joins (one of the technical problems that had been identified as slowing down queries).
  * a new command-line query tool for extracting key indexing information from the TDB database, coupled with some new tools to generate file-based indexes.
  * ARQ property functions to read the new indexes and feed results into an ARQ query.
  * submit test queries, extract results and generate reports of query performance, which will form the evidential base for specific query acceleration strategies to be implemented in the CLAROS query service.

GK had replicated the revised query service, using later versions of Jena, ARQ and TDB, and updated the test suite to work with the revised service.  He had also spent some time loading revised IMC data into the revised query service, and identified some problems with the conversion of LIMC data.

## Articulate status ##

## Articulate next steps ##

  * Decide how to configure the property functions and indexes so that new Java code doesn't have to be written just to change a configuration
  * Suggest Java API for configuration which is invoked in the context (of a particular application?)
  * command.query to have prefixes file option and to allow query to come from a file or stdin (to facilitate scripting of new "rules" based on CONSTRUCT queries)
  * put new code in a shared MILARQ Google Code repository
  * fix name issues in test files
  * review term extraction algorithm/interface
  * check year extraction code in process.sh
  * review use of ?g in queries
  * produce HOWTO for new config options

### Higher priorities ###

  * Isolate the configuration API so that server configuration code can be in one place
  * Code and HOWTO to the point that GK can start to create CLAROS service using new accelerated queries
  * We aim to talk again - by teleconference and/or another face-to-face meeting - when GK returns from holiday in just over 2 weeks, to coordinate implementation by GK of the revised query structures in the CLAROS query service.  In particular, GK has allocated a window for some focused development work in the 3rd week of August.

### Lower priorities ###

  * Implement non-Java configuration file for configuring indexes.

### Other issues ###

  * Multilingual search terms: we confirmed that this was out-of-scope for the current project, and that this was something that was being developed separately by the CLAROS partners.

## Summary of actions ##

  * ACTION EH: fix technical problems uncovered
    * _details...??_
  * ACTION EH: create tools for integrating new indexes into the CLAROS query service
    * initially, develop APIs for registering new ARQ property functions for the new indexes
    * later, if possible, create a configuration framework for adding new indexes without modifying the query service Java code
  * ACTION EH: create documentation in HOWTO-type format for creating and integrating additional fast functions
  * ACTION GK: on return from vacation, implement new structures in CLAROS query service and test suite