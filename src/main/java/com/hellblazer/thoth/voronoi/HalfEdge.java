/**
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

import static com.hellblazer.thoth.voronoi.SFVoronoi.le;
import static com.hellblazer.thoth.voronoi.SFVoronoi.re;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

class HalfEdge {
    Edge edge;
    HalfEdge left, right;
    HalfEdge next;
    int pm;
    Vertex vertex;
    float ystar = 0.0f;

    public HalfEdge() {
    }

    public HalfEdge(Edge e, int pm) {
        edge = e;
        this.pm = pm;
    }

    void ELdelete() {
        left.right = right;
        right.left = left;
        edge = Edge.DELETED;
    }

    Site leftreg(Site bottomsite) {
        if (edge == null) {
            return bottomsite;
        }
        return pm == le ? edge.reg[le] : edge.reg[re];
    }

    Site rightreg(Site bottomsite) {
        if (edge == null) {
            return bottomsite;
        }
        return pm == le ? edge.reg[re] : edge.reg[le];
    }
}
