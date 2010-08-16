#!/bin/bash

#
# classpath. must include Jena w/ARQ and TDB, plus the
# additional classes for the index creation utilities.
#
export CP=$(classpath)

#
# tools directory, ie the directory from which this process.sh
# file has been taken. This file can be executed from anywhere
# so long as the parameters have been properly set.
#
export TOOLS=./tools

#
# the TDB stores. the source TDB contains the statements that
# form the claros data; computed information will be written
# to the target TDB. They may be the same, in which case the
# source data will be augmented with the new data -- in this
# case you should probably keep a clean copy of the original
# source somewhere.

export SOURCE_TDB=tdb-augmented 
export TARGET_TDB=tdb-augmented

#
# the directory in which the term/date index files will be
# created. Should be empty.
#

export INDEXES=./indexes

#
# directory in which *.tf testfiles reside, for the production
# of performance reports.
#

export TESTSDIR=./testfiles

#
# directory in which template files (for weave/weavil) are found
#

export TEMPLATES=./templates

#
# working directory for intermediate files; default is current
# directory. edit as it please you.
#
export WORK=.

# -------------------------------------------------------------
# end of settable paramters; should now do some checks.
#

echo TODO == check $WORK is a writable directory
echo TODO == check $INDEXES is an empty writable directory
echo TODO == check that $SOURCE_TDB is a non-empty TDB directory
echo TODO == check that $TARGET_TDB is a writable directory
echo TODO == check that the classpath has some relevant classes

# -------------------------------------------------------------
#
# invoke java with the given classpath
#
export JAVA="java -cp $CP"

#
# generate the (?s claros:has-term ?t) triples from the claros:hasLiteral
# triples. A "term" is a run of letters and hyphens; we could (but are not)
# more sophisticated.
#
function generate_has-term_triples()
    {
    echo :: generating has-term triples ::
    $JAVA cmd.query -tdb $SOURCE_TDB -query "select ?s ?l where {graph ?g {?s claros:hasLiteral ?l}}" \
        | $JAVA cmd.generate_hasterm_triples \
        > $WORK/s-hasterm.ttl
}

#
#
#
function generate_has-type_triples()
    {
    echo :: generating has-type triples ::
    tdbquery --query $TOOLS/has_subject.sparql --loc $SOURCE_TDB > $WORK/s-has-subject.ttl
    }

# 
# generate the (?s claros:not-before/not-after ?year) triples using a
# variant of the original probe queries, and then convert the year
# to a plain integer rather than a gYear. We also recognise the naff
# year "" and replace it with "9999" and dates that have swarf like
# MARCH and 13 are stripped down to their 4-digits-only form. Heuristic
# but it will do.
#
function generate_time-span_triples()
    {
    echo ::generating time-span triples ::
    tdbquery --query $TOOLS/time_span.sparql --loc $SOURCE_TDB \
        | sed -r \
            -e 's/gYear/integer/g' \
            -e 's/"[^"]*(-?[0-9][0-9][0-9][0-9]*)[^"]*"/"\1"/' \
            -e 's/""/"9999"/g' \
            > $WORK/s-notbefore-notafter.ttl
    }

#
# load the generated triples into the target model.
#
function load_generated_triples()
    {
    echo :: loading generated triples into the target ::
    tdbloader --incremental --loc $TARGET_TDB \
        $WORK/s-hasterm.ttl \
        $WORK/s-notbefore-notafter.ttl \
        $WORK/s-has-subject.ttl
    }
#
# extract the term x nb x na data from the enhanced model. We do the sort
# externally to take load of the query engine -- we're only sorting strings.
#
function generate_term-subject-notbefore-notafter()
    {
    echo :: generating term x subject x not-before x not-after cvs file ::
    $JAVA cmd.query -tdb $TARGET_TDB -query "select ?term ?s ?nb ?na where { ?s claros:subject-has-term ?term; claros:subject-not-before ?nb; claros:subject-not-after ?na}" \
        | sort > $WORK/term-subject-nb-na.csv
    }

#
# convert the generated CSV into index files. Each term T causes the files
# T-na and T-nb to be created; -nb is sorted by not-before date, and -na is
# sorted IN REVERSE by not-after date.
#
# existing content for different terms in the indexes directory is not
# disturbed. If you want a fresh bunch of indexes, clear out existing content
# first.
#
function create_index_files()
    {
    echo :: creating index files ::
    $JAVA cmd.generate_sorted_indexes -indexes $INDEXES < $WORK/term-subject-nb-na.csv 
    }

#
# run the non-server performance tests and dump the data in performance-data.ttl.
# formatting the results is done by a separate task, so that the data need only
# be generated once (eg when tinkering with report layout).
#
function run_performance_queries()
    {
    echo :: running performance tests == may take some time ::
    $JAVA cmd.runmanyqueries -from $TESTSDIR/*.tf > $WORK/performance-data.ttl
    }

#
# produce the performance report for the example queries using weavil to expand the
# report template with the data from the performance queries.
#
function produce_performance_report()
    {
    echo :: producing performance report ::
    $JAVA cmd.weavil -from $WORK/performance-data.ttl -template $TEMPLATES/for-primary-example.t.html \
        > example-performance-report.html
    }

#
# run all the nice little functions
#
function run_to_completion()
    {
    echo :: running everything from scratch ::
    generate_has-term_triples
    generate_time-span_triples
    load_generated_triples
    generate_term-subject-notbefore-notafter
    create_index_files
    run_performance_queries
    produce_performance_report
    }
