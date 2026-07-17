package com.farmrunhelper;

import net.runelite.client.plugins.timetracking.farming.CompostState;

final class PatchSnapshot
{
	private final FarmPatch patch;
	private final PatchPrediction prediction;
	private final CompostState compostState;

	PatchSnapshot(FarmPatch patch, PatchPrediction prediction)
	{
		this(patch, prediction, null);
	}

	PatchSnapshot(FarmPatch patch, PatchPrediction prediction, CompostState compostState)
	{
		this.patch = patch;
		this.prediction = prediction;
		this.compostState = compostState;
	}

	FarmPatch getPatch()
	{
		return patch;
	}

	PatchPrediction getPrediction()
	{
		return prediction;
	}

	CompostState getCompostState()
	{
		return compostState;
	}
}
