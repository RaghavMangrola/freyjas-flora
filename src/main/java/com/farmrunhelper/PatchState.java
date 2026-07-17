package com.farmrunhelper;

enum PatchState
{
	READY,
	GROWING,
	DISEASED,
	DEAD,
	EMPTY,
	UNKNOWN;

	boolean isActionable(boolean includeEmpty, boolean includeUnknown)
	{
		switch (this)
		{
			case READY:
			case DISEASED:
			case DEAD:
				return true;
			case EMPTY:
				return includeEmpty;
			case UNKNOWN:
				return includeUnknown;
			default:
				return false;
		}
	}
}
