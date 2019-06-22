package com.ternsip.glade.graphics.visual.impl.basis;

import com.ternsip.glade.common.logic.Indexer;
import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.graphics.general.Material;
import com.ternsip.glade.graphics.general.Mesh;
import com.ternsip.glade.graphics.general.Model;
import com.ternsip.glade.graphics.shader.base.MeshAttributes;
import com.ternsip.glade.graphics.shader.impl.ChunkShader;
import com.ternsip.glade.graphics.visual.base.Effigy;
import com.ternsip.glade.graphics.visual.repository.TexturePackRepository;
import com.ternsip.glade.graphics.visual.repository.TextureRepository;
import com.ternsip.glade.universe.common.Light;
import com.ternsip.glade.universe.common.Universal;
import com.ternsip.glade.universe.parts.blocks.Block;
import com.ternsip.glade.universe.parts.blocks.BlockSide;
import com.ternsip.glade.universe.parts.chunks.BlocksUpdate;
import com.ternsip.glade.universe.parts.chunks.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.*;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.ternsip.glade.graphics.shader.base.ShaderProgram.INDICES;
import static com.ternsip.glade.graphics.shader.base.ShaderProgram.VERTICES;
import static com.ternsip.glade.graphics.shader.impl.ChunkShader.*;
import static com.ternsip.glade.graphics.visual.impl.basis.EffigyChunks.SideIndexData.*;
import static com.ternsip.glade.universe.parts.chunks.Chunks.MAX_LIGHT_LEVEL;

@Getter
@Setter
public class EffigyChunks extends Effigy<ChunkShader> implements Universal {

    private static final float SIDE = 0.5f;
    private static final float BLOCK_PHYSICAL_SIZE = 2 * SIDE;
    private static final float CHUNK_PHYSICAL_SIZE = BLOCK_PHYSICAL_SIZE * Chunk.SIZE;

