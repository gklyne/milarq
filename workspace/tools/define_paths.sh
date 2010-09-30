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
# Also defines function classpath for use by the "process..." scripts,
# and commands for tdbquery, tdbloader, tdblarqindex
#

# Can override the following by setting JAVA_BIN externally
SYSTEM="$(uname -s)"
if [ "$SYSTEM" == "Linux" ]; then
    # Linux
    PATHSEP=':'
    SEDE="sed -r"
    JAVA_BIN=${JAVA_BIN:-java}
elif [ "$SYSTEM" == "Darwin" ]; then
    # MacOS
    PATHSEP=':'
    SEDE="sed -E"
    JAVA_BIN=${JAVA_BIN:-/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Commands/java}
elif [ "$SYSTEM" == "Cygwin" ]; then
    # Windows+Cygwin
    PATHSEP=';'
    SEDE="sed -r" # TODO: is this right?
    JAVA_BIN=${JAVA_BIN:-java}
else
    echo "Unrecognized system type: $SYSTEM"
    exit 1
fi

# Can override by setting WORKSPACE_ROOT externally
WORKSPACE_ROOT=${WORKSPACE_ROOT:-~/workspace}

# Can override by setting MILARQ_PROJECT externally
MILARQ_PROJECT=${MILARQ_PROJECT:-googlecode_milarq}
MILARQ_ROOT=${WORKSPACE_ROOT}/${MILARQ_PROJECT}

# Derived values
MILARQ_CLASSES=$MILARQ_ROOT/webapp/WEB-INF/classes
MILARQ_LIBS="`echo $MILARQ_ROOT/webapp/WEB-INF/lib/*.jar | $SEDE 's/ /:/g'`"
CP=$MILARQ_CLASSES:$MILARQ_LIBS
JAVA="$JAVA_BIN -Xmx1024M -server -cp $CP"

# echo "The script you are running has basename `basename $0`, dirname `dirname $0`"

# From http://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
function real_path ()
    {
    python -c 'import os,sys;print os.path.realpath(sys.argv[1])' $1
    }

which realpath || alias realpath='real_path'

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

function cmd.query()
    {
    $JAVA cmd.query "$@" | grep -v "^[0-9][0-9]:[0-9][0-9]:[0-9][0-9] WARN"
    }

# End.
