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

import java.util.UUID;

import javax.vecmath.Point3i;
import javax.vecmath.Tuple3i;

import com.hellblazer.thoth.Cursor;
import com.hellblazer.thoth.Perceiving;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

@SuppressWarnings("restriction")
abstract public class AbstractNode<E extends Perceiving> implements Node,
		Cursor, Cloneable {

	protected int aoiRadius;
	protected final UUID id;
	protected Point3i location;
	protected int maximumVelocity;
	protected int maxRadiusSquared;
	protected E sim;

	public AbstractNode(E entity, UUID id, Point3i location, int aoiRadius,
			int maximumVelocity) {
		this.sim = entity;
		this.location = location;
		this.aoiRadius = aoiRadius;
		this.id = id;
		this.maximumVelocity = maximumVelocity;
		int maxExtent = aoiRadius + maximumVelocity * BUFFER_MULTIPLIER;
		this.maxRadiusSquared = maxExtent * maxExtent;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AbstractNode<E> clone() {
		try {
			return (AbstractNode<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Unable to clone", e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractNode)) {
			return false;
		}
		return id.equals(((AbstractNode<?>) obj).id);
	}

	public int getAoiRadius() {
		return aoiRadius;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public Point3i getLocation() {
		return new Point3i(location);
	}

	public int getMaximumRadiusSquared() {
		return maxRadiusSquared;
	}

	public int getMaximumVelocity() {
		return maximumVelocity;
	}

	public E getSim() {
		return sim;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public void moveBy(Tuple3i velocity) {
		Point3i newLocation = new Point3i();
		newLocation.add(location, velocity);
		setLocation(newLocation);
	}

	public void setLocation(Point3i location) {
		this.location = location;
	}

	@Override
	public String toString() {
		String className = getClass().getCanonicalName();
		int index = className.lastIndexOf('.');
		return className.substring(index + 1) + " [" + id + "] (" + location.x
				+ ", " + location.y + ") aoi: " + getAoiRadius();
	}

}
