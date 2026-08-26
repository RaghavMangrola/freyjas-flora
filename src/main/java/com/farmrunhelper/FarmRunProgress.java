package com.farmrunhelper;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Tracks only the user's navigation progress for the current farm run. */
final class FarmRunProgress
{
	private final EnumSet<FarmPatch> donePatches = EnumSet.noneOf(FarmPatch.class);
	private FarmPatch currentPatch;

	FarmPatch start(List<PatchSnapshot> orderedSnapshots)
	{
		donePatches.clear();
		currentPatch = firstNotDone(orderedSnapshots);
		return currentPatch;
	}

	FarmPatch advance(List<PatchSnapshot> orderedSnapshots)
	{
		if (currentPatch != null)
		{
			donePatches.add(currentPatch);
		}
		currentPatch = firstNotDone(orderedSnapshots);
		return currentPatch;
	}

	boolean toggleDone(FarmPatch patch, List<PatchSnapshot> orderedSnapshots)
	{
		if (!donePatches.remove(patch))
		{
			return false;
		}

		if (currentPatch == null)
		{
			currentPatch = firstNotDone(orderedSnapshots);
		}
		return true;
	}

	void select(FarmPatch patch)
	{
		donePatches.remove(patch);
		currentPatch = patch;
	}

	void clear()
	{
		donePatches.clear();
		currentPatch = null;
	}

	FarmPatch getCurrentPatch()
	{
		return currentPatch;
	}

	Set<FarmPatch> getDonePatches()
	{
		return Collections.unmodifiableSet(donePatches);
	}

	private FarmPatch firstNotDone(List<PatchSnapshot> orderedSnapshots)
	{
		for (PatchSnapshot snapshot : orderedSnapshots)
		{
			if (!donePatches.contains(snapshot.getPatch()))
			{
				return snapshot.getPatch();
			}
		}
		return null;
	}
}
