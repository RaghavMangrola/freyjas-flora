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
	public void followsThePatchTypeCompostReminderPolicy()
	{
		assertTrue(CompostReminder.shouldShow(FarmPatch.HOPS_YANILLE, PatchState.GROWING, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.TREE_FALADOR, PatchState.GROWING, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.FRUIT_TREE_CATHERBY, PatchState.GROWING, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST, PatchState.GROWING, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.CALQUAT_TAI_BWO_WANNAI, PatchState.GROWING, false));
		assertTrue(CompostReminder.shouldShow(FarmPatch.SEAWEED_NORTH, PatchState.GROWING, false));
		assertFalse(CompostReminder.shouldShow(FarmPatch.CORAL_EAST, PatchState.GROWING, false));
		assertFalse(CompostReminder.shouldShow(FarmPatch.BIRD_HOUSE_VALLEY_NORTH, PatchState.GROWING, false));
	}
}
