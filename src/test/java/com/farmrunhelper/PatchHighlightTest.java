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
	public void onlyShowsCompostReminderForGrowingUncompostedHerbsAndSeaweed()
	{
		assertEquals(PatchHighlight.COMPOST, PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.GROWING, false));
		assertNull(PatchHighlight.forPatch(FarmPatch.ARDOUGNE, PatchState.GROWING, true));
		assertNull(PatchHighlight.forPatch(FarmPatch.CORAL_EAST, PatchState.GROWING, false));
	}
}
