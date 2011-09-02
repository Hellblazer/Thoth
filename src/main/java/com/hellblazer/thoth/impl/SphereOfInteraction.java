/**
 * Copyright (C) 2009 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.thoth.impl;

import java.util.Collection;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3i;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

public interface SphereOfInteraction {

    /**
     * returns the closest node to a point
     * 
     * @param coord
     * @return
     */
    Peer closestTo(Point3i coord);

    /**
     * Answer the Peer aliased to the Peer
     * 
     * @param peer
     * @return
     */
    Peer getAliased(Peer peer);

    /**
     * get a list of enclosing neighbors
     * 
     * @param id
     * @return
     */
    Collection<Peer> getEnclosingNeighbors(Peer id);

    Iterable<Peer> getPeers();

    /**
     * TODO temporary
     * 
     * @return
     */
    List<Point2d[]> getVoronoiDomainEdges();

    /**
     * @param peer
     * @return
     */
    boolean includes(Peer peer);

    /**
     * insert a new site, the first inserted is myself
     * 
     * @param id
     * @param coord
     */
    void insert(Peer id, Point3i coord);

    /**
     * check if the node is a boundary neighbor
     * 
     * @param peer
     * @param center
     * @param radiusSquared
     * @return
     */
    boolean isBoundary(Peer peer, Point3i center, int radiusSquared);

    /**
     * check if the node 'id' is an enclosing neighbor of 'center_node_id'
     * 
     * @param peer
     * @param center_node_id
     * @return
     */
    boolean isEnclosing(Peer peer, Peer center_node_id);

    /**
     * check if a circle overlaps with a particular node
     * 
     * @param peer
     * @param center
     * @param radiusSquared
     * @return
     */
    boolean overlaps(Peer peer, Point3i center, int radiusSquared);

    /**
     * remove a site
     * 
     * @param peer
     */
    void remove(Peer peer);

    /**
     * modify the coordinates of a site
     * 
     * @param peer
     * @param coord
     */
    void update(Peer peer, Point3i coord);

}
