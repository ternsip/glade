package com.ternsip.glade.universe.parts.items;

import com.ternsip.glade.universe.entities.impl.EntityPlayer;
import com.ternsip.glade.universe.entities.impl.EntityPlayerServer;
import com.ternsip.glade.universe.parts.tools.Schematic;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.File;

@Getter
@Setter
public class ItemSelectTool extends Item {

    private Vector3ic startPos = new Vector3i(0);
    private Vector3ic endPos = new Vector3i(0);
    private int used = 0;

    @Override
    public void useOnServer(EntityPlayerServer player) {
        Vector3ic pos = getUniverseServer().getBlocksServerRepository().traverse(player.getEyeSegment(), (b, p) -> b.isObstacle());
        if (pos != null && getUniverseServer().getBlocksServerRepository().isBlockExists(pos)) {
            if (getUsed() == 0) {
                setStartPos(pos);
            }
            if (getUsed() == 1) {
                setEndPos(pos);
            }
            if (getUsed() == 2) {
                new Schematic(getStartPos(), getEndPos()).save(new File("schematics/latest" + Schematic.EXTENSION));
            }
            setUsed((getUsed() + 1) % 3);
        }
    }

    @Override
    public void useOnClient(EntityPlayer player) {
    }

    @Override
    public Object getKey() {
        return new File("interface/wand2.png");
    }

}
