/*
 * Copyright (C) 2008 Hal Hildebrand. All rights reserved.
 * 
 * This file is part of the Thoth Interest Management and Load Balancing
 * Framework.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.thoth.voronoi;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

class PriorityQueue {
    private int count = 0;
    private HalfEdge[] hash;
    private int hashSize;
    private int min = 0;

    PriorityQueue(int sqrt_nsites) {
        hashSize = 4 * sqrt_nsites;
        hash = new HalfEdge[hashSize];
        for (int i = 0; i < hashSize; i++) {
            hash[i] = new HalfEdge();
            hash[i].next = null;
        }
    }

    private int bucket(HalfEdge he, float ymin, float deltay) {
        int bucket = (int) ((he.ystar - ymin) / deltay * hashSize);
        if (bucket < 0) {
            bucket = 0;
        }
        if (bucket >= hashSize) {
            bucket = hashSize - 1;
        }
        if (bucket < min) {
            min = bucket;
        }
        return bucket;
    }

    void delete(HalfEdge he, float ymin, float deltay) {
        HalfEdge last;
        if (he.vertex != null) {
            last = hash[bucket(he, ymin, deltay)];
            while (last.next != he) {
                last = last.next;
            }
            last.next = he.next;
            count--;
            he.vertex = null;
        }
    }

    boolean empty() {
        return count == 0;
    }

    HalfEdge extractMin() {
        HalfEdge curr;
        curr = hash[min].next;
        hash[min].next = curr.next;
        count--;
        return curr;
    }

    void insert(HalfEdge he, Vertex v, float offset, float ymin, float deltay) {
        HalfEdge last, next;
        he.vertex = v;
        he.ystar = v.y + offset;
        last = hash[bucket(he, ymin, deltay)];
        while ((next = last.next) != null
               && (he.ystar > next.ystar || he.ystar == next.ystar
                                            && v.x > next.vertex.x)) {
            last = next;
        }
        he.next = last.next;
        last.next = he;
        count++;
    }

    HalfEdge min() {
        while (hash[min].next == null) {
            min++;
        }
        return hash[min];
    }
}
