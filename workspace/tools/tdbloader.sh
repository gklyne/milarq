# Run tdbloader
# e.g. to create tdb database in lgpn/tdb, loading data from  ~/claros/lgpn-par.rdf:
#   tdbloader.sh -v -loc=lgpn/tdb lgpn/rdf/lgpn-par.rdf

if [ "$1" == "" ]; then
    echo "usage tdbloader.sh [-v] -loc=tdbdirectory rdffile"
    exit 0
fi

source `dirname $0`/define_paths.sh

$JAVA tdb.tdbloader "$@"

# End.
