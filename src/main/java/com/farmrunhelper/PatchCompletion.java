package com.farmrunhelper;

final class PatchCompletion
{
	private PatchCompletion()
	{
	}

	static boolean wasReplanted(PatchState previous, PatchState current)
	{
		if (current != PatchState.GROWING)
		{
			return false;
		}

		switch (previous)
		{
			case READY:
			case EMPTY:
			case DEAD:
			case DISEASED:
				return true;
			default:
				return false;
		}
	}
}