    private static final CubeSideMeshData SIDE_FRONT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE, SIDE},
            new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            new boolean[]{true, false, false, false, false, true, true, true},
            BlockSide.FRONT
    );

    private static final CubeSideMeshData SIDE_RIGHT = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0},
            new boolean[]{false, false, false, true, true, true, true, false},
            BlockSide.RIGHT
    );

    private static final CubeSideMeshData SIDE_TOP = new CubeSideMeshData(
            new float[]{SIDE, SIDE, SIDE, SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, SIDE},
            new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            new boolean[]{true, true, true, false, false, false, false, true},
            BlockSide.TOP
    );

    private static final CubeSideMeshData SIDE_LEFT = new CubeSideMeshData(
            new float[]{-SIDE, SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE},
            new float[]{-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0},
            new boolean[]{true, false, false, false, false, true, true, true},
            BlockSide.LEFT
    );

    private static final CubeSideMeshData SIDE_BOTTOM = new CubeSideMeshData(
            new float[]{-SIDE, -SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, -SIDE, -SIDE, SIDE},
            new float[]{0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0},
            new boolean[]{false, true, true, true, true, false, false, false},
            BlockSide.BOTTOM
    );

    private static final CubeSideMeshData SIDE_BACK = new CubeSideMeshData(
            new float[]{SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, -SIDE, SIDE, -SIDE, SIDE, SIDE, -SIDE},
            new float[]{0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1},
            new boolean[]{false, true, true, true, true, false, false, false},
            BlockSide.BACK
    );

    private static final CubeSideMeshData ALL_SIDES[] = new CubeSideMeshData[]{
            SIDE_FRONT, SIDE_BACK, SIDE_LEFT, SIDE_RIGHT, SIDE_TOP, SIDE_BOTTOM
    };

    private final int viewDistance;
    private final Vector3i shift;
    private final Block[][][] blocks;
    private final byte[][][] light;
    private final int[][] heights;
    private final Integer[][][][] sidesIndex;
    private final Indexer indexer;
    private ArrayList<SidePosition> activeSides = new ArrayList<>();

    public EffigyChunks(int viewDistance) {
        this.viewDistance = viewDistance;
        this.shift = new Vector3i(0, 0, 0);
        this.blocks = new Block[viewDistance][viewDistance][viewDistance];
        this.light = new byte[viewDistance][viewDistance][viewDistance];
        this.heights = new int[viewDistance][viewDistance];
        this.sidesIndex = new Integer[ALL_SIDES.length][viewDistance][viewDistance][viewDistance];
        this.indexer = new Indexer(new Vector3i(getViewDistance()));
    }

    public void recalculateBlockRegion(BlocksUpdate blocksUpdate) {
        Vector3ic arraySize = new Vector3i(
                blocksUpdate.getBlocks().length,
                blocksUpdate.getBlocks()[0].length,
                blocksUpdate.getBlocks()[0][0].length
        );
        Vector3ic viewEndExcluding = new Vector3i(getShift()).add(new Vector3i(getViewDistance()));
        Vector3ic realStart = new Vector3i(blocksUpdate.getStart()).max(getShift());
        Vector3i realEndExcluding = new Vector3i(blocksUpdate.getStart()).add(arraySize).min(viewEndExcluding);
        Vector3ic start = new Vector3i(realStart).sub(blocksUpdate.getStart());
        Vector3ic endExcluding = new Vector3i(realEndExcluding).sub(blocksUpdate.getStart());
        Vector3i size = new Vector3i(endExcluding).sub(start);

        if (size.x() <= 0 || size.y() <= 0 || size.z() <= 0) {
            return;
        }

        // Recalculate light maps
        Queue<Integer> queue = new ArrayDeque<>();
        Set<Integer> changedBlocks = new HashSet<>();

        for (int x = start.x(), cx = realStart.x(); x < endExcluding.x(); ++x, ++cx) {
            for (int z = start.z(), cz = realStart.z(); z < endExcluding.z(); ++z, ++cz) {
                int rcx = Math.floorMod(cx, getViewDistance());
                int rcz = Math.floorMod(cz, getViewDistance());
                int height = blocksUpdate.getHeights()[x][z];
                getHeights()[rcx][rcz] = height;
                for (int y = start.y(), cy = realStart.y(); y < endExcluding.y(); ++y, ++cy) {
                    int rcy = Math.floorMod(cy, getViewDistance());
                    Block newBlock = blocksUpdate.getBlocks()[x][y][z];
                    byte newLight = newBlock.getEmitLight();
                    int posIndex = getIndexer().getIndex(rcx, rcy, rcz);
                    if (height <= cy) {
                        newLight = MAX_LIGHT_LEVEL;
                    }
                    if (newLight > 0 && height >= cy) {
                        queue.add(posIndex);
                    }
                    if (blocksUpdate.isForceUpdate() || newBlock != getBlocks()[rcx][rcy][rcz] || newLight != getLight()[rcx][rcy][rcz]) {
                        getBlocks()[rcx][rcy][rcz] = newBlock;
                        getLight()[rcx][rcy][rcz] = newLight;
                        changedBlocks.add(posIndex);
                    }
                }
            }
        }

        // TODO reset MAX_LIGHT_LEVEL distance border to update light

        // Add border blocks to engage neighbour side-recalculation
        ArrayList<Vector3i> borderPositions = new ArrayList<>();
        for (int y = realStart.y(); y < realEndExcluding.y(); ++y) {
            for (int z = realStart.z(); z < realEndExcluding.z(); ++z) {
                borderPositions.add(new Vector3i(realStart.x() - 1, y, z));
                borderPositions.add(new Vector3i(realEndExcluding.x(), y, z));
            }
        }
        for (int x = realStart.x(); x < realEndExcluding.x(); ++x) {
            for (int z = realStart.z(); z < realEndExcluding.z(); ++z) {
                borderPositions.add(new Vector3i(x, realStart.y() - 1, z));
                borderPositions.add(new Vector3i(x, realEndExcluding.y(), z));
            }
        }
        for (int x = realStart.x(); x < realEndExcluding.x(); ++x) {
            for (int y = realStart.y(); y < realEndExcluding.y(); ++y) {
                borderPositions.add(new Vector3i(x, y, realStart.z() - 1));
                borderPositions.add(new Vector3i(x, y, realEndExcluding.z()));
            }
        }
        borderPositions.forEach(pos -> {
            if (pos.x() >= getShift().x() && pos.y() >= getShift().y() && pos.z() >= getShift().z() &&
                    pos.x() < viewEndExcluding.x() && pos.y() < viewEndExcluding.y() && pos.z() < viewEndExcluding.z()) {
                int rcx = Math.floorMod(pos.x(), getViewDistance());
                int rcy = Math.floorMod(pos.y(), getViewDistance());
                int rcz = Math.floorMod(pos.z(), getViewDistance());
                changedBlocks.add(getIndexer().getIndex(rcx, rcy, rcz));
            }
        });

        // Start light propagation BFS
        int[] dx = {1, 0, 0, -1, 0, 0};
        int[] dy = {0, 1, 0, 0, -1, 0};
        int[] dz = {0, 0, 1, 0, 0, -1};
        while (!queue.isEmpty()) {
            Integer top = queue.poll();
            int x = getIndexer().getX(top);
            int y = getIndexer().getY(top);
            int z = getIndexer().getZ(top);
            byte lightLevel = getLight()[x][y][z];
            for (int k = 0; k < dx.length; ++k) {
                int nx = Math.floorMod(x + dx[k], getViewDistance());
                int ny = Math.floorMod(y + dy[k], getViewDistance());
                int nz = Math.floorMod(z + dz[k], getViewDistance());
                if (isTransitionImpossible(x, y, z, nx, ny, nz)) {
                    continue;
                }
                byte dstLightOpacity = getBlocks()[nx][ny][nz] == null ? MAX_LIGHT_LEVEL : getBlocks()[nx][ny][nz].getLightOpacity();
                byte dstLight = (byte) (lightLevel - dstLightOpacity);
                if (getLight()[nx][ny][nz] < dstLight) {
                    getLight()[nx][ny][nz] = dstLight;
                    int nIndex = getIndexer().getIndex(nx, ny, nz);
                    queue.add(nIndex);
                    changedBlocks.add(nIndex);
                }
            }
        }

        // Calculate which sides should be removed or added
        TexturePackRepository texturePackRepository = getGraphics().getGraphicalRepository().getTexturePackRepository();
        TreeSet<Integer> sidesToRemove = new TreeSet<>();
        List<SideData> sidesToAdd = new ArrayList<>();
        changedBlocks.forEach(index -> {

            int x = getIndexer().getX(index);
            int y = getIndexer().getY(index);
            int z = getIndexer().getZ(index);

            for (int side = 0; side < ALL_SIDES.length; ++side) {
                Integer sideToRemoveIndex = getSidesIndex()[side][x][y][z];
                if (sideToRemoveIndex != null) {
                    sidesToRemove.add(sideToRemoveIndex);
                }
            }
            Block block = getBlocks()[x][y][z];
            if (block == null || block == Block.AIR) {
                return;
            }
            TexturePackRepository.TextureCubeMap textureCubeMap = texturePackRepository.getCubeMap(block);

            for (int side = 0; side < ALL_SIDES.length; ++side) {
                CubeSideMeshData meshDataSide = ALL_SIDES[side];
                BlockSide blockSide = meshDataSide.getBlockSide();
                int nx = Math.floorMod(x + blockSide.getAdjacentBlockOffset().x(), getViewDistance());
                int ny = Math.floorMod(y + blockSide.getAdjacentBlockOffset().y(), getViewDistance());
                int nz = Math.floorMod(z + blockSide.getAdjacentBlockOffset().z(), getViewDistance());
                if (isTransitionImpossible(x, y, z, nx, ny, nz)) {
                    sidesToAdd.add(new SideData(new SidePosition(side, x, y, z), 0f, textureCubeMap, meshDataSide));;
                    continue;
                }
                Block nextBlock = getBlocks()[nx][ny][nz];
                if (nextBlock == null || (nextBlock.isSemiTransparent() && (block != nextBlock || !block.isCombineSides()))) {
                    float sideLight = (float) getLight()[nx][ny][nz] / MAX_LIGHT_LEVEL;
                    sidesToAdd.add(new SideData(new SidePosition(side, x, y, z), sideLight, textureCubeMap, meshDataSide));
                }
            }

        });

        updateSides(sidesToRemove, sidesToAdd);
    }

    private boolean isTransitionImpossible(int x, int y, int z, int nx, int ny, int nz) {
        int cx = Math.floorMod(getShift().x(), getViewDistance());
        int cy = Math.floorMod(getShift().y(), getViewDistance());
        int cz = Math.floorMod(getShift().z(), getViewDistance());
        return (x == cx && nx == cx - 1) || (x == cx - 1 && nx == cx) ||
                (y == cy && ny == cy - 1) || (y == cy - 1 && ny == cy) ||
                (z == cz && nz == cz - 1) || (z == cz - 1 && nz == cz);
    }

    private void updateSides(TreeSet<Integer> sidesToRemove, List<SideData> sidesToAdd) {
        Material material = new Material(getGraphics().getGraphicalRepository().getTexturePackRepository().getBlockAtlasTexture());
        int oldLength = getActiveSides().size();
        int newLength = oldLength + (sidesToAdd.size() - sidesToRemove.size());
        int meshesNumber = newLength / SIDES_PER_MESH + (newLength % SIDES_PER_MESH > 0 ? 1 : 0);
        while (getModel().getMeshes().size() < meshesNumber) {
            Mesh mesh = new Mesh(
                    new MeshAttributes()
                            .add(INDICES, Utils.arrayToBuffer(new int[SIDES_PER_MESH * INDEX_SIDE_SIZE]))
                            .add(VERTICES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * VERTEX_SIDE_SIZE]))
                            .add(COLORS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * COLOR_SIDE_SIZE]))
                            .add(NORMALS, Utils.arrayToBuffer(new float[SIDES_PER_MESH * NORMAL_SIDE_SIZE]))
                            .add(TEXTURES, Utils.arrayToBuffer(new float[SIDES_PER_MESH * TEXTURE_SIDE_SIZE])),
                    material, true
            );
            mesh.setIndicesCount(0);
            mesh.setVertexCount(0);
            getModel().getMeshes().add(mesh);
        }
        while (getModel().getMeshes().size() > meshesNumber) {
            int currentSize = getModel().getMeshes().size();
            getModel().getMeshes().get(currentSize - 1).finish();
            getModel().getMeshes().remove(currentSize - 1);
        }
        int sizeOfTheLastMesh = newLength % SIDES_PER_MESH;
        for (int i = 0; i < meshesNumber; ++i) {
            getModel().getMeshes().get(i).setVertexCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * VERTICES_PER_SIDE + 1) : SIDES_PER_MESH * VERTICES_PER_SIDE);
            getModel().getMeshes().get(i).setIndicesCount((i == meshesNumber - 1) ? (sizeOfTheLastMesh * SIDE_INDICES.length + 1) : SIDES_PER_MESH * SIDE_INDICES.length);
        }
        Iterator<Integer> toRemove = sidesToRemove.iterator();
        for (SideData sideData : sidesToAdd) {
            if (toRemove.hasNext()) {
                int sideIndex = toRemove.next();
                fillSide(sideIndex, sideData);
                setSideIndex(getActiveSides().get(sideIndex), null);
                setSideIndex(sideData.getSidePosition(), sideIndex);
                getActiveSides().set(sideIndex, sideData.getSidePosition());
            } else {
                int sideIndex = getActiveSides().size();
                fillSide(sideIndex, sideData);
                setSideIndex(sideData.getSidePosition(), sideIndex);
                getActiveSides().add(sideData.getSidePosition());
            }
        }
        while (toRemove.hasNext()) {
            int sideIndexDst = toRemove.next();
            int sideIndexSrc = getActiveSides().size() - 1;
            relocateSide(sideIndexSrc, sideIndexDst);
            SidePosition sidePositionDst = getActiveSides().get(sideIndexDst);
            SidePosition sidePositionSrc = getActiveSides().get(sideIndexSrc);
            setSideIndex(sidePositionDst, null);
            setSideIndex(sidePositionSrc, sideIndexDst);
            getActiveSides().set(sideIndexDst, sidePositionSrc);
            getActiveSides().remove(sideIndexSrc);
        }
        if (sidesToRemove.size() > 0 || sidesToAdd.size() > 0) {
            for (Mesh mesh : getModel().getMeshes()) {
                mesh.updateBuffers();
            }
        }
    }

    private void setSideIndex(SidePosition sidePosition, Integer index) {
        getSidesIndex()[sidePosition.getSide()][sidePosition.getX()][sidePosition.getY()][sidePosition.getZ()] = index;
    }

    private void relocateSide(int sideIndexSrc, int sideIndexDst) {

        SideIndexData sideIndexDataSrc = new SideIndexData(sideIndexSrc, getModel());
        SideIndexData sideIndexDataDst = new SideIndexData(sideIndexDst, getModel());

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexDataDst.getIndices().put(i + sideIndexDataDst.getIndexPos(), SIDE_INDICES[i] + sideIndexDataDst.getVertexStart());
        }

        float[] vertices = new float[VERTEX_SIDE_SIZE];
        sideIndexDataSrc.getVertices().position(sideIndexDataSrc.getVertexPos());
        sideIndexDataDst.getVertices().position(sideIndexDataDst.getVertexPos());
        sideIndexDataSrc.getVertices().get(vertices);
        sideIndexDataDst.getVertices().put(vertices);

        float[] colors = new float[COLOR_SIDE_SIZE];
        sideIndexDataSrc.getColors().position(sideIndexDataSrc.getColorPos());
        sideIndexDataDst.getColors().position(sideIndexDataDst.getColorPos());
        sideIndexDataSrc.getColors().get(colors);
        sideIndexDataDst.getColors().put(colors);

        float[] normals = new float[NORMAL_SIDE_SIZE];
        sideIndexDataSrc.getNormals().position(sideIndexDataSrc.getNormalPos());
        sideIndexDataDst.getNormals().position(sideIndexDataDst.getNormalPos());
        sideIndexDataSrc.getNormals().get(normals);
        sideIndexDataDst.getNormals().put(normals);

        float[] textures = new float[TEXTURE_SIDE_SIZE];
        sideIndexDataSrc.getTextures().position(sideIndexDataSrc.getTexturePos());
        sideIndexDataDst.getTextures().position(sideIndexDataDst.getTexturePos());
        sideIndexDataSrc.getTextures().get(textures);
        sideIndexDataDst.getTextures().put(textures);

    }

    private void fillSide(int sideIndex, SideData sideData) {

        SideIndexData sideIndexData = new SideIndexData(sideIndex, getModel());
        CubeSideMeshData cubeSideMeshData = sideData.getCubeSideMeshData();
        int dx = getShift().x() + (sideData.getSidePosition().getX() - getShift().x()) % getViewDistance();
        int dy = getShift().y() + (sideData.getSidePosition().getY() - getShift().y()) % getViewDistance();
        int dz = getShift().z() + (sideData.getSidePosition().getZ() - getShift().z()) % getViewDistance();
        TextureRepository.AtlasFragment atlasFragment = sideData.getTextureCubeMap().getByBlockSide(cubeSideMeshData.getBlockSide());

        for (int i = 0; i < SIDE_INDICES.length; ++i) {
            sideIndexData.getIndices().put(i + sideIndexData.getIndexPos(), SIDE_INDICES[i] + sideIndexData.getVertexStart());
        }
        for (int i = 0; i < VERTICES_PER_SIDE; i++) {
            int vIdx = i * VERTICES.getNumberPerVertex();
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos(), cubeSideMeshData.getVertices()[vIdx] + dx * BLOCK_PHYSICAL_SIZE);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 1, cubeSideMeshData.getVertices()[vIdx + 1] + dy * BLOCK_PHYSICAL_SIZE);
            sideIndexData.getVertices().put(vIdx + sideIndexData.getVertexPos() + 2, cubeSideMeshData.getVertices()[vIdx + 2] + dz * BLOCK_PHYSICAL_SIZE);

            int cIdx = i * COLORS.getNumberPerVertex();
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos(), 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 1, 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 2, 0f);
            sideIndexData.getColors().put(cIdx + sideIndexData.getColorPos() + 3, sideData.getAmbientLight());

            int nIdx = i * NORMALS.getNumberPerVertex();
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos(), cubeSideMeshData.getNormals()[nIdx]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 1, cubeSideMeshData.getNormals()[nIdx + 1]);
            sideIndexData.getNormals().put(nIdx + sideIndexData.getNormalPos() + 2, cubeSideMeshData.getNormals()[nIdx + 2]);

            int tIdx = i * TEXTURES.getNumberPerVertex();
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos(), cubeSideMeshData.getTextures()[tIdx] ? atlasFragment.getEndU() : atlasFragment.getStartU());
            sideIndexData.getTextures().put(tIdx + sideIndexData.getTexturePos() + 1, cubeSideMeshData.getTextures()[tIdx + 1] ? atlasFragment.getEndV() : atlasFragment.getStartV());
        }

    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4fc rotMatrix = Maths.getRotationQuaternion(getAdjustedRotation()).get(new Matrix4f());
        return new Matrix4f().translate(getAdjustedPosition()).mul(rotMatrix).scale(getAdjustedScale());
    }

    @Override
    public void render(Set<Light> lights) {
        getShader().start();
        getShader().getProjectionMatrix().load(getProjectionMatrix());
        getShader().getViewMatrix().load(getViewMatrix());
        getShader().getTransformationMatrix().load(getTransformationMatrix());
        for (Mesh mesh : getModel().getMeshes()) {
            getShader().getDiffuseMap().load(mesh.getMaterial().getDiffuseMap());
            getShader().getSpecularMap().load(mesh.getMaterial().getSpecularMap());
            getShader().getAmbientMap().load(mesh.getMaterial().getAmbientMap());
            getShader().getEmissiveMap().load(mesh.getMaterial().getEmissiveMap());
            mesh.render();
        }
        getShader().stop();
    }

    @Override
    public Model loadModel() {
        return new Model(
                new ArrayList<>(),
                new Vector3f(SIDE),
                new Vector3f(0),
                new Vector3f(1)
        );
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isGraphicalInsideFrustum() {
        return true;
    }

    @Override
    public float getSquaredDistanceToCamera() {
        return 0;
    }

    @Override
    public Class<ChunkShader> getShaderClass() {
        return ChunkShader.class;
    }

    @Override
    public Object getModelKey() {
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    public static class CubeSideMeshData {

        private final float[] vertices;
        private final float[] normals;
        private final boolean[] textures;
        private final BlockSide blockSide;

    }

    @RequiredArgsConstructor
    @Getter
    public static class SideIndexData {

        public static final int VERTICES_PER_SIDE = 4;
        public static final int[] SIDE_INDICES = new int[]{0, 1, 2, 2, 3, 0};
        public static final int SIDES_PER_MESH = Mesh.MAX_VERTICES / VERTICES_PER_SIDE;

        public static final int VERTEX_SIDE_SIZE = VERTICES_PER_SIDE * VERTICES.getNumberPerVertex();
        public static final int INDEX_SIDE_SIZE = SIDE_INDICES.length;
        public static final int NORMAL_SIDE_SIZE = VERTICES_PER_SIDE * NORMALS.getNumberPerVertex();
        public static final int TEXTURE_SIDE_SIZE = VERTICES_PER_SIDE * TEXTURES.getNumberPerVertex();
        public static final int COLOR_SIDE_SIZE = VERTICES_PER_SIDE * COLORS.getNumberPerVertex();

        int vertexStart;
        int vertexPos;
        int indexPos;
        int normalPos;
        int texturePos;
        int colorPos;

        IntBuffer indices;
        FloatBuffer vertices;
        FloatBuffer colors;
        FloatBuffer normals;
        FloatBuffer textures;

        public SideIndexData(int sideIndex, Model model) {

            int meshPos = sideIndex / SIDES_PER_MESH;
            int sideOffset = sideIndex % SIDES_PER_MESH;
            Mesh mesh = model.getMeshes().get(meshPos);
            MeshAttributes meshAttributes = mesh.getMeshAttributes();

            this.vertexStart = sideOffset * VERTICES_PER_SIDE;
            this.vertexPos = sideOffset * VERTEX_SIDE_SIZE;
            this.indexPos = sideOffset * INDEX_SIDE_SIZE;
            this.normalPos = sideOffset * NORMAL_SIDE_SIZE;
            this.texturePos = sideOffset * TEXTURE_SIDE_SIZE;
            this.colorPos = sideOffset * COLOR_SIDE_SIZE;

            this.indices = meshAttributes.getBuffer(INDICES);
            this.vertices = meshAttributes.getBuffer(VERTICES);
            this.colors = meshAttributes.getBuffer(COLORS);
            this.normals = meshAttributes.getBuffer(NORMALS);
            this.textures = meshAttributes.getBuffer(TEXTURES);

        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class SideData {

        private final SidePosition sidePosition;
        private final float ambientLight;
        private final TexturePackRepository.TextureCubeMap textureCubeMap;
        private final CubeSideMeshData cubeSideMeshData;

    }

    @RequiredArgsConstructor
    @Getter
    public static class SidePosition {

        private final int side;
        private final int x;
        private final int y;
        private final int z;

    }

}
