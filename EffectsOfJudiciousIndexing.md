Investigation suggests there are two entangled reasons for the slowness
of the prototypical pottery query. One is that excessive triple searching
resulting from the complexity of the query; the other is the time taken
to do the sorting, much of which is irrelevant given that only one (an
earliest) result is returned.

It turns out that although gYear literals are sorted appropriately
(without the original xsd:integer cast hack), they are (not surprisingly)
not treated specially by TDB's node encoding. That is, while certain
values (smallish integers and dates, for example) are encoded in-line
in node values, gYears are not. The effect of this is that indexing
from gYear objects (a) requires a unconstrained scan of non-inline
index entries and (b) delivers them in pseudo-random order.

If we augment the pottery data with early dates which are encoded as
xsd:integer years rather than gYears, those dates come out of the index
in order. (This assumes that a non-subject index is being used, which
is apparently true if the subject is unknown at the point the indexes
are consulted.)

Thus if basic graph patterns are not being re-ordered, organising the
query so that

```
?s integer-not-before-predicate ?early
```

comes first means that the results will be delivered earliest years first,
making the sort unnecessary. As it happens this will **also** reduce the
searching required in the rest of the query. Taking the **original**
prototypical query, adding this triple at the beginning, removing the
ORDER BY clause, and running it with the cmd.timing test, gives
times of 0.121s (warmup), 0.0080s, 0.0060s, 0.0060s -- all well within
the subsecond target -- and correctly producing an earliest example.

I am a little surprised at how fast this runs, given that eliminating
the sorting alone from the original gives run-times in the seconds not
millseconds, so its possible that there's something horrid going on.
But assuming not, this is very promising.

To make this robust, rather than assuming that ARQ/TDB won't misoptimise
the query at some point, will require adding some specific optimisation
code that blocks any later reordering; my intention is to work towards
optimisations that recognise "order by X: ... P X" where P is a predicate
that we know orders its object, and replace the "... P X" with a
specialised triple pattern structure.

Whether this can be reused in the context of property functions in the
other prototypical query remains to be seen.