package com.farmrunhelper;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FarmRunProgressTest
{
	@Test
	public void startsAndAdvancesInTheSuppliedPatchOrder()
	{
		FarmRunProgress progress = new FarmRunProgress();
		List<PatchSnapshot> ordered = Arrays.asList(
			snapshot(FarmPatch.SEAWEED_NORTH),
			snapshot(FarmPatch.ARDOUGNE),
			snapshot(FarmPatch.TREE_AUBURNVALE));

		assertEquals(FarmPatch.SEAWEED_NORTH, progress.start(ordered));
		assertEquals(FarmPatch.ARDOUGNE, progress.advance(ordered));
		assertTrue(progress.getDonePatches().contains(FarmPatch.SEAWEED_NORTH));
		assertEquals(FarmPatch.TREE_AUBURNVALE, progress.advance(ordered));
		assertTrue(progress.getDonePatches().contains(FarmPatch.ARDOUGNE));
	}

	@Test
	public void uncompletingADonePatchDoesNotRequireRestartingTheRun()
	{
		FarmRunProgress progress = new FarmRunProgress();
		List<PatchSnapshot> ordered = Arrays.asList(snapshot(FarmPatch.ARDOUGNE));

		progress.start(ordered);
		assertEquals(null, progress.advance(ordered));
		assertTrue(progress.getDonePatches().contains(FarmPatch.ARDOUGNE));

		assertTrue(progress.toggleDone(FarmPatch.ARDOUGNE, ordered));
		assertFalse(progress.getDonePatches().contains(FarmPatch.ARDOUGNE));
		assertEquals(FarmPatch.ARDOUGNE, progress.getCurrentPatch());
	}

	private static PatchSnapshot snapshot(FarmPatch patch)
	{
		return new PatchSnapshot(patch, PatchPrediction.known("Crop", PatchState.GROWING, 500L, 5000L));
	}
}
