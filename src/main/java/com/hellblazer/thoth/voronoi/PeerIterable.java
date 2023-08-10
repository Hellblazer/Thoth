package com.hellblazer.thoth.voronoi;

import java.util.Iterator;

import com.hellblazer.thoth.impl.Peer;

public class PeerIterable implements Iterable<Peer> {
    private Iterator<Site> iterator;

    public PeerIterable(Iterator<Site> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<Peer> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Peer next() {
                return iterator.next().peer;
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
}
