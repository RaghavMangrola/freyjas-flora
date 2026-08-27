package com.farmrunhelper;

enum PatchType
{
	HERB("Herbs", SceneHighlightPolicy.COMPOST_AWARE),
	HOPS("Hops", SceneHighlightPolicy.COMPOST_AWARE),
	TREE("Trees", SceneHighlightPolicy.COMPOST_AWARE),
	FRUIT_TREE("Fruit Trees", SceneHighlightPolicy.COMPOST_AWARE),
	HARDWOOD_TREE("Hardwood Trees", SceneHighlightPolicy.COMPOST_AWARE),
	CALQUAT("Calquat Trees", SceneHighlightPolicy.COMPOST_AWARE),
	CORAL("Coral", SceneHighlightPolicy.STANDARD),
	SEAWEED("Seaweed", SceneHighlightPolicy.COMPOST_AWARE),
	BIRD_HOUSE("Birdhouses", SceneHighlightPolicy.NONE);

	private final String displayName;
	private final SceneHighlightPolicy sceneHighlightPolicy;

	PatchType(String displayName, SceneHighlightPolicy sceneHighlightPolicy)
	{
		this.displayName = displayName;
		this.sceneHighlightPolicy = sceneHighlightPolicy;
	}

	String getDisplayName()
	{
		return displayName;
	}

	boolean supportsSceneHighlights()
	{
		return sceneHighlightPolicy != SceneHighlightPolicy.NONE;
	}

	boolean supportsCompostReminder()
	{
		return sceneHighlightPolicy == SceneHighlightPolicy.COMPOST_AWARE;
	}

	boolean usesTreeReadyAction()
	{
		return this == TREE || this == FRUIT_TREE || this == HARDWOOD_TREE || this == CALQUAT;
	}

	private enum SceneHighlightPolicy
	{
		NONE,
		STANDARD,
		COMPOST_AWARE
	}
}
