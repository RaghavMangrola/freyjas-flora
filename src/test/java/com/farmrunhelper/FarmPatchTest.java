package com.farmrunhelper;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FarmPatchTest
{
	@Test
	public void containsEverySupportedPatchFamily()
	{
		int[] counts = new int[PatchType.values().length];
		for (FarmPatch patch : FarmPatch.values())
		{
			counts[patch.getType().ordinal()]++;
		}

		assertEquals(45, FarmPatch.values().length);
		assertEquals(10, counts[PatchType.HERB.ordinal()]);
		assertEquals(5, counts[PatchType.HOPS.ordinal()]);
		assertEquals(7, counts[PatchType.TREE.ordinal()]);
		assertEquals(7, counts[PatchType.FRUIT_TREE.ordinal()]);
		assertEquals(5, counts[PatchType.HARDWOOD_TREE.ordinal()]);
		assertEquals(3, counts[PatchType.CALQUAT.ordinal()]);
		assertEquals(2, counts[PatchType.CORAL.ordinal()]);
		assertEquals(2, counts[PatchType.SEAWEED.ordinal()]);
		assertEquals(4, counts[PatchType.BIRD_HOUSE.ordinal()]);
	}

	@Test
	public void usesTimeTrackingProfileKeysFromFarmingWorld()
	{
		assertEquals("10548.4774", FarmPatch.ARDOUGNE.getTimeTrackingKey());
		assertEquals("4922.4775", FarmPatch.FARMING_GUILD.getTimeTrackingKey());
		assertEquals("5421.4771", FarmPatch.HOPS_ALDARIN.getTimeTrackingKey());
		assertEquals("11060.4771", FarmPatch.HOPS_ENTRANA.getTimeTrackingKey());
		assertEquals("12851.4771", FarmPatch.HOPS_LUMBRIDGE.getTimeTrackingKey());
		assertEquals("10551.4771", FarmPatch.HOPS_SEERS_VILLAGE.getTimeTrackingKey());
		assertEquals("10288.4771", FarmPatch.HOPS_YANILLE.getTimeTrackingKey());
		assertEquals("5427.4771", FarmPatch.TREE_AUBURNVALE.getTimeTrackingKey());
		assertEquals("11828.4771", FarmPatch.TREE_FALADOR.getTimeTrackingKey());
		assertEquals("4922.7905", FarmPatch.TREE_FARMING_GUILD.getTimeTrackingKey());
		assertEquals("9781.4771", FarmPatch.TREE_GNOME_STRONGHOLD.getTimeTrackingKey());
		assertEquals("12594.4771", FarmPatch.TREE_LUMBRIDGE.getTimeTrackingKey());
		assertEquals("11573.4771", FarmPatch.TREE_TAVERLEY.getTimeTrackingKey());
		assertEquals("12854.4771", FarmPatch.TREE_VARROCK.getTimeTrackingKey());
		assertEquals("11058.4771", FarmPatch.FRUIT_TREE_BRIMHAVEN.getTimeTrackingKey());
		assertEquals("11317.4771", FarmPatch.FRUIT_TREE_CATHERBY.getTimeTrackingKey());
		assertEquals("4922.7909", FarmPatch.FRUIT_TREE_FARMING_GUILD.getTimeTrackingKey());
		assertEquals("9781.4772", FarmPatch.FRUIT_TREE_GNOME_STRONGHOLD.getTimeTrackingKey());
		assertEquals("5423.4772", FarmPatch.FRUIT_TREE_KASTORI.getTimeTrackingKey());
		assertEquals("9265.4771", FarmPatch.FRUIT_TREE_LLETYA.getTimeTrackingKey());
		assertEquals("9777.4771", FarmPatch.FRUIT_TREE_GNOME_VILLAGE.getTimeTrackingKey());
		assertEquals("9770.4771", FarmPatch.HARDWOOD_ANGLERS_RETREAT.getTimeTrackingKey());
		assertEquals("6702.4771", FarmPatch.HARDWOOD_AVIUM_SAVANNAH.getTimeTrackingKey());
		assertEquals("14651.4771", FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST.getTimeTrackingKey());
		assertEquals("14651.4772", FarmPatch.HARDWOOD_FOSSIL_ISLAND_MIDDLE.getTimeTrackingKey());
		assertEquals("14651.4773", FarmPatch.HARDWOOD_FOSSIL_ISLAND_WEST.getTimeTrackingKey());
		assertEquals("12581.4773", FarmPatch.CALQUAT_GREAT_CONCH.getTimeTrackingKey());
		assertEquals("5423.4771", FarmPatch.CALQUAT_KASTORI.getTimeTrackingKey());
		assertEquals("11056.4771", FarmPatch.CALQUAT_TAI_BWO_WANNAI.getTimeTrackingKey());
		assertEquals("12581.4771", FarmPatch.CORAL_EAST.getTimeTrackingKey());
		assertEquals("12581.4772", FarmPatch.CORAL_WEST.getTimeTrackingKey());
		assertEquals("15008.4771", FarmPatch.SEAWEED_NORTH.getTimeTrackingKey());
		assertEquals("15008.4772", FarmPatch.SEAWEED_SOUTH.getTimeTrackingKey());
		assertEquals("birdhouse.1628", FarmPatch.BIRD_HOUSE_VALLEY_NORTH.getTimeTrackingKey());
		assertEquals("birdhouse.1629", FarmPatch.BIRD_HOUSE_VALLEY_SOUTH.getTimeTrackingKey());
		assertEquals("birdhouse.1626", FarmPatch.BIRD_HOUSE_MEADOW_NORTH.getTimeTrackingKey());
		assertEquals("birdhouse.1627", FarmPatch.BIRD_HOUSE_MEADOW_SOUTH.getTimeTrackingKey());
	}

	@Test
	public void coralRoutesToEntranceUntilNurseryIsEntered()
	{
		assertEquals(
			new WorldPoint(3272, 2463, 0),
			FarmPatch.CORAL_EAST.getNavigationTarget(new WorldPoint(3178, 2447, 0)));
		assertNotNull(FarmPatch.CORAL_EAST.getAccessInstruction(new WorldPoint(3178, 2447, 0)));

		WorldPoint nursery = new WorldPoint(3296, 8861, 0);
		assertEquals(nursery, FarmPatch.CORAL_EAST.getNavigationTarget(nursery));
		assertNull(FarmPatch.CORAL_EAST.getAccessInstruction(nursery));
	}

	@Test
	public void seaweedRoutesToDivePointUntilUnderwaterIsEntered()
	{
		assertEquals(
			new WorldPoint(3734, 3893, 0),
			FarmPatch.SEAWEED_NORTH.getNavigationTarget(new WorldPoint(3724, 3807, 0)));

		WorldPoint underwater = new WorldPoint(3730, 10271, 0);
		assertEquals(underwater, FarmPatch.SEAWEED_NORTH.getNavigationTarget(underwater));
	}

	@Test
	public void matchesSceneObjectsByVarbitAndActualPatchRegion()
	{
		assertTrue(FarmPatch.ARDOUGNE.matchesSceneObject(
			FarmPatch.ARDOUGNE.getVarbitId(), new WorldPoint(2663, 3375, 0)));
		assertTrue(FarmPatch.SEAWEED_SOUTH.matchesSceneObject(
			FarmPatch.SEAWEED_SOUTH.getVarbitId(), new WorldPoint(3730, 10271, 0)));
		assertTrue(FarmPatch.CORAL_EAST.matchesSceneObject(
			FarmPatch.CORAL_EAST.getVarbitId(), new WorldPoint(3296, 8861, 0)));
		assertTrue(FarmPatch.CALQUAT_GREAT_CONCH.matchesSceneObject(
			FarmPatch.CALQUAT_GREAT_CONCH.getVarbitId(), new WorldPoint(3129, 2406, 0)));
		assertTrue(FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST.matchesSceneObject(
			FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST.getVarbitId(), new WorldPoint(3715, 3835, 0)));
		assertFalse(FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST.matchesSceneObject(
			FarmPatch.HARDWOOD_FOSSIL_ISLAND_MIDDLE.getVarbitId(), new WorldPoint(3715, 3835, 0)));
	}

	@Test
	public void usesDistinctFossilIslandHardwoodTargets()
	{
		assertEquals(new WorldPoint(3715, 3835, 0),
			FarmPatch.HARDWOOD_FOSSIL_ISLAND_EAST.getNavigationTarget(null));
		assertEquals(new WorldPoint(3708, 3833, 0),
			FarmPatch.HARDWOOD_FOSSIL_ISLAND_MIDDLE.getNavigationTarget(null));
		assertEquals(new WorldPoint(3702, 3837, 0),
			FarmPatch.HARDWOOD_FOSSIL_ISLAND_WEST.getNavigationTarget(null));
	}

	@Test
	public void enablesEveryCatalogEntryWithTheDefaultConfiguration()
	{
		FarmRunHelperConfig config = new FarmRunHelperConfig() { };
		for (FarmPatch patch : FarmPatch.values())
		{
			assertTrue(patch.name(), PatchSelection.isEnabled(config, patch));
		}
	}

	@Test
	public void usesTheStandardBirdHouseRunOrderAndTargets()
	{
		assertEquals(new WorldPoint(3768, 3761, 0),
			FarmPatch.BIRD_HOUSE_VALLEY_NORTH.getNavigationTarget(null));
		assertEquals(new WorldPoint(3763, 3755, 0),
			FarmPatch.BIRD_HOUSE_VALLEY_SOUTH.getNavigationTarget(null));
		assertEquals(new WorldPoint(3677, 3882, 0),
			FarmPatch.BIRD_HOUSE_MEADOW_NORTH.getNavigationTarget(null));
		assertEquals(new WorldPoint(3678, 3815, 0),
			FarmPatch.BIRD_HOUSE_MEADOW_SOUTH.getNavigationTarget(null));
		assertFalse(FarmPatch.BIRD_HOUSE_MEADOW_NORTH.matchesSceneObject(
			FarmPatch.BIRD_HOUSE_MEADOW_NORTH.getVarbitId(), new WorldPoint(3677, 3882, 0)));
	}

	@Test
	public void keepsNamedHerbPatchLocationsInTheirTimeTrackingRegions()
	{
		assertEquals(FarmPatch.CIVITAS_ILLA_FORTIS.getTimeTrackingRegionId(),
			new WorldPoint(1587, 3101, 0).getRegionID());
		assertEquals(FarmPatch.HOSIDIUS.getTimeTrackingRegionId(),
			new WorldPoint(1729, 3558, 0).getRegionID());
	}
}
