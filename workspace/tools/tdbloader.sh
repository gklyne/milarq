# Run tdbloader
# e.g. to create tdb database in lgpn/tdb, loading data from  ~/claros/lgpn-par.rdf:
#   tdbloader.sh -v -loc=lgpn/tdb lgpn/rdf/lgpn-par.rdf

if [ "$1" == "" ]; then
    echo "usage tdbloader.sh [-v] -loc=tdbdirectory rdffile"
    exit 0
fi

# MILARQ project root directory: default correct for GK's mac}

export MILARQ_ROOT=${milarq:-~/workspace/googlecode_milarq}
export CP="`echo $MILARQ_ROOT/webapp/WEB-INF/lib/*.jar | sed -e 's/ /:/g'`"
export JAVA="java -Xmx1024M -server -cp $CP"
export TDBLOADER="$JAVA tdb.tdbloader"

$TDBLOADER "$@"

# End.

#export CP="`echo $MILARQ_ROOT/webapp/WEB-INF/lib/*.jar | sed -e 's/ /:/g'`:$MILARQ_ROOT/webapp/WEB-INF/classes"

