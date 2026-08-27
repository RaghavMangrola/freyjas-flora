package com.farmrunhelper;

final class PatchPrediction
{
	private final String cropName;
	private final PatchState observedState;
	private final long observedAt;
	private final long readyAt;

	private PatchPrediction(String cropName, PatchState observedState, long observedAt, long readyAt)
	{
		this.cropName = cropName;
		this.observedState = observedState;
		this.observedAt = observedAt;
		this.readyAt = readyAt;
	}

	static PatchPrediction known(String cropName, PatchState state, long observedAt, long readyAt)
	{
		return new PatchPrediction(cropName, state, observedAt, readyAt);
	}

	static PatchPrediction unknown()
	{
		return new PatchPrediction("Unknown", PatchState.UNKNOWN, 0, 0);
	}

	String getCropName()
	{
		return cropName;
	}

	PatchState getObservedState()
	{
		return observedState;
	}

	long getObservedAt()
	{
		return observedAt;
	}

	long getReadyAt()
	{
		return readyAt;
	}

	PatchState getEffectiveState(long now)
	{
		if (observedState == PatchState.GROWING && readyAt > 0 && readyAt <= now)
		{
			return PatchState.READY;
		}
		return observedState;
	}
}
