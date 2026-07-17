package com.farmrunhelper;

enum PatchType
{
	HERB("Herbs"),
	TREE("Trees"),
	FRUIT_TREE("Fruit Trees"),
	HARDWOOD_TREE("Hardwood Trees"),
	CALQUAT("Calquat Trees"),
	CORAL("Coral"),
	SEAWEED("Seaweed");

	private final String displayName;

	PatchType(String displayName)
	{
		this.displayName = displayName;
	}

	String getDisplayName()
	{
		return displayName;
	}
}
