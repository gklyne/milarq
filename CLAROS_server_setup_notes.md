These notes were copied from the CLAROS Basecamp area for wider access.  Some details are quite specific to the specific server environment we use at Oxford University, but a number of general points are here too.

There are additional relevant notes at:
  * http://code.google.com/p/sparqlite/wiki/SparqliteWithEclipse
  * http://code.google.com/p/sparqlite/
  * etc.

# Testing server in Eclipse environment #

Check out the following projects into an Eclipse workspace:
  * `https://sparqlite.googlecode.com/svn/branches/sparqlite-20090309-0.4-claros`
  * `svn+ssh://milos2.zoo.ox.ac.uk/var/svn/ImageWeb/claros_test_server`
  * `svn+ssh://milos2.zoo.ox.ac.uk/var/svn/ImageWeb/claros_demo_server`
  * `svn+ssh://milos2.zoo.ox.ac.uk/var/svn/ImageWeb/Claros-query-test`

Ensure that the Tomcat server working directory refers to the Eclipse workspace (e.g. /Users/graham/workspace/).
  * right click "Servers > Tomcat" in Eclipse project explorer
  * select "Run as... > run configurations..."
  * in the "arguments" tab, set the working directory value accordingly

Start the Tomcat server

To test, browse to:
  * http://localhost:8080/claros_test_server/
  * In the query pane, uncomment the line `ASK { ?s ?p ?o `}
  * click "Run query"
  * look for HTTP response body containing `{"head":{},"boolean":true`}

## Running the test cases ##

Project Claros\_query\_test contains separate test suites:
  * `src/claros/tests/AllTests.py` runs against the test server containing a small selection of CLAROS data designed to exercise different query types, and completes very quickly.
  * `src/claros/tests/querydemoserver/TestDemoQueries.py` runs against the demo server with full CLAROS data, using a set of query patterns used by the CLAROS explorer.  This provides a basis for performance evaluation as well as dataset checking (the test cases may need updating as new data is added).  This test suite may take some time to run (5-10 minutes against a local server on a MacBook).  This test also logs all queries issued and results returned.  It has been used as the basis for handing over query patterns to the CLAROS Explorer developer.

Look for files `ClarosTestConfig.py` and `ClarosDemoConfig.py` to configure details of the target server to query.

## Troubleshooting ##

When updating an existing project, remember to refresh the project before starting the Tomcat server under Eclipse.

Check the contents of project files `WebContent/WEB-INF/tdb/*.ttl` (e.g. `/Users/graham/workspace/claros_test_server/WebContent/WEB-INF/tdb/combined.ttl`).

# Claros-public: network details #

You can access the VM using the VMware Infrastructure client (or web
access) at the following link (currently university VPN only):
  * "https://vcs.oerc.ox.ac.uk/":https://vcs.oerc.ox.ac.uk/

You can confirm the https / ssl cert. figerprint with the below:
  * SHA1: 0E:8E:E0:B8:3F:40:4B:CB:A6:C2:78:81:EE:20:39:AD:2D:F6:D0:4A
  * MD5:  37:76:B4:75:A1:A4:C1:3D:44:F0:87:BF:AD:C8:A4:A8

Change password:
  * "https://postgrid.oerc.ox.ac.uk/iisadmpwd/aexp2b.asp":https://postgrid.oerc.ox.ac.uk/iisadmpwd/aexp2b.asp
  * (Use domain oerc.ox.ac.uk in the password-change form)

Virtual machine network info:
  * claros-public.oerc.ox.ac.uk
  * address 163.1.125.221
  * netmask: 255.255.254.00
  * Gateway: 163.1.125.254
  * DNS: 163.1.2.1, 129.67.1.1

# Disk layout #

8Gb root partition (/) and 30Gb data partition (/srv).  This is intended to create a separation between the system and the data and associated utilities.  LVM is used so that disks can be extended later, if needed.

Selected home directories are symlinked to data volume (mainly for software/database development)

```
root@claros-public:/srv/home/graham# df -B 1M
Filesystem           1M-blocks      Used Available Use% Mounted on
/dev/mapper/claros--public-root
                          8128       680      7039   9% /
varrun                    1008         1      1008   1% /var/run
varlock                   1008         0      1008   0% /var/lock
udev                      1008         1      1008   1% /dev
devshm                    1008         0      1008   0% /dev/shm
/dev/sda1                  236        26       199  12% /boot
/dev/mapper/claros--public-data
                         30557       173     28845   1% /srv
```

# Software setup #

Operating system Ubuntu 64-bit 8.04 LTS server
  * Installed sshd, restricted remote login to users in group 'remote'
  * Installed denyhosts
  * firewall (ufw) enabled (ufw enable)
  * ssh protocol allowed through firewall (ufw allow ssh)
  * install sun java 6 sdk
  * install tomcat 5.5, configure for and allow port 8080 access
  * install tomcat management/admin utils, create admin user in tomcat-users
  * Edit /etc/init.d/tomcat5.5 script to disable use of security policies
  * LATER: tweak tomcat security descriptor for properly controlled file access (50user.policy)
  * Install Python extras for testing:
```
  sudo apt-get install subversion
  sudo apt-get install python-setuptools
  sudo easy_install simplejson
```
  * Install xauth to allow X-session tunnelling through SSH:
```
  sudo apt-get install xauth
```

# Deploy war file #

  * Use tomcat web interface to upload and deploy war file
  * edit tdb/???.ttl files to use appropriate absolute paths

# TODO #

  * Move tomcat webapps area to /srv
  * Create SVN/HG repository?
  * Install backup software

Maybe:
  * Install X client software
  * Install Eclipse