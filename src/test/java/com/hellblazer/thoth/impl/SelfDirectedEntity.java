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

import static com.hellblazer.thoth.impl.SimulationDriver.MOVES;

import java.util.Random;

public class SelfDirectedEntity extends SimEntityImpl {
    protected int move = 0;

    public SelfDirectedEntity(Random random, int thinkTime, int flipStep, int maxVelocity, int dimX, int dimY) {
        super(random, thinkTime, flipStep, maxVelocity, dimX, dimY);
    }

    @Override
    public void doSomething() {
        super.doSomething();
        if (++move < MOVES) {
            doSomething();
        }
    }

}
