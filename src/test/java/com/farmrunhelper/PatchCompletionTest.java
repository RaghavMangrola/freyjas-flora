package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PatchCompletionTest
{
	@Test
	public void detectsReplantingFromEveryActionablePatchState()
	{
		assertTrue(PatchCompletion.wasReplanted(PatchState.READY, PatchState.GROWING));
		assertTrue(PatchCompletion.wasReplanted(PatchState.EMPTY, PatchState.GROWING));
		assertTrue(PatchCompletion.wasReplanted(PatchState.DEAD, PatchState.GROWING));
		assertTrue(PatchCompletion.wasReplanted(PatchState.DISEASED, PatchState.GROWING));
	}

	@Test
	public void ignoresGrowthTicksAndUnobservedPatches()
	{
		assertFalse(PatchCompletion.wasReplanted(PatchState.GROWING, PatchState.GROWING));
		assertFalse(PatchCompletion.wasReplanted(PatchState.UNKNOWN, PatchState.GROWING));
		assertFalse(PatchCompletion.wasReplanted(PatchState.READY, PatchState.EMPTY));
	}
}
