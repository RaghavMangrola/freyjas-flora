package com.farmrunhelper;

final class DecodedPatchState
{
	private final String cropName;
	private final PatchState state;
	private final int stage;
	private final int stages;
	private final int tickRateMinutes;

	DecodedPatchState(String cropName, PatchState state, int stage, int stages, int tickRateMinutes)
	{
		this.cropName = cropName;
		this.state = state;
		this.stage = stage;
		this.stages = stages;
		this.tickRateMinutes = tickRateMinutes;
	}

	String getCropName()
	{
		return cropName;
	}

	PatchState getState()
	{
		return state;
	}

	int getStage()
	{
		return stage;
	}

	int getStages()
	{
		return stages;
	}

	int getTickRateMinutes()
	{
		return tickRateMinutes;
	}
}
