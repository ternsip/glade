package com.ternsip.glade.universe.collisions.base;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.universe.common.Universal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.AABBf;
import org.joml.LineSegmentf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter(AccessLevel.PRIVATE)
public class Collisions implements Universal {

    private final Octree octree = new Octree();

    public Collision collideSegmentFirstOrNull(LineSegmentf segment) {
        return collideSegment(segment).stream().findFirst().orElse(null);
    }

    public List<Collision> collideSegment(LineSegmentf segment) {
        return getOctree().collideSegment(segment);
    }

    public void add(Obstacle obstacle) {
        getOctree().add(obstacle);
    }

    public void remove(Obstacle obstacle) {
        getOctree().remove(obstacle);
    }

    public void update() {
        getOctree().update();
    }

    @Getter
    private static class Octree {

        private final OctreeNode root = new OctreeNode();
        private final Map<Obstacle, OctreeNode> obstacleToOctreeNode = new HashMap<>();
        private final Map<Obstacle, AABBf> obstacleToPreviousAABB = new HashMap<>();
        private final AtomicBoolean modified = new AtomicBoolean(false);

        public List<Collision> collideSegment(LineSegmentf segment) {
            return getRoot().collideSegment(segment);
        }

        public void add(Obstacle obstacle) {
            OctreeNode tree = getRoot().findTree(obstacle);
            getObstacleToOctreeNode().put(obstacle, tree);
            tree.getObstacles().add(obstacle);
            getObstacleToPreviousAABB().put(obstacle, obstacle.getAabb());
        }

        public void remove(Obstacle obstacle) {
            OctreeNode tree = getObstacleToOctreeNode().get(obstacle);
            getObstacleToOctreeNode().remove(obstacle);
            tree.getObstacles().remove(obstacle);
            getModified().set(true);
        }

        public void update() {
            getObstacleToPreviousAABB().entrySet().forEach(entry -> {

                Obstacle obstacle = entry.getKey();
                AABBf aabb = entry.getValue();
                AABBf newAABB = obstacle.getAabb();

                if (!aabb.equals(newAABB)) {
                    getObstacleToOctreeNode().get(obstacle).getObstacles().remove(obstacle);
                    OctreeNode addTreeNode = getRoot().findTree(obstacle);
                    getObstacleToOctreeNode().put(obstacle, addTreeNode);
                    addTreeNode.getObstacles().add(obstacle);
                    entry.setValue(newAABB);
                    getModified().set(true);
                }

            });
            if (getModified().get()) {
                getRoot().cleanEmptyChildren();
                getModified().set(false);
            }
        }

    }

    @RequiredArgsConstructor
    @Getter
    private static class OctreeNode {

        private final Set<Obstacle> obstacles = new HashSet<>();
        private final OctreeNode parent;
        private final OctreeNode[] children = new OctreeNode[8];
        private final int level;
        private final int dx;
        private final int dy;
        private final int dz;

        public OctreeNode() {
            this.parent = null;
            this.level = Integer.SIZE - 1;
            this.dx = Integer.MIN_VALUE;
            this.dy = Integer.MIN_VALUE;
            this.dz = Integer.MIN_VALUE;
        }

        public List<Collision> collideSegment(LineSegmentf segment) {
            AABBf aabb = new AABBf(
                    Math.min(segment.aX, segment.bX),
                    Math.min(segment.aY, segment.bY),
                    Math.min(segment.aZ, segment.bZ),
                    Math.max(segment.aX, segment.bX),
                    Math.max(segment.aY, segment.bY),
                    Math.max(segment.aZ, segment.bZ)
            );
            List<Collision> collisions = new ArrayList<>();
            ArrayDeque<OctreeNode> queue = new ArrayDeque<>();
            queue.push(this);
            while (!queue.isEmpty()) {
                OctreeNode top = queue.poll();
                top.getObstacles().forEach(e -> {
                    Vector3fc collisionPoint = e.collideSegment(segment);
                    if (collisionPoint != null) {
                        collisions.add(new Collision(e, collisionPoint));
                    }
                });
                for (OctreeNode child : top.getChildren()) {
                    if (child != null && child.isInside(aabb)) {
                        queue.push(child);
                    }
                }
            }
            Vector3f origin = new Vector3f(segment.aX, segment.aY, segment.aZ);
            collisions.sort(Comparator.comparing(e -> origin.distanceSquared(e.getPosition())));
            return collisions;
        }

        public OctreeNode findTree(Obstacle obstacle) {
            AABBf aabb = obstacle.getAabb();
            return findTree((int) aabb.minX, (int) aabb.minY, (int) aabb.minZ, (int) aabb.maxX, (int) aabb.maxY, (int) aabb.maxZ);
        }

        public boolean isEmpty() {
            for (OctreeNode child : getChildren()) {
                if (child != null) {
                    return false;
                }
            }
            return getObstacles().isEmpty();
        }

        public void cleanEmptyChildren() {
            for (int i = 0; i < getChildren().length; ++i) {
                getChildren()[i].cleanEmptyChildren();
                if (getChildren()[i].isEmpty()) {
                    getChildren()[i] = null;
                }
            }
        }

        private boolean isInside(AABBf aabb) {
            return dx >= aabb.minX && dx + (1 << level) < aabb.maxX &&
                    dy >= aabb.minY && dy + (1 << level) < aabb.maxY &&
                    dz >= aabb.minZ && dz + (1 << level) < aabb.maxZ;
        }

        private OctreeNode findTree(int sx, int sy, int sz, int ex, int ey, int ez) {

            int pLevel = 1 << level;
            int tsx = sx >= dx + pLevel ? 1 : 0, tsy = sy >= dy + pLevel ? 1 : 0, tsz = sz >= dz + pLevel ? 1 : 0;
            int tex = ex >= dx + pLevel ? 1 : 0, tey = ey >= dy + pLevel ? 1 : 0, tez = ez >= dz + pLevel ? 1 : 0;

            if (tsx != tex || tsy != tey || tsz != tez) {
                return this;
            }

            int idx = tsx + tsy * 2 + tsz * 4;
            int csx = tsx << level, csy = tsy << level, csz = tsz << level;

            if (getChildren()[idx] == null) {
                Utils.assertThat(level > 0);
                getChildren()[idx] = new OctreeNode(this, level - 1, dx + csx, dy + csy, dz + csz);
            }

            return getChildren()[idx].findTree(sx, sy, sz, ex, ey, ez);

        }

    }

}
