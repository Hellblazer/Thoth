/*
 * The author of this software is Steven Fortune. Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * 
 * Permission to use, copy, modify, and distribute this software for any purpose
 * without fee is hereby granted, provided that this entire notice is included
 * in all copies of any software which is or includes a copy or modification of
 * this software and in all copies of the supporting documentation for such
 * software.
 * 
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY. IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY REPRESENTATION
 * OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY OF THIS SOFTWARE OR
 * ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

/*
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
package com.hellblazer.thoth.voronoi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.vecmath.Point2d;
import javax.vecmath.Point3i;

import com.hellblazer.thoth.impl.Peer;
import com.hellblazer.thoth.impl.SphereOfInteraction;

/**
 * 
 * @author <a href="mailto:hal.hildebrand@gmail.com">Hal Hildebrand</a>
 * 
 */

public class SFVoronoi implements SphereOfInteraction {
    static final int le = 0;
    static final int re = 1;

    static float distSquared(float x1, float y1, float x2, float y2) {
        float dx, dy;
        dx = x1 - x2;
        dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public ArrayList<Point2d[]> edges;
    private boolean invalidated = false;
    private boolean plot = true;
    private Map<UUID, Site> sites = new HashMap<UUID, Site>();

    public SFVoronoi(boolean plot) {
        this.plot = plot;
        if (plot) {
            edges = new ArrayList<Point2d[]>();
        }
    }

    // returns the closest node to a point
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#closestTo(javax.vecmath.Point3i)
     */
    @Override
    public Peer closestTo(Point3i coord) {
        // assume the first node is the closest
        Peer closest = null;
        float min_dist = 0.0f;
        float d;
        for (Site site : sites.values()) {
            if (closest == null) {
                closest = site.peer;
                min_dist = site.distanceSquared(coord.x, coord.y);
            } else {
                if ((d = site.distanceSquared(coord.x, coord.y)) < min_dist) {
                    min_dist = d;
                    closest = site.peer;
                }
            }
        }
        return closest;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#getAliased(T)
     */
    @Override
    public Peer getAliased(Peer id) {
        Site site = sites.get(id.getId());
        return site != null ? site.peer : null;
    }

    // get a list of enclosing neighbors
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#getEnclosingNeighbors(T)
     */
    @Override
    public Collection<Peer> getEnclosingNeighbors(Peer id) {
        Site site = sites.get(id.getId());
        if (site == null) {
            return new ArrayList<Peer>(0);
        }
        recompute();
        HashSet<Peer> en_list = new HashSet<Peer>();
        for (Edge line : site.edges) {
            Site en_id = line.reg[0].peer.equals(id) ? line.reg[1]
                                                    : line.reg[0];
            en_list.add(en_id.peer);
        }
        return en_list;
    }

    @Override
    public Iterable<Peer> getPeers() {
        return new PeerIterable(sites.values().iterator());
    }

    @Override
    public List<Point2d[]> getVoronoiDomainEdges() {
        return edges;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#includes(T)
     */
    @Override
    public boolean includes(Peer peer) {
        return sites.containsKey(peer.getId());
    }

    // insert a new site, the first inserted is myself
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#insert(T, javax.vecmath.Point3i)
     */
    @Override
    public void insert(Peer id, Point3i coord) {
        // avoid duplicate insert
        if (!sites.containsKey(id.getId())) {
            invalidated = true;
            Site newSite = new Site(coord.x, coord.y);
            newSite.peer = id;
            sites.put(id.getId(), newSite);
        }
    }

    // check if the node is a boundary neighbor
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#isBoundary(T, javax.vecmath.Point3i, int)
     */
    @Override
    public boolean isBoundary(Peer id, Point3i center, int radiusSquared) {
        Site site = sites.get(id.getId());
        if (site == null) {
            return false;
        }
        recompute();
        Vertex v1, v2;
        for (Edge edge : site.edges) {
            v1 = edge.ep[0];
            v2 = edge.ep[1];
            // TODO: we are checking redundant points, can be avoided
            if (v1 == null || v2 == null
                || distSquared(center.x, center.y, v1.x, v1.y) >= radiusSquared
                || distSquared(center.x, center.y, v2.x, v2.y) >= radiusSquared) {
                return true;
            }
        }
        // all the ENs of the node to check are within the AOI-radius (so not a boundary neighbor)
        return false;
    }

    // check if the node 'id' is an enclosing neighbor of 'center_node_id'
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#isEnclosing(T, T)
     */
    @Override
    public boolean isEnclosing(Peer id, Peer center_node_id) {
        Site site = sites.get(center_node_id.getId());
        if (site == null) {
            return false;
        }
        recompute();
        for (Edge edge : site.edges) {
            Object en_id = edge.reg[0].peer.equals(center_node_id) ? edge.reg[1].peer
                                                                  : edge.reg[0].peer;
            if (en_id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // check if a circle overlaps with a particular node
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#overlaps(T, javax.vecmath.Point3i, int)
     */
    @Override
    public boolean overlaps(Peer id, Point3i center, int radiusSquared) {
        Site site = sites.get(id.getId());
        if (site == null) {
            return false;
        }
        return site.distanceSquared(center.x, center.y) <= radiusSquared;
    }

    // remove a site
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#remove(T)
     */
    @Override
    public void remove(Peer id) {
        if (sites.remove(id.getId()) != null) {
            invalidated = true;
        }
    }

    // modify the coordinates of a site
    /* (non-Javadoc)
     * @see com.hellblazer.thoth.voronoi.AOI#update(T, javax.vecmath.Point3i)
     */
    @Override
    public void update(Peer id, Point3i coord) {
        invalidated = true;
        Site site = sites.get(id.getId());
        if (site == null) {
            throw new IllegalStateException("Site does not exist: " + id);
        } else {
            site.x = coord.x;
            site.y = coord.y;
        }
    }

    // find the bisecting edge for two sites (creating a new edge)
    private Edge bisect(Site s1, Site s2) {
        float dx, dy, adx, ady;
        Edge newedge = new Edge();
        newedge.reg[0] = s1;
        newedge.reg[1] = s2;
        newedge.ep[0] = null;
        newedge.ep[1] = null;
        dx = s2.x - s1.x;
        dy = s2.y - s1.y;
        adx = dx > 0 ? dx : -dx;
        ady = dy > 0 ? dy : -dy;
        newedge.c = (float) (s1.x * dx + s1.y * dy + (dx * dx + dy * dy) * 0.5);
        if (adx > ady) {
            newedge.a = 1.0f;
            newedge.b = dy / dx;
            newedge.c /= dx;
        } else {
            newedge.b = 1.0f;
            newedge.a = dx / dy;
            newedge.c /= dy;
        }
        s1.edges.add(newedge);
        s2.edges.add(newedge);
        return newedge;
    }

    // cut edges so that they are displayable
    private void clip_line(Edge e, float pxmin, float pxmax, float pymin,
                           float pymax) {
        Vertex v1, v2;
        float x1, x2, y1, y2;
        if (e.a == 1.0 && e.b >= 0.0) {
            v1 = e.ep[1];
            v2 = e.ep[0];
        } else {
            v1 = e.ep[0];
            v2 = e.ep[1];
        }
        if (e.a == 1.0) {
            y1 = pymin;
            if (v1 != null && v1.y > pymin) {
                y1 = v1.y;
            }
            if (y1 > pymax) {
                return;
            }
            x1 = e.c - e.b * y1;
            y2 = pymax;
            if (v2 != null && v2.y < pymax) {
                y2 = v2.y;
            }
            if (y2 < pymin) {
                return;
            }
            x2 = e.c - e.b * y2;
            if (x1 > pxmax & x2 > pxmax | x1 < pxmin & x2 < pxmin) {
                return;
            }
            if (x1 > pxmax) {
                x1 = pxmax;
                y1 = (e.c - x1) / e.b;
            }
            if (x1 < pxmin) {
                x1 = pxmin;
                y1 = (e.c - x1) / e.b;
            }
            if (x2 > pxmax) {
                x2 = pxmax;
                y2 = (e.c - x2) / e.b;
            }
            if (x2 < pxmin) {
                x2 = pxmin;
                y2 = (e.c - x2) / e.b;
            }
        } else {
            x1 = pxmin;
            if (v1 != null && v1.x > pxmin) {
                x1 = v1.x;
            }
            if (x1 > pxmax) {
                return;
            }
            y1 = e.c - e.a * x1;
            x2 = pxmax;
            if (v2 != null && v2.x < pxmax) {
                x2 = v2.x;
            }
            if (x2 < pxmin) {
                return;
            }
            y2 = e.c - e.a * x2;
            if (y1 > pymax & y2 > pymax | y1 < pymin & y2 < pymin) {
                return;
            }
            if (y1 > pymax) {
                y1 = pymax;
                x1 = (e.c - y1) / e.a;
            }
            if (y1 < pymin) {
                y1 = pymin;
                x1 = (e.c - y1) / e.a;
            }
            if (y2 > pymax) {
                y2 = pymax;
                x2 = (e.c - y2) / e.a;
            }
            if (y2 < pymin) {
                y2 = pymin;
                x2 = (e.c - y2) / e.a;
            }
        }
        edges.add(new Point2d[] { new Point2d(x1, y1), new Point2d(x2, y2) });
    }

    private int compare(float x1, float y1, float x2, float y2) {
        if (y1 < y2) {
            return -1;
        }
        if (y1 > y2) {
            return 1;
        }
        if (x1 < x2) {
            return -1;
        }
        if (x1 > x2) {
            return 1;
        }
        return 0;
    }

    private void endpoint(Edge e, int lr, Vertex v, float pxmin, float pxmax,
                          float pymin, float pymax) {
        e.ep[lr] = v;
        if (e.ep[re - lr] == null) {
            return;
        }
        out_ep(e, pxmin, pxmax, pymin, pymax);
    }

    private Vertex intersect(HalfEdge el1, HalfEdge el2, Point2d p) {
        Edge e1, e2, e;
        HalfEdge el;
        float d, xint, yint;
        e1 = el1.edge;
        e2 = el2.edge;
        if (e1 == null || e2 == null || e1.reg[1] == e2.reg[1]) {
            return null;
        }
        d = e1.a * e2.b - e1.b * e2.a;
        if (-1.0e-10 < d && d < 1.0e-10) {
            return null;
        }
        xint = (e1.c * e2.b - e2.c * e1.b) / d;
        yint = (e2.c * e1.a - e1.c * e2.a) / d;
        if (compare(e1.reg[1].x, e1.reg[1].y, e2.reg[1].x, e2.reg[1].y) < 0) {
            el = el1;
            e = e1;
        } else {
            el = el2;
            e = e2;
        }
        boolean right_of_site = xint >= e.reg[1].x;
        if (right_of_site && el.pm == le || !right_of_site && el.pm == re) {
            return null;
        }
        return new Vertex(xint, yint);
    }

    private void out_ep(Edge e, float pxmin, float pxmax, float pymin,
                        float pymax) {
        if (plot) {
            clip_line(e, pxmin, pxmax, pymin, pymax);
        }
    }

    private void recompute() {
        if (!invalidated) {
            return;
        }
        float xmin, xmax, ymin, ymax, deltax, deltay;
        if (plot) {
            edges.clear();
        }
        // find out the x & y ranges for all sites 
        Site[] sortedSites = sites.values().toArray(new Site[0]);
        Arrays.sort(sortedSites);
        xmin = xmax = sortedSites[0].x;
        ymin = ymax = sortedSites[0].y;
        Site s;
        for (int i = 0; i < sites.size(); i++) {
            s = sortedSites[i];
            s.reset();
            if (s.x < xmin) {
                xmin = s.x;
            } else if (s.x > xmax) {
                xmax = s.x;
            }
            if (s.y < ymin) {
                ymin = s.y;
            } else if (s.y > ymax) {
                ymax = s.y;
            }
        }
        deltay = ymax - ymin;
        deltax = xmax - xmin;
        voronoi(sortedSites, xmin, xmax, ymin, ymax, deltax, deltay);
        invalidated = false;
    }

    private void voronoi(Site[] sortedSites, float xmin, float xmax,
                         float ymin, float ymax, float deltax, float deltay) {
        float pxmax, pymax, pxmin, pymin;
        pxmax = pymax = pxmin = pymin = 0.0f;
        Site newsite = null, bot = null, top = null, temp = null;
        Vertex p;
        Vertex v;
        float newIntStarX = 0, newIntStarY = 0;
        int pm;
        HalfEdge lbnd, rbnd, llbnd, rrbnd, bisector;
        Edge e;
        int sqrt_nsites = (int) Math.sqrt(sites.size() + 4);
        PriorityQueue pq = new PriorityQueue(sqrt_nsites);
        int currSiteIndex = 0;
        Site bottomsite = null;
        if (currSiteIndex < sortedSites.length) {
            bottomsite = sortedSites[currSiteIndex++];
        }
        EL el = new EL(sqrt_nsites);
        if (plot) {
            float d = (deltax > deltay ? deltax : deltay) * 1.1f;
            pxmin = xmin - (d - deltax) / 2.0f;
            pxmax = xmax + (d - deltax) / 2.0f;
            pymin = ymin - (d - deltay) / 2.0f;
            pymax = ymax + (d - deltay) / 2.0f;
        }
        if (currSiteIndex < sortedSites.length) {
            newsite = sortedSites[currSiteIndex++];
        }
        while (true) {
            if (!pq.empty()) {
                HalfEdge min = pq.min();
                newIntStarX = min.next.vertex.x;
                newIntStarY = min.next.ystar;
            }
            if (newsite != null
                && (pq.empty() || compare(newsite.x, newsite.y, newIntStarX,
                                          newIntStarY) < 0)) {
                // new site is smallest 
                lbnd = el.leftbnd(newsite.x, newsite.y, xmin, deltax);
                rbnd = el.right(lbnd);
                bot = lbnd.rightreg(bottomsite);
                e = bisect(bot, newsite);
                bisector = new HalfEdge(e, le);
                el.insert(lbnd, bisector);
                if ((p = intersect(lbnd, bisector, null)) != null) {
                    pq.delete(lbnd, ymin, deltay);
                    pq.insert(lbnd, p, p.distance(newsite), ymin, deltay);
                }
                lbnd = bisector;
                bisector = new HalfEdge(e, re);
                el.insert(lbnd, bisector);
                if ((p = intersect(bisector, rbnd, null)) != null) {
                    pq.insert(bisector, p, p.distance(newsite), ymin, deltay);
                }
                if (currSiteIndex < sortedSites.length) {
                    newsite = sortedSites[currSiteIndex++];
                } else {
                    newsite = null;
                }
            }
            // intersection is smallest
            else if (!pq.empty()) {
                lbnd = pq.extractMin();
                llbnd = el.left(lbnd);
                rbnd = el.right(lbnd);
                rrbnd = el.right(rbnd);
                bot = lbnd.leftreg(bottomsite);
                top = rbnd.rightreg(bottomsite);
                v = lbnd.vertex;
                endpoint(lbnd.edge, lbnd.pm, v, pxmin, pxmax, pymin, pymax);
                endpoint(rbnd.edge, rbnd.pm, v, pxmin, pxmax, pymin, pymax);
                lbnd.ELdelete();
                pq.delete(rbnd, ymin, deltay);
                rbnd.ELdelete();
                pm = le;
                if (bot.y > top.y) {
                    temp = bot;
                    bot = top;
                    top = temp;
                    pm = re;
                }
                e = bisect(bot, top);
                bisector = new HalfEdge(e, pm);
                el.insert(llbnd, bisector);
                endpoint(e, re - pm, v, pxmin, pxmax, pymin, pymax);
                if ((p = intersect(llbnd, bisector, null)) != null) {
                    pq.delete(llbnd, ymin, deltay);
                    pq.insert(llbnd, p, p.distance(bot), ymin, deltay);
                }
                if ((p = intersect(bisector, rrbnd, null)) != null) {
                    pq.insert(bisector, p, p.distance(bot), ymin, deltay);
                }
            } else {
                break;
            }
        }
        for (lbnd = el.right(el.leftEnd); lbnd != el.rightEnd; lbnd = el.right(lbnd)) {
            e = lbnd.edge;
            out_ep(e, pxmin, pxmax, pymin, pymax);
        }
    }
}
