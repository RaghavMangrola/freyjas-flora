package com.farmrunhelper;

import java.util.List;
import java.util.Optional;

final class RunPlanner
{
	Optional<PatchSnapshot> next(
		List<PatchSnapshot> snapshots,
		FarmPatch afterPatch,
		long now,
		boolean includeEmpty,
		boolean includeUnknown,
		SeedInventory seedInventory,
		boolean skipEmptyWithoutSeed)
	{
		if (snapshots.isEmpty())
		{
			return Optional.empty();
		}

		int afterIndex = -1;
		if (afterPatch != null)
		{
			for (int index = 0; index < snapshots.size(); index++)
			{
				if (snapshots.get(index).getPatch() == afterPatch)
				{
					afterIndex = index;
					break;
				}
			}
		}

		for (int offset = 1; offset <= snapshots.size(); offset++)
		{
			PatchSnapshot snapshot = snapshots.get((afterIndex + offset) % snapshots.size());
			PatchState state = snapshot.getPrediction().getEffectiveState(now);
			if (state.isActionable(includeEmpty, includeUnknown)
				&& !(skipEmptyWithoutSeed && state == PatchState.EMPTY
				&& !seedInventory.canPlant(snapshot.getPatch().getType())))
			{
				return Optional.of(snapshot);
			}
		}

		return Optional.empty();
	}
}
