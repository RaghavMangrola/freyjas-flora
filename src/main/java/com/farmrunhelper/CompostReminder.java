package com.farmrunhelper;

final class CompostReminder
{
	private CompostReminder()
	{
	}

	static boolean shouldShow(FarmPatch patch, PatchState state, boolean hasRecordedCompost)
	{
		if (!patch.supportsCompostReminder() || hasRecordedCompost)
		{
			return false;
		}

		return state == PatchState.EMPTY || state == PatchState.GROWING;
	}
}
