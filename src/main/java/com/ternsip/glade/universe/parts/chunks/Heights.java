package com.ternsip.glade.universe.parts.chunks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class Heights implements Serializable {

    private int throughAir;
    private int throughGas;
    private int throughGasAndLiquid;
    private int untilSoil;

}