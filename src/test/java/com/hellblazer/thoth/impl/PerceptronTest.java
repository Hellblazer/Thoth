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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;

import javax.vecmath.Point3i;

import org.junit.jupiter.api.Test;

import com.hellblazer.geometry.Vector3i;
import com.hellblazer.primeMover.controllers.SteppingController;
import com.hellblazer.primeMover.runtime.Framework;
import com.hellblazer.thoth.Perceiving;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

public class PerceptronTest {

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testTracking() throws Throwable {
        SteppingController controller = new SteppingController();
        Framework.setController(controller);
        controller.setCurrentTime(0);

        int x = 1000;
        int y = 1000;
        int thinkTime = 1000;
        int numNodes = 100;
        int aoi = 20;
        int maxStep = 1000;
        int flipStep = 75;
        int maxVelocity = 10;
        Random random = new Random(666);

        Perceptron<Perceiving>[] perceptrons = new Perceptron[numNodes];
        SimEntityImpl[] entities = new SimEntityImpl[numNodes];
        for (int i = 0; i < numNodes; i++) {
            entities[i] = new SimEntityImpl(random, thinkTime, flipStep, maxVelocity, x, y);
            perceptrons[i] = new Perceptron(entities[i], new UUID(0, i),
                                            new Point3i(random.nextInt(x), random.nextInt(y), 0), aoi, 10, false);
            perceptrons[i].join(perceptrons[0].getThisAsPeer());
        }

        controller.step();

        for (int step = 0; step < maxStep; step++) {
            for (SimEntity entity : entities) {
                entity.doSomething();
            }
            controller.step();
            for (Perceptron<Perceiving> perceptron : perceptrons) {
                for (AbstractNode<? extends Perceiving> neighbor : perceptron.getNeighbors()) {
                    AbstractNode<?> node = neighbor;
                    Vector3i distance = new Vector3i(perceptron.getLocation());
                    if (distance.length() < perceptron.getAoiRadius()) {
                        // Verify that all the neighbors that are within the
                        // perceptron's AOI are perceived in the right location
                        assertTrue(node.getLocation().equals(neighbor.getLocation()), step + ": Node [" + perceptron
                        + "] Model location does not match neighbor's reported location: [" + node + "]");
                    }
                }
            }
        }
    }
}
