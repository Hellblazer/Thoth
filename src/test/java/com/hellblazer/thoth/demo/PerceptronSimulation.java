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

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.vecmath.Point3i;

import com.hellblazer.primeMover.controllers.SteppingController;
import com.hellblazer.primeMover.runtime.Framework;
import com.hellblazer.thoth.impl.Node;
import com.hellblazer.thoth.impl.Perceptron;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

@SuppressWarnings("removal")
public class PerceptronSimulation extends Applet implements KeyListener, MouseListener, MouseMotionListener {
    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            synchronized (sync) {
                if (next_ready) {
                    steps++;
                    for (DisplayedEntity entity : entities) {
                        entity.doSomething();
                    }
                    try {
                        controller.step();
                    } catch (Throwable e) {
                        throw new IllegalStateException("Unable to step controller", e);
                    }
                    next_ready = false;
                }
                if (!step_mode) {
                    next_ready = true;
                }
            }
            repaint();
        }
    }

    final static Color bg = Color.white;

    final static Color        fg               = Color.black;
    final static BasicStroke  stroke           = new BasicStroke(2.0f);
    private static final int  RANDOM_SEED      = 666;
    private static final long serialVersionUID = 1L;
    public int                steps            = 0;
    BufferedImage             buff_i;
    Graphics2D                buff_ig;
    SteppingController        controller;
    int                       currentX, currentY;
    int                       default_aoi      = 300;
    int                       dim_x            = 1024;
    int                       dim_y            = 1024;
    DisplayedEntity[]         entities;
    boolean                   follow_mode      = false;
    int                       mouseX, mouseY;
    boolean                   next_ready       = true;                        // if we're ready to move to the next step
    int                       node_radius      = 5;
    int                       node_size        = 25;
    Map<Node, Integer>        nodeNames        = new HashMap<Node, Integer>();
    int                       originX          = 0;
    int                       originY          = 0;
    @SuppressWarnings("rawtypes")
    Perceptron[]              perceptrons;                                    // a list of all perceptron nodes created
    boolean                   running          = true;
    DisplayedEntity           selectedEntity;
    int                       selfX            = 250;
    int                       selfY            = 250;
    boolean                   show_aoi         = true;
    boolean                   show_edges       = true;
    boolean                   show_global      = true;
    boolean                   step_mode        = true;
    //
    // node refresh thread
    //
    Timer                timer;
    int                  updateDelay = 20;          // in millisec
    RefreshTask          updateTask;
    private int          flipStep    = 200;
    private boolean      graphicsInitialized;
    private boolean      initialized;
    private int          maxVelocity = 10;
    private final Object sync        = new Object();
    private int          thinkTime   = 1000;

    public void destory() {
        updateTask.cancel();
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void init() {
        controller = new SteppingController();
        Framework.setController(controller);
        controller.setCurrentTime(0);
        System.err.println("Thoth demo (Java applet version) Hal Hildebrand (c) 2008");
        setSize(dim_x, dim_y);
        // get initial parameters, if available
        perceptrons = new Perceptron[node_size];
        entities = new DisplayedEntity[node_size];
        int i;
        Random random = new Random(RANDOM_SEED);
        Random idGen = new Random(RANDOM_SEED);
        for (i = 0; i < node_size; i++) {
            Point3i pt = new Point3i(random.nextInt(dim_x), random.nextInt(dim_y), 0);
            UUID id = new UUID(idGen.nextLong(), idGen.nextLong());
            entities[i] = new DisplayedEntity(random, thinkTime, flipStep, maxVelocity, dim_x, dim_y);
            perceptrons[i] = new Perceptron<>(entities[i], id, pt, default_aoi, 10, true);
            entities[i].setPerceptron(perceptrons[i]);
            nodeNames.put(perceptrons[i], i);
            perceptrons[i].join(perceptrons[0]);
        }
        selectedEntity = entities[0];
        setBackground(bg);
        setForeground(fg);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        currentX = currentY = 0;
        try {
            controller.step();
        } catch (Throwable e) {
            throw new IllegalStateException("Unable to step controller", e);
        }
        timer = new java.util.Timer();
        updateTask = new RefreshTask();
        timer.schedule(updateTask, 0, updateDelay);
        initialized = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        // System.out.println("keycode=" + keyCode);
        // up, down, left, right
        switch (keyCode) {
        // up
        case 38:
            // originY -= 2;
            selfY -= 2;
            // sendMessage ("MOVE_UP");
            break;
        // down
        case 40:
            // originY += 2;
            selfY += 2;
            // sendMessage ("MOVE_DOWN");
            break;
        // left
        case 37:
            // originX -= 2;
            selfX -= 2;
            // sendMessage ("MOVE_LEFT");
            break;
        // right
        case 39:
            // originX += 2;
            selfX += 2;
            // sendMessage ("MOVE_RIGHT");
            break;
        // 'f'
        case 70:
            follow_mode = !follow_mode;
            break;
        // 'o'
        case 79:
            if (follow_mode = true) {
                follow_mode = false;
            }
            originX = 0;
            originY = 0;
            break;
        // ' ' (space)
        case 32:
            if (step_mode == true) {
                next_ready = true;
            } else {
                step_mode = true;
            }
            break;
        // enter (step-mode toggle)
        case 10:
            step_mode = !step_mode;
            break;
        // 'g'
        case 71:
            show_global = !show_global;
            break;
        // 'a'
        case 65:
            show_aoi = !show_aoi;
            break;
        // 'e'
        case 69:
            show_edges = !show_edges;
            break;
        // 'q'
        case 81:
            break;
        }
        if (keyCode >= 37 && keyCode <= 40 && !step_mode) {
            return;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // required by MouseListener.
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX() + originX;
        mouseY = e.getY() + originY;
        repaint();
    }

    // Handles the event of the user pressing down the mouse button.
    @Override
    public void mousePressed(MouseEvent e) {
        // System.out.println("clicked at (" + e.getX() + ", " + e.getY() +
        // ")");
        for (int i = 0; i < node_size; i++) {
            Point3i pt = perceptrons[i].getLocation();
            Point3i pt_clicked = new Point3i(e.getX() + originX, e.getY() + originY, 0);
            if (distance(pt, pt_clicked) < 2 * node_radius) {
                // left-click
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // System.out.println("node [" + id_num + "] selected");
                    selectedEntity = entities[i];
                    repaint();
                    break;
                }
                // toggle display of a remote node's AOI
                else {
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void paint(Graphics gobj) {
        synchronized (sync) {
            if (!initialized) {
                return;
            }
            internalPaint(gobj);
        }
    }

    double distance(Point3i from, Point3i to) {
        double PX = to.x - from.x;
        double PY = to.y - from.y;
        return Math.sqrt(PX * PX + PY * PY);
    }

    private void internalPaint(Graphics gobj) {
        Graphics2D g2 = (Graphics2D) gobj;
        if (follow_mode) {
            originX = currentX - dim_x / 2;
            originY = currentY - dim_y / 2;
        }
        if (!graphicsInitialized) {
            graphicsInitialized = true;
            buff_i = (BufferedImage) createImage(dim_x, dim_y);
            buff_ig = buff_i.createGraphics();
        }
        // clear screen & draw bounding box
        buff_ig.setColor(bg);
        buff_ig.clearRect(0, 0, dim_x, dim_y);
        buff_ig.setPaint(fg);
        buff_ig.setStroke(stroke);
        buff_ig.draw(new Rectangle2D.Double(0, 0, dim_x, dim_y));
        for (DisplayedEntity entity : entities) {
            entity.display(nodeNames, entity.equals(selectedEntity), show_edges, show_aoi, originX, originY, buff_ig);
        }
        try {
            controller.step();
        } catch (Throwable e) {
            throw new IllegalStateException("Unable to step controller", e);
        }
        // draw info message at top
        buff_ig.setPaint(fg);
        String s = " origin: (" + originX + ", " + originY + ") step: " + steps + " selected: ["
        + nodeNames.get(selectedEntity.perceptron) + "] (" + currentX + ", " + currentY + ")";
        buff_ig.drawString(s, 0, 12);
        g2.drawImage(buff_i, 0, 0, this);
        // see if we should obtain next frame
        if (!step_mode) {
            next_ready = true;
        }
    }
}
