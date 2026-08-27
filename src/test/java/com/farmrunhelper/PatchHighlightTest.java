package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatchHighlightTest
{
	@Test
	public void highlightsReadyEmptyDeadAndDiseasedPatches()
	{
		assertEquals(PatchHighlight.READY, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.READY, false));
		assertEquals(PatchHighlight.EMPTY, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.EMPTY, true));
		assertEquals(PatchHighlight.DEAD, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.DEAD, false));
		assertEquals(PatchHighlight.DISEASED, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.DISEASED, false));
	}

	@Test
	public void sceneHighlightPolicyIsExplicitForEveryPatchFamily()
	{
		for (PatchType type : PatchType.values())
		{
			FarmPatch patch = firstPatchOf(type);
			if (type == PatchType.BIRD_HOUSE)
			{
				assertNull(PatchHighlight.forPatch(patch, PatchState.READY, false));
			}
			else
			{
				assertEquals(PatchHighlight.READY, PatchHighlight.forPatch(patch, PatchState.READY, false));
			}
		}
	}

	@Test
	public void showsCompostReminderForGrowingUncompostedHardwoodsLikeHerbs()
	{
		assertEquals(PatchHighlight.COMPOST, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.GROWING, false));
		assertNull(PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.GROWING, true));
		assertEquals(PatchHighlight.COMPOST,
			PatchHighlight.forPatch(FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST, PatchState.GROWING, false));
		assertNull(PatchHighlight.forPatch(FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST, PatchState.GROWING, true));
		assertNull(PatchHighlight.forPatch(FarmPatch.CORAL_EAST, PatchState.GROWING, false));
	}

	private FarmPatch firstPatchOf(PatchType type)
	{
		for (FarmPatch patch : FarmPatch.values())
		{
			if (patch.getType() == type)
			{
				return patch;
			}
		}
		throw new AssertionError("No patch for " + type);
	}
}
