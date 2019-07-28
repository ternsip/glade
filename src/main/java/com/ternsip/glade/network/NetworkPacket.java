package com.ternsip.glade.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NetworkPacket<T extends Serializable> implements Serializable {

    private Class<T> clazz;
    private T object;

}
