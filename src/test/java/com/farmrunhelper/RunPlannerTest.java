package com.farmrunhelper;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RunPlannerTest
{
	private final RunPlanner planner = new RunPlanner();

	@Test
	public void skipsGrowingPatchesAndWrapsAfterLastRoute()
	{
		long now = 1000L;
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.ARDOUGNE, PatchState.GROWING, 2000L),
			snapshot(FarmPatch.CATHERBY, PatchState.READY, 0L),
			snapshot(FarmPatch.CIVITAS_ILLA_FORTIS, PatchState.READY, 0L));

		assertEquals(
			FarmPatch.CIVITAS_ILLA_FORTIS,
			planner.next(snapshots, FarmPatch.CATHERBY, now, true, true, SeedInventory.empty(), false).get().getPatch());
		assertEquals(
			FarmPatch.CATHERBY,
			planner.next(snapshots, FarmPatch.CIVITAS_ILLA_FORTIS, now, true, true, SeedInventory.empty(), false).get().getPatch());
	}

	@Test
	public void treatsElapsedGrowthAsReady()
	{
		PatchSnapshot elapsed = snapshot(FarmPatch.ARDOUGNE, PatchState.GROWING, 900L);
		assertEquals(
			FarmPatch.ARDOUGNE,
			planner.next(Arrays.asList(elapsed), null, 1000L, false, false, SeedInventory.empty(), false).get().getPatch());
	}

	@Test
	public void honorsEmptyAndUnknownPreferences()
	{
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.ARDOUGNE, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.CATHERBY, PatchState.UNKNOWN, 0L));

		assertFalse(planner.next(snapshots, null, 1000L, false, false, SeedInventory.empty(), false).isPresent());
		assertEquals(
			FarmPatch.ARDOUGNE,
			planner.next(snapshots, null, 1000L, true, false, SeedInventory.empty(), false).get().getPatch());
		assertEquals(
			FarmPatch.CATHERBY,
			planner.next(snapshots, null, 1000L, false, true, SeedInventory.empty(), false).get().getPatch());
	}

	@Test
	public void canSkipEmptyPatchesWhenTheirPlantingItemsAreNotCarried()
	{
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.ARDOUGNE, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.TREE_AUBURNVALE, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.FRUIT_TREE_BRIMHAVEN, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.HARDWOOD_ANGLERS_RETREAT, PatchState.EMPTY, 0L),
			 snapshot(FarmPatch.CALQUAT_GREAT_CONCH, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.SEAWEED_NORTH, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.HOPS_ALDARIN, PatchState.EMPTY, 0L),
			snapshot(FarmPatch.CORAL_EAST, PatchState.EMPTY, 0L));

		assertEquals(
			FarmPatch.CORAL_EAST,
			planner.next(snapshots, null, 1000L, true, false, SeedInventory.empty(), true).get().getPatch());
	}

	@Test
	public void doesNotTreatAnEmptyBirdHouseAsASeededFarmingPatch()
	{
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.BIRD_HOUSE_VALLEY_NORTH, PatchState.EMPTY, 0L));

		assertEquals(
			FarmPatch.BIRD_HOUSE_VALLEY_NORTH,
			planner.next(snapshots, null, 1000L, true, false, SeedInventory.empty(), true).get().getPatch());
	}

	@Test
	public void savedBankSeedsDoNotPretendTheyAreAlreadyCarried()
	{
		SeedInventory savedSeeds = SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, 1L)
			.markObserved(SeedInventory.Location.BANK, 2L)
			.add(SeedInventory.Location.BANK, PatchType.HERB, true, "Ranarr seed", 100)
			.build();
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.ARDOUGNE, PatchState.EMPTY, 0L));

		assertFalse(planner.next(
			snapshots,
			null,
			1000L,
			true,
			false,
			savedSeeds,
			true).isPresent());
	}

	@Test
	public void followsTheSuppliedCustomPatchOrder()
	{
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.SEAWEED_NORTH, PatchState.READY, 0L),
			snapshot(FarmPatch.TREE_AUBURNVALE, PatchState.READY, 0L),
			snapshot(FarmPatch.ARDOUGNE, PatchState.READY, 0L));

		assertEquals(
			FarmPatch.SEAWEED_NORTH,
			planner.next(snapshots, null, 1000L, true, true, SeedInventory.empty(), false).get().getPatch());
		assertEquals(
			FarmPatch.TREE_AUBURNVALE,
			planner.next(snapshots, FarmPatch.SEAWEED_NORTH, 1000L, true, true, SeedInventory.empty(), false).get().getPatch());
	}

	@Test
	public void startsAtTheBeginningWhenThePreviousPatchIsNoLongerVisible()
	{
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.CATHERBY, PatchState.READY, 0L),
			snapshot(FarmPatch.FALADOR, PatchState.READY, 0L));

		assertEquals(
			FarmPatch.CATHERBY,
			planner.next(snapshots, FarmPatch.SEAWEED_NORTH, 1000L, true, true, SeedInventory.empty(), false).get().getPatch());
	}

	private static PatchSnapshot snapshot(FarmPatch patch, PatchState state, long readyAt)
	{
		return new PatchSnapshot(patch, PatchPrediction.known("Crop", state, 500L, readyAt));
	}
}
