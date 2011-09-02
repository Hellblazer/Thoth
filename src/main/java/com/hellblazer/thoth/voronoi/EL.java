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

import static com.hellblazer.thoth.voronoi.Edge.DELETED;
import static com.hellblazer.thoth.voronoi.SFVoronoi.le;
import static com.hellblazer.thoth.voronoi.SFVoronoi.re;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

class EL {
    private HalfEdge[] hash;
    private int hashSize;
    HalfEdge leftEnd, rightEnd;

    EL(int sqrt_nsites) {
        hashSize = 2 * sqrt_nsites;
        hash = new HalfEdge[hashSize];
        for (int i = 0; i < hashSize; i++) {
            hash[i] = null;
        }
        leftEnd = new HalfEdge(null, 0);
        rightEnd = new HalfEdge(null, 0);
        leftEnd.left = null;
        leftEnd.right = rightEnd;
        rightEnd.left = leftEnd;
        rightEnd.right = null;
        hash[0] = leftEnd;
        hash[hashSize - 1] = rightEnd;
    }

    private boolean rightOf(HalfEdge el, float x, float y) {
        boolean right_of_site, above, fast;
        float dxp, dyp, dxs, t1, t2, t3, yl;
        Edge e = el.edge;
        Site topsite = e.reg[1];
        right_of_site = x > topsite.x;
        if (right_of_site && el.pm == le) {
            return true;
        }
        if (!right_of_site && el.pm == re) {
            return false;
        }
        if (e.a == 1.0) {
            dyp = y - topsite.y;
            dxp = x - topsite.x;
            fast = false;
            if (!right_of_site & e.b < 0.0 | right_of_site & e.b >= 0.0) {
                fast = above = dyp >= e.b * dxp;
            } else {
                above = x + y * e.b > e.c;
                if (e.b < 0.0) {
                    above = !above;
                }
                if (!above) {
                    fast = true;
                }
            }
            if (!fast) {
                dxs = topsite.x - e.reg[0].x;
                // joker: update, skip divide by zero 2005/05/27
                // TODO: need to further check what cases could cause divide by
                // 0
                if (dxs != 0) {
                    above = e.b * (dxp * dxp - dyp * dyp) < dxs
                                                            * dyp
                                                            * (1.0 + 2.0 * dxp
                                                               / dxs + e.b
                                                                       * e.b);
                } else {
                    above = false;
                }
                if (e.b < 0.0) {
                    above = !above;
                }
            }
            ;
        }
        // e.b==1.0
        else {
            yl = e.c - e.a * x;
            t1 = y - yl;
            t2 = x - topsite.x;
            t3 = yl - topsite.y;
            above = t1 * t1 > t2 * t2 + t3 * t3;
        }
        return el.pm == le ? above : !above;
    }

    HalfEdge gethash(int b) {
        HalfEdge he;
        if (b < 0 || b >= hashSize) {
            return null;
        }
        he = hash[b];
        if (he == null || he.edge != DELETED) {
            return he;
        }
        /* Hash table points to deleted half edge. Patch as necessary. */
        hash[b] = null;
        return null;
    }

    // change arg2 to newH
    void insert(HalfEdge lb, HalfEdge newH) {
        newH.left = lb;
        newH.right = lb.right;
        lb.right.left = newH;
        lb.right = newH;
    }

    HalfEdge left(HalfEdge he) {
        return he.left;
    }

    HalfEdge leftbnd(float x, float y, float xmin, float deltax) {
        int i, bucket;
        HalfEdge he;
        /* Use hash table to get close to desired HalfEdge */
        bucket = (int) ((x - xmin) / deltax * hashSize);
        if (bucket < 0) {
            bucket = 0;
        }
        if (bucket >= hashSize) {
            bucket = hashSize - 1;
        }
        he = gethash(bucket);
        if (he == null) {
            for (i = 1; true; i++) {
                if ((he = gethash(bucket - i)) != null) {
                    break;
                }
                if ((he = gethash(bucket + i)) != null) {
                    break;
                }
            }
        }
        /* Now search linear list of HalfEdges for the correct one */
        if (he == leftEnd || he != rightEnd && rightOf(he, x, y)) {
            do {
                he = he.right;
            } while (he != rightEnd && rightOf(he, x, y));
            he = he.left;
        } else {
            do {
                he = he.left;
            } while (he != leftEnd && !rightOf(he, x, y));
        }
        // Update hash table and reference counts
        if (bucket > 0 && bucket < hashSize - 1) {
            hash[bucket] = he;
        }
        return he;
    }

    HalfEdge right(HalfEdge he) {
        return he.right;
    }
}
