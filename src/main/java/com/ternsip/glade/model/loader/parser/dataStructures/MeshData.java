package com.ternsip.glade.model.loader.parser.dataStructures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class MeshData {

	private static final int DIMENSIONS = 3;

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private int[] jointIds;
	private float[] vertexWeights;

}
