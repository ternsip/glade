package com.ternsip.glade.universe.entities.base;

import com.ternsip.glade.common.logic.Maths;
import com.ternsip.glade.common.logic.Timer;
import lombok.Getter;
import org.joml.Vector3fc;

@Getter
public class VolumetricInterpolated {

    private final Volumetric curVolumetric = new Volumetric();
    private final Volumetric prevVolumetric = new Volumetric();
    private final Timer tickTimer = new Timer();

    public void setFromVolumetric(Volumetric volumetric) {
        if (volumetric.getLastTimeChanged() > getCurVolumetric().getLastTimeChanged()) {
            getPrevVolumetric().setFromVolumetric(getCurVolumetric());
            getCurVolumetric().setFromVolumetric(volumetric);
            getTickTimer().drop();
        }
    }

    public Vector3fc getPositionInterpolated() {
        return Maths.interpolate(getCurVolumetric().getPosition(), getPrevVolumetric().getPosition(), getTimeMultiplier());
    }

    public Vector3fc getScaleInterpolated() {
        return Maths.interpolate(getCurVolumetric().getScale(), getPrevVolumetric().getScale(), getTimeMultiplier());
    }

    public Vector3fc getRotationInterpolated() {
        return Maths.interpolate(getCurVolumetric().getRotation(), getPrevVolumetric().getRotation(), getTimeMultiplier());
    }

    public boolean isVisible() {
        // TODO make fade away effect (alpha 0f-1f) interpolated
        return getCurVolumetric().isVisible();
    }

    public void setPosition(Vector3fc position) {
        getCurVolumetric().setPosition(position);
    }

    public void setScale(Vector3fc scale) {
        getCurVolumetric().setScale(scale);
    }

    public void setRotation(Vector3fc rotation) {
        getCurVolumetric().setRotation(rotation);
    }

    public void setVisible(boolean visible) {
        getCurVolumetric().setVisible(visible);
    }

    private float getTimeMultiplier() {
        long timeGap = getCurVolumetric().getLastTimeChanged() - getPrevVolumetric().getLastTimeChanged();
        return timeGap <= 0 ? 0 : Maths.bound(0f, 1f, (timeGap - getTickTimer().spent()) / ((float)timeGap));
    }

}
