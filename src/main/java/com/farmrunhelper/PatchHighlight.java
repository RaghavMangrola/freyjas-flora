package com.farmrunhelper;

enum PatchHighlight
{
	COMPOST,
	READY,
	EMPTY,
	DEAD,
	DISEASED;

	static PatchHighlight forPatch(FarmPatch patch, PatchState state, boolean hasRecordedCompost)
	{
		switch (state)
		{
			case READY:
				return READY;
			case EMPTY:
				return EMPTY;
			case DEAD:
				return DEAD;
			case DISEASED:
				return DISEASED;
			case GROWING:
				return CompostReminder.shouldShow(patch, state, hasRecordedCompost) ? COMPOST : null;
			default:
				return null;
		}
	}
}
