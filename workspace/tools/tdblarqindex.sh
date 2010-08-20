# Create TDB LARQ indexes
# e.g.
#   tdblarqindex.sh lgpn/tdb lgpn/literals lgpn/subjects
#

if [ "$1" == "" ]; then
    echo "usage tdblarqindex.sh tdbdir literalindexdir subjectindexdir"
    exit 0
fi

source `dirname $0`/define_paths.sh

$JAVA uk.ac.ox.zoo.sparqlite.CreateLARQIndex "$@"
