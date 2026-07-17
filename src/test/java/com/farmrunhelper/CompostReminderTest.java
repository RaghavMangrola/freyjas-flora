package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompostReminderTest
{
	@Test
	public void remindsForEmptyAndGrowingHerbsWithoutRecordedCompost()
	{
		assertTrue(CompostReminder.shouldShow(FarmPatch.ARDOUGNE, PatchState.EMPTY, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.ARDOUGNE, PatchState.GROWING, false));
	}

	@Test
	public void suppressesReminderWhenCompostIsRecordedOrCropIsFinished()
	{
		assertFalse(CompostReminder.shouldShow(FarmPatch.ARDOUGNE, PatchState.GROWING, true));
		assertFalse(CompostReminder.shouldShow(FarmPatch.ARDOUGNE, PatchState.READY, false));
		assertFalse(CompostReminder.shouldShow(FarmPatch.ARDOUGNE, PatchState.DISEASED, false));
	}

	@Test
	public void includesSeaweedButNotCoral()
	{
		assertTrue(CompostReminder.shouldShow(FarmPatch.SEAWEED_NORTH, PatchState.GROWING, false));
		assertFalse(CompostReminder.shouldShow(FarmPatch.CORAL_EAST, PatchState.GROWING, false));
	}
}
