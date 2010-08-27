#!/bin/bash
#
# source define_paths.sh
#
# defines a number of useful symbols:
#
# System-dependent path definitions:
#   WORKSPACE_ROOT - base directory containing all projects
#   MILARQ_ROOT    - base directory for MILARQ projects
#   JAVA_BIN       - binary for java environment
#
# System-independent derived paths:
#   MILARQ_CLASSES - base directory for MILARQ project classes
#   MILARQ_LIBS    - classpath JAR files from MILARQ project
#   CP             - classpath for MILARQ utilities
#   JAVA           - command to activiate Java environment with classpath, options, etc.
#
# Also defines function classpath for use by the "process..." scripts
#

# Can override by setting JAVA_BIN externally
# Generic
### JAVA_BIN=${JAVA_BIN:-java}
# MacOS
JAVA_BIN=${JAVA_BIN:-/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Commands/java}
# Linux
### JAVA_BIN=${JAVA_BIN:-/usr/lib/jvm/java-6-sun/jre/bin/java}

# Can override by setting WORKSPACE_ROOT externally
WORKSPACE_ROOT=${WORKSPACE_ROOT:-~/workspace}

# Can override by setting MILARQ_PROJECT externally
MILARQ_PROJECT=${MILARQ_PROJECT:-googlecode_milarq}
MILARQ_ROOT=${WORKSPACE_ROOT}/${MILARQ_PROJECT}

# Derived values
MILARQ_CLASSES=$MILARQ_ROOT/webapp/WEB-INF/classes
MILARQ_LIBS="`echo $MILARQ_ROOT/webapp/WEB-INF/lib/*.jar | sed -e 's/ /:/g'`"
CP=$MILARQ_CLASSES:$MILARQ_LIBS
JAVA="$JAVA_BIN -Xmx1024M -server -cp $CP"

# echo "The script you are running has basename `basename $0`, dirname `dirname $0`"

function classpath()
    {
    echo "$CP"
    }

function tdbquery()
    {
    $JAVA tdb.tdbquery "$@" | grep -v "^[0-9][0-9]:[0-9][0-9]:[0-9][0-9] WARN"
    }

function tdbloader()
    {
    $JAVA tdb.tdbloader "$@"
    }

function tdblarqindex()
    {
    $JAVA uk.ac.ox.zoo.sparqlite.CreateLARQIndex "$@"
    }

# End.
