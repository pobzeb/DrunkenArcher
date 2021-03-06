package com.sudosoftware.drunkenarcher.world;

public enum BlockType {
	AIR((byte)0, new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}),
	DIRT((byte)1, new float[]{2.0f, 0.0f}, new float[]{2.0f, 0.0f}, new float[]{2.0f, 0.0f}),
	GRASS((byte)2, new float[]{0.0f, 0.0f}, new float[]{3.0f, 0.0f}, new float[]{2.0f, 0.0f}),
	STONE((byte)3, new float[]{1.0f, 0.0f}, new float[]{1.0f, 0.0f}, new float[]{1.0f, 0.0f});

	public byte id;
	public float[] textureIDTop;
	public float[] textureIDSide;
	public float[] textureIDBottom;

	private BlockType(byte id, float[] textureIDTop, float[] textureIDSide, float[] textureIDBottom) {
		this.id = id;
		this.textureIDTop = textureIDTop;
		this.textureIDSide = textureIDSide;
		this.textureIDBottom = textureIDBottom;
	}

	public static BlockType findByID(byte id) {
		for (BlockType blockType : BlockType.values()) {
			if (blockType.id == id) {
				return blockType;
			}
		}

		return null;
	}
}
