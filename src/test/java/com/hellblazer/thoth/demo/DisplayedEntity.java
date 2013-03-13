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

package com.hellblazer.thoth.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Point2d;

import com.hellblazer.thoth.Perceiving;
import com.hellblazer.thoth.impl.AbstractNode;
import com.hellblazer.thoth.impl.Node;
import com.hellblazer.thoth.impl.Perceptron;
import com.hellblazer.thoth.impl.SimEntityImpl;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

@SuppressWarnings("restriction")
public class DisplayedEntity extends SimEntityImpl {
	final static BasicStroke aoiStroke = new BasicStroke(3.0f);
	final static int node_radius = 5;
	final static BasicStroke stroke = new BasicStroke(2.0f);
	protected Perceptron<Perceiving> perceptron;

	public DisplayedEntity(Random random, int thinkTime, int flipStep,
			int maxVelocity, int dimX, int dimY) {
		super(random, thinkTime, flipStep, maxVelocity, dimX, dimY);
	}

	public void display(Map<Node, Integer> nodeNames, boolean selected,
			boolean showEdges, boolean showAoi, int originX, int originY,
			Graphics2D graphics) {
		graphics.setStroke(stroke);
		graphics.setPaint(Color.blue);
		graphics.draw(new Ellipse2D.Double(locator.getLocation().x
				- node_radius * 2 - originX, locator.getLocation().y
				- node_radius * 2 - originY, node_radius * 2 * 2,
				node_radius * 2 * 2));
		graphics.drawString("[" + nodeNames.get(perceptron) + "]",
				locator.getLocation().x - originX, locator.getLocation().y
						- node_radius * 3 - originY);
		if (selected) {
			graphics.setPaint(Color.red);
			for (AbstractNode<? extends Perceiving> neighbor : perceptron
					.getNeighbors()) {
				graphics.drawString("[" + nodeNames.get(neighbor) + "]",
						neighbor.getLocation().x - originX,
						neighbor.getLocation().y - node_radius * 3 - originY);
				graphics.fill(new Ellipse2D.Double(neighbor.getLocation().x
						- node_radius - originX, neighbor.getLocation().y
						- node_radius - originY, node_radius * 2,
						node_radius * 2));
			}
			if (showEdges) {
				List<Point2d[]> edges = perceptron.getVoronoiDomainEdges();
				for (Point2d[] edge : edges) {
					graphics.draw(new Line2D.Double(edge[0].x - originX,
							edge[0].y - originY, edge[1].x - originX, edge[1].y
									- originY));
				}
			}
			if (showAoi) {
				graphics.setStroke(aoiStroke);
				graphics.draw(new Ellipse2D.Double(locator.getLocation().x
						- perceptron.getAoiRadius() - originX, locator
						.getLocation().y - perceptron.getAoiRadius() - originY,
						perceptron.getAoiRadius() * 2, perceptron
								.getAoiRadius() * 2));
				graphics.setStroke(stroke);
			}
		}

	}

	public void setPerceptron(Perceptron<Perceiving> perceptron) {
		this.perceptron = perceptron;
	}
}
