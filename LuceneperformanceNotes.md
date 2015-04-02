# Introduction #

These notes have been copied from emails between team members about Lucene sorting performance.

# Details #

(From Chris)

So on Wednesday I did an experiment with ordering using
Lucene indexing, the results of which areroughly that (a) it
works (b) it won't scale, as the ordering is done by a sort
on the matching entries, ie will get slower and slower as
there are more entries. Yesterday I did a sketch of some
documentation and today I've been writing a little test
framework, not unakin to your Python stuff I suppose, to
automate running the different queries and generate some
pretty results.