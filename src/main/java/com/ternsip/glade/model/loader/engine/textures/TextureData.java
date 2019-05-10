package com.ternsip.glade.model.loader.engine.textures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TextureData {

    private final byte[] data;
	private final int width;
	private final int height;

}
