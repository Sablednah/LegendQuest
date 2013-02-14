package me.sablednah.legendquest.utils;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

public class WeightedProbMap<EltType> {

    private final SortedMap<Integer, EltType> elts = new TreeMap<Integer, EltType>();
    private final Random rand = new Random();
    private int sum = 0;

    // assume: each weight is > 0; there is at least one element;
    // elements should not be repeated
    // ensure: this.elts maps cumulative weights to elements;
    // this.sum is the total weight
    public WeightedProbMap(final Iterable<Pair<Integer, EltType>> weights) {
        for (final Pair<Integer, EltType> e : weights) {
            this.elts.put(this.sum, e.second);
            this.sum += e.first;
        }
    }

    // assume: this was initialized properly (cf. constructor req)
    // ensure: return an EltType with relative probability proportional
    // to its associated weight
    public EltType nextElt() {
        final int index = this.rand.nextInt(this.sum) + 1;
        final SortedMap<Integer, EltType> view = this.elts.headMap(index);
        return view.get(view.lastKey());
    }
}