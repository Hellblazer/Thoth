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

package com.hellblazer.thoth.impl;

import java.util.Collection;
import java.util.UUID;

import javax.vecmath.Point3i;

import com.hellblazer.thoth.Perceiving;

/**
 *
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 *
 */

public class Peer extends AbstractNode<Perceiving> {
    protected Node peer;

    public Peer(Node peer, Perceiving sim, UUID id, Point3i location, int aoiRadius, int maximumVelocity) {
        super(sim, id, location, aoiRadius, maximumVelocity);
        this.peer = peer;
    }

    @Override
    public Peer clone() {
        return (Peer) super.clone();
    }

    @Override
    public void fadeFrom(Peer neighbor) {
        peer.fadeFrom(neighbor);
    }

    @Override
    public void leave(Peer leaving) {
        peer.leave(leaving);
    }

    @Override
    public void move(Peer neighbor) {
        peer.move(neighbor);
    }

    @Override
    public void moveBoundary(Peer neighbor) {
        peer.moveBoundary(neighbor);
    }

    @Override
    public void noticePeers(Collection<Peer> peers) {
        peer.noticePeers(peers);
    }

    @Override
    public void perceive(Peer neighbor) {
        peer.perceive(neighbor);
    }

    @Override
    public void query(Peer from, Peer joiner) {
        peer.query(from, joiner);
    }

}
