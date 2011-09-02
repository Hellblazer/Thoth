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

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

public class Vertex implements Comparable<Vertex> {
    protected float x, y;

    public Vertex(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Vertex o) {
        if (y < o.y) {
            return -1;
        }
        if (y > o.y) {
            return 1;
        }
        if (x < o.x) {
            return -1;
        }
        if (x > o.x) {
            return 1;
        }
        return 0;
    }

    public float distance(Vertex s) {
        float dx, dy;
        dx = x - s.x;
        dy = y - s.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distanceSquared(float x2, float y2) {
        float dx, dy;
        dx = x - x2;
        dy = y - y2;
        return dx * dx + dy * dy;
    }

    public boolean overlaps(Vertex center, int radius) {
        return distance(center) <= radius;
    }
}
