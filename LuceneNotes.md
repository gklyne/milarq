

# Introduction #

Discussion with Andy this morning led me to realize that I've made some assumptions about Lucene indexing that are not obviously true.  So I thought I should do some investigating.

Specifically, I'm assuming that a Lucene index can be created that (a) returns values in sorted order, and (b) can be keyed by multiple values (like an SQL composite key).

Details from my investigation are below, but to summarize: I think I'm seeing the required capabilities here.

Official documentation fo9r Lucene is at:
  * http://lucene.apache.org/java/3_0_1/

# Details #

<blockquote>
In Lucene, fields may be stored, in which case their text is stored in the index literally, in a non-inverted manner. Fields that are inverted are called indexed. A field may be both stored and indexed.<br>
<br>
The text of a field may be tokenized into terms to be indexed, or the text of a field may be used literally as a term to be indexed. Most fields are tokenized, but sometimes it is useful for certain identifier fields to be indexed literally.<br>
</blockquote>
-- http://lucene.apache.org/java/3_0_1/fileformats.html#Definitions (sounds promising)

and:
<blockquote>
Each segment index maintains the following:<br>
<br>
Field names. This contains the set of field names used in the index.<br>
</blockquote>
-- ibid. (sounds like a composite key capability)

I also see:
<blockquote>
However, sorting by fields is fairly easy. Make sure the desired field is indexed, and create a org.apache.lucene.search.Sort object to be passed as part of the search criteria; most search methods have an overload that accepts a Sort instance.<br>
</blockquote>
-- http://stackoverflow.com/questions/342966/grouping-lucene-search-results

More here:
  * http://marc.info/?l=lucene-user&m=124104028303394&w=2
  * http://blog.tremend.ro/2007/05/17/a-z-0-9-custom-sorting-in-lucene/
  * http://www.amazon.co.uk/dp/1932394281/?tag=sollc-gb-20

Sorting with multiple fields described here:
  * http://www.mail-archive.com/lucene-user@jakarta.apache.org/msg10277.html

I think I read somewhere along the way that these Lucene sorts are done in memory at query time, which may be not ideal, but working from limited data, that should be a lot faster than the present mechanisms.

I also saw some suggestions that the ordering can be (is usually?) determined at index building time - something to do with document insertion order.

On the other hand, these suggest that sorting is based on Lucene indexes:
  * http://lucene.apache.org/java/3_0_1/api/core/org/apache/lucene/search/Sort.html
  * http://lucene.apache.org/java/3_0_1/api/core/org/apache/lucene/search/SortField.html

# Additional notes #

Andy Seaborne notes that TDB has sorted indexes with composite keys for certain understood values: integers, dateTime/dates.  The underlying B+Tree system could be used to create sorted indexes over other things (the trees are key-sorted but keys are fixed length).

I have no objection to using the TDB indexes instead of Lucene if it gets us the results we need.  In the long run I'd like to be able to use a simple Web interface to, e.g. SOLR, but that's not a requirement for this project.