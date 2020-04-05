package com.ternsip.glade.universe.entities.impl;

import com.ternsip.glade.graphics.visual.impl.basis.EffigyLightMass;
import com.ternsip.glade.universe.entities.base.GraphicalEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class EntityLightMass extends GraphicalEntity<EffigyLightMass> {

    private EffigyLightMass effigyLightMass;

    @Override
    public EffigyLightMass getEffigy() {
        effigyLightMass = new EffigyLightMass(getUniverseClient().getBlocksClientRepository().getChangeBlocksRequests());
        return effigyLightMass;
    }

}
