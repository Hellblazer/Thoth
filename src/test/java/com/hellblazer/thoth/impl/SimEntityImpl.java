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

import java.util.Random;

import javax.vecmath.Point2f;
import javax.vecmath.Point3i;

import com.hellblazer.geometry.Vector3i;
import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.annotations.Entity;
import com.hellblazer.thoth.Cursor;
import com.hellblazer.thoth.Perceiving;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

@Entity({ SimEntity.class, Perceiving.class })
public class SimEntityImpl implements Perceiving, SimEntity {
    protected int     dimX;
    protected int     dimY;
    protected Point2f directionFlipSteps;
    protected boolean directionX;
    protected boolean directionY;
    protected int     flipStep;
    protected Cursor  locator;
    protected int     maxVelocity;
    protected Random  random;
    protected int     thinkTime;
    protected Point3i velocity;

    public SimEntityImpl(Random random, int thinkTime, int flipStep, int maxVelocity, int dimX, int dimY) {
        this.random = random;
        this.thinkTime = thinkTime;
        this.flipStep = flipStep;
        this.maxVelocity = maxVelocity;
        this.dimX = dimX;
        this.dimY = dimY;
        velocity = new Point3i(newDelta(), newDelta(), 0);
        directionX = random.nextBoolean();
        directionY = random.nextBoolean();
        directionFlipSteps = new Point2f(random.nextInt(flipStep), random.nextInt(flipStep));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.thoth.SimEntity#doSomething()
     */
    @Override
    public void doSomething() {
        Kronos.sleep(Math.max(random.nextInt(thinkTime), 100));
        nextVelocityVector();
        locator.moveBy(velocity);
    }

    @Override
    public void fade(Perceiving neighbor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(Perceiving neighbor, Point3i location, Vector3i velocity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notice(Perceiving neighbor, Point3i location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCursor(Cursor locator) {
        this.locator = locator;
    }

    protected int newDelta() {
        return (int) (random.nextDouble() * maxVelocity);
    }

    protected void nextVelocityVector() {
        if (locator.getLocation().x < 0) {
            velocity.x *= -1;
            directionX = !directionX;
            directionFlipSteps.x = (int) (random.nextDouble() * flipStep);
        } else if (locator.getLocation().x >= dimX) {
            velocity.x *= -1;
            directionX = !directionX;
            directionFlipSteps.x = (int) (random.nextDouble() * flipStep);
        } else {
            if (--directionFlipSteps.x == 0) {
                directionX = !directionX;
                directionFlipSteps.x = (int) (random.nextDouble() * flipStep);
            }
            velocity.x = newDelta() * (directionX ? 1 : -1);
        }
        if (locator.getLocation().y < 0) {
            velocity.y *= -1;
            directionY = !directionY;
            directionFlipSteps.y = (int) (random.nextDouble() * flipStep);
        } else if (locator.getLocation().y >= dimY) {
            velocity.y *= -1;
            directionY = !directionY;
            directionFlipSteps.y = (int) (random.nextDouble() * flipStep);
        } else {
            if (--directionFlipSteps.y == 0) {
                directionY = !directionY;
                directionFlipSteps.y = (int) (random.nextDouble() * flipStep);
            }
            velocity.y = newDelta() * (directionY ? 1 : -1);
        }
    }

}
