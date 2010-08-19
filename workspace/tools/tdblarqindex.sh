# Create TDB LARQ indexes
# e.g.
#   tdblarqindex.sh lgpn/tdb lgpn/literals lgpn/subjects
#

export MILARQ_ROOT=${milarq:-~/workspace/googlecode_milarq}
export MILARQ_CLASSES=$MILARQ_ROOT/webapp/WEB-INF/classes
export CP="`echo $MILARQ_ROOT/webapp/WEB-INF/lib/*.jar | sed -e 's/ /:/g'`"
export CP=$MILARQ_CLASSES:$CP
export JAVA="/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Commands/java -Xmx1024M -server -cp $CP"

$JAVA uk.ac.ox.zoo.sparqlite.CreateLARQIndex "$@"
