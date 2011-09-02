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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.vecmath.Point2d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;

import com.hellblazer.geometry.Vector3i;
import com.hellblazer.primeMover.Entity;
import com.hellblazer.thoth.Movable;
import com.hellblazer.thoth.Perceiving;
import com.hellblazer.thoth.voronoi.SFVoronoi;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

@Entity({ Node.class, Movable.class })
public class Perceptron<E extends Perceiving> extends AbstractNode<E> {
    protected boolean active = true;
    protected final SphereOfInteraction soi;
    protected final Map<UUID, Perceiving> soiSet = new HashMap<UUID, Perceiving>();
    protected final Peer thisAsPeer;

    public Perceptron(E entity, UUID id, Point3i location, int aoiRadius,
                      int maximumVelocity) {
        this(entity, id, location, aoiRadius, maximumVelocity, false);
    }

    public Perceptron(E entity, UUID id, Point3i location, int aoiRadius,
                      int maximumVelocity, boolean graph) {
        super(entity, id, location, aoiRadius, maximumVelocity);
        this.aoiRadius = aoiRadius;
        entity.setCursor(this);
        soi = new SFVoronoi(graph);
        thisAsPeer = new Peer(this, sim, id, location, aoiRadius,
                              maximumVelocity);
        soi.insert(thisAsPeer, location);
    }

    @Override
    public void fadeFrom(Peer neighbor) {
        remove(neighbor);
    }

    public Collection<Peer> getNeighbors() {
        final ArrayList<Peer> neighbors = new ArrayList<Peer>();
        for (Peer peer : soi.getPeers()) {
            if (!peer.equals(this)) {
                neighbors.add(peer);
            }
        }
        return neighbors;
    }

    public Peer getThisAsPeer() {
        return thisAsPeer;
    }

    public List<Point2d[]> getVoronoiDomainEdges() {
        return soi.getVoronoiDomainEdges();
    }

    public void join(Node gateway) {
        if (!gateway.equals(this)) {
            gateway.query(thisAsPeer, thisAsPeer);
        }
    }

    public void leave() {
        active = false;
        for (Peer peer : soi.getPeers()) {
            if (!peer.equals(this)) {
                peer.fadeFrom(thisAsPeer);
            }
        }
    }

    @Override
    public void leave(Peer leaving) {
        for (Node enclosing : soi.getEnclosingNeighbors(leaving)) {
            enclosing.leave(leaving);
        }
        remove(leaving);
    }

    @Override
    public void move(Peer neighbor) {
        if (!active) {
            neighbor.leave(thisAsPeer);
            return;
        }

        notifySimMove(neighbor, update(neighbor));
        removeNonOverlapped();
    }

    @Override
    public void moveBoundary(Peer neighbor) {
        if (!active) {
            neighbor.leave(thisAsPeer);
            return;
        }

        if (!soi.includes(neighbor)) {
            if (soi.overlaps(thisAsPeer, neighbor.getLocation(),
                             maxRadiusSquared)) {
                soi.insert(neighbor, neighbor.getLocation());
            }
            return;
        }
        handshakeWith(neighbor);
        notifySimMove(neighbor, update(neighbor));
        removeNonOverlapped();
    }

    @Override
    public void moveBy(Tuple3i velocity) {
        super.moveBy(velocity);
        thisAsPeer.setLocation(location);
        soi.update(thisAsPeer, location);
        removeNonOverlapped();
        for (Peer peer : soi.getPeers()) {
            if (!peer.equals(this)) {
                if (soi.isBoundary(peer, location, maxRadiusSquared)) {
                    peer.moveBoundary(thisAsPeer);
                } else {
                    peer.move(thisAsPeer);
                }
            }
        }
    }

    @Override
    public void noticePeers(Collection<Peer> peers) {
        if (!active) {
            return;
        }

        for (Peer peer : peers) {
            if (!soi.includes(peer)) {
                soi.insert(peer.clone(), peer.getLocation());
                if (soi.overlaps(thisAsPeer, peer.getLocation(),
                                 peer.getMaximumRadiusSquared())) {
                    peer.perceive(thisAsPeer);
                }
            }
        }
    }

    @Override
    public void perceive(Peer neighbor) {
        if (!active) {
            neighbor.leave(thisAsPeer);
            return;
        }

        add(neighbor);
        handshakeWith(neighbor);
        notifySimNotice(neighbor);
    }

    @Override
    public void query(Peer from, Peer joiner) {
        if (!active) {
            from.leave(thisAsPeer);
            from.query(joiner, joiner);
            return;
        }

        Peer closest = soi.closestTo(joiner.getLocation());
        if (closest != null && !closest.equals(this) && !closest.equals(from)) {
            closest.query(thisAsPeer, joiner);
        } else {
            add(joiner);
            joiner.perceive(thisAsPeer);
            handshakeWith(joiner);
        }
    }

    protected void add(Peer node) {
        soi.insert(node.clone(), node.getLocation());
    }

    protected void handshakeWith(Peer node) {
        if (node.equals(this)) {
            return;
        }
        Collection<Peer> peers = soi.getEnclosingNeighbors(node);
        if (peers.size() > 0) {
            node.noticePeers(peers);
        }
    }

    protected void notifySimMove(Peer neighbor, Point3i oldLocation) {
        if (oldLocation == null || !soiSet.containsKey(neighbor.id)) {
            notifySimNotice(neighbor);
            return;
        }
        Vector3i distance = new Vector3i(location);
        distance.sub(neighbor.getLocation());
        if (distance.lengthSquared() <= maxRadiusSquared) {
            Vector3i velocity = new Vector3i();
            velocity.sub(neighbor.getLocation(), oldLocation);
            Point3i nLocation = neighbor.getLocation();
            sim.move(neighbor.getSim(), nLocation, velocity);
        } else {
            soiSet.remove(neighbor.getSim());
            sim.fade(neighbor.getSim());
        }
    }

    protected void notifySimNotice(Peer neighbor) {
        Vector3i distance = new Vector3i(location);
        distance.sub(neighbor.getLocation());
        if (distance.lengthSquared() <= maxRadiusSquared) {
            soiSet.put(neighbor.id, neighbor.getSim());
            sim.notice(neighbor.getSim(), neighbor.getLocation());
        }
    }

    protected void remove(Peer neighbor) {
        soi.remove(neighbor);
        Perceiving node = soiSet.remove(neighbor.id);
        if (node != null) {
            sim.fade(node);
        }
    }

    /**
     * disconnect neighbors no longer relevant (within AOI or is an enclosing
     * neighbor)
     */
    protected void removeNonOverlapped() {
        ArrayList<Peer> removed = new ArrayList<Peer>();
        for (Peer neighbor : soi.getPeers()) {
            if (!equals(neighbor)
                && !soi.overlaps(thisAsPeer,
                                 neighbor.getLocation(),
                                 Math.max(maxRadiusSquared,
                                          neighbor.getMaximumRadiusSquared()))
                && !soi.isEnclosing(neighbor, thisAsPeer)) {
                removed.add(neighbor);
            }
        }
        for (Peer neighbor : removed) {
            remove(neighbor);
            neighbor.fadeFrom(thisAsPeer);
        }
    }

    protected Point3i update(Peer node) {
        Peer neighbor = soi.getAliased(node);
        if (neighbor == null) {
            soi.insert(node.clone(), node.getLocation());
            return null;
        }
        Point3i oldLocation = new Point3i(neighbor.getLocation());
        neighbor.setLocation(node.getLocation());
        soi.update(neighbor, node.getLocation());
        return oldLocation;
    }
}
