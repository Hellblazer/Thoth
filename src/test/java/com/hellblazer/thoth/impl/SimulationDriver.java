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

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.vecmath.Point3i;

import com.hellblazer.primeMover.Kronos;
import com.hellblazer.primeMover.controllers.SimulationController;
import com.hellblazer.primeMover.runtime.Framework;
import com.hellblazer.thoth.Perceiving;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

public class SimulationDriver {
    public static final int AOI_RADIUS = 200;
    public static final int dim_x = 1200;
    public static final int dim_y = 1200;
    public static final int ENTRY_PERIOD = 10000;
    public static final int FLIP_STEP_CONSTANT = 100;
    public static final int MAX_VELOCITY = 10;
    public static final int MOVES = 2000;
    public static final int NUMBER_OF_NODES = 100;

    public static final int RANDOM_SEED = 666;

    public static final int THINK_TIME = 1000;

    public static void main(String[] argv) throws Exception {
        SimulationController controller = new SimulationController();
        Framework.setController(controller);
        long now = System.currentTimeMillis();
        new SimulationDriver().run();
        controller.eventLoop();
        System.out.println("real time elapsed: "
                           + (System.currentTimeMillis() - now) / 1000.0
                           + " Seconds");
        System.out.println("simulation time elapsed: "
                           + (controller.getSimulationEnd() - controller.getSimulationStart()));
        System.out.println("Spectrum : ");
        for (Map.Entry<String, Integer> spectrumEntry : controller.getSpectrum().entrySet()) {
            System.out.println("\t" + spectrumEntry.getValue() + "\t\t : "
                               + spectrumEntry.getKey());
        }
    }

    public void run() {
        Kronos.endSimulationAt(600 * 1000);
        Random idGenerator = new Random(RANDOM_SEED);
        Random simRandom = new Random(RANDOM_SEED);
        SelfDirectedEntity gatewayEntity = new SelfDirectedEntity(
                                                                  simRandom,
                                                                  THINK_TIME,
                                                                  FLIP_STEP_CONSTANT,
                                                                  MAX_VELOCITY,
                                                                  dim_x, dim_y);
        Perceptron<Perceiving> gateway = new Perceptron<Perceiving>(
                                                                    gatewayEntity,
                                                                    new UUID(
                                                                             idGenerator.nextLong(),
                                                                             idGenerator.nextLong()),
                                                                    new Point3i(
                                                                                simRandom.nextInt(dim_x),
                                                                                simRandom.nextInt(dim_y),
                                                                                0),
                                                                    AOI_RADIUS,
                                                                    10, true);
        gateway.join(gateway.getThisAsPeer());
        for (int i = 1; i < NUMBER_OF_NODES; i++) {
            SelfDirectedEntity simEntity = new SelfDirectedEntity(
                                                                  simRandom,
                                                                  THINK_TIME,
                                                                  FLIP_STEP_CONSTANT,
                                                                  MAX_VELOCITY,
                                                                  dim_x, dim_y);
            Perceptron<Perceiving> node = new Perceptron<Perceiving>(
                                                                     simEntity,
                                                                     new UUID(
                                                                              idGenerator.nextLong(),
                                                                              idGenerator.nextLong()),
                                                                     new Point3i(
                                                                                 simRandom.nextInt(dim_x),
                                                                                 simRandom.nextInt(dim_y),
                                                                                 0),
                                                                     AOI_RADIUS,
                                                                     10, true);
            start(simEntity, node, gateway.getThisAsPeer(), simRandom);
        }
        gatewayEntity.doSomething();
    }

    public void start(SimEntity entity, Perceptron<Perceiving> node,
                      AbstractNode<Perceiving> gateway, Random simRandom) {
        Kronos.sleep(simRandom.nextInt(ENTRY_PERIOD));
        node.join(gateway);
        entity.doSomething();
    }
}
