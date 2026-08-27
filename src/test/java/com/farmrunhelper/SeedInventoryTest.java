package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SeedInventoryTest
{
	@Test
	public void distinguishesEverySupportedPlantingItemFamily()
	{
		SeedInventory seeds = new SeedInventory(12, 1, 2, 3, 1, 4);
		assertTrue(seeds.canPlant(PatchType.HERB));
		assertTrue(seeds.canPlant(PatchType.TREE));
		assertTrue(seeds.canPlant(PatchType.FRUIT_TREE));
		assertTrue(seeds.canPlant(PatchType.HARDWOOD_TREE));
		assertTrue(seeds.canPlant(PatchType.CALQUAT));
		assertTrue(seeds.canPlant(PatchType.SEAWEED));
		assertTrue(SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, 0L)
			.add(SeedInventory.Location.INVENTORY, PatchType.HOPS, true, "Yanillian seed", 1)
			.build()
			.canPlant(PatchType.HOPS));
	}

	@Test
	public void emptyInventoryCannotPlantAnySeededPatchType()
	{
		SeedInventory seeds = SeedInventory.empty();
		assertFalse(seeds.canPlant(PatchType.HERB));
		assertFalse(seeds.canPlant(PatchType.TREE));
		assertFalse(seeds.canPlant(PatchType.FRUIT_TREE));
		assertFalse(seeds.canPlant(PatchType.HARDWOOD_TREE));
		assertFalse(seeds.canPlant(PatchType.CALQUAT));
		assertFalse(seeds.canPlant(PatchType.SEAWEED));
		assertFalse(seeds.canPlant(PatchType.HOPS));
		assertTrue(seeds.canPlant(PatchType.CORAL));
		assertTrue(seeds.canPlant(PatchType.BIRD_HOUSE));
	}

	@Test
	public void combinesLiveAndSavedLocationsWithoutTreatingStorageAsCarried()
	{
		SeedInventory seeds = SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, 100L)
			.markObserved(SeedInventory.Location.BANK, 200L)
			.markObserved(SeedInventory.Location.SEED_VAULT, 300L)
			.add(SeedInventory.Location.INVENTORY, PatchType.HERB, true, "Ranarr seed", 3)
			.add(SeedInventory.Location.BANK, PatchType.TREE, true, "Magic sapling", 2)
			.add(SeedInventory.Location.SEED_VAULT, PatchType.HERB, true, "Snapdragon seed", 40)
			.add(SeedInventory.Location.SEED_VAULT, PatchType.FRUIT_TREE, false, "Palm tree seed", 5)
			.build();

		assertTrue(seeds.canPlant(PatchType.HERB));
		assertFalse(seeds.canPlant(PatchType.TREE));
		assertEquals("Bank", seeds.getStoredPlantingLocations(PatchType.TREE));
		assertEquals("Seed Vault", seeds.getStoredPlantingLocations(PatchType.HERB));
		assertTrue(seeds.hasSeedToPrepare(PatchType.FRUIT_TREE));
		assertNull(seeds.getStoredPlantingLocations(PatchType.FRUIT_TREE));
		assertEquals(2L, seeds.getTotal(SeedInventory.Location.BANK));
		assertEquals(45L, seeds.getTotal(SeedInventory.Location.SEED_VAULT));
		assertEquals("Magic sapling", seeds.getItems(SeedInventory.Location.BANK).get(0).getName());
	}

	@Test
	public void distinguishesAnUnseenStorageFromAKnownEmptyStorage()
	{
		SeedInventory unseen = SeedInventory.empty();
		assertTrue(unseen.hasUnknownStoredStock());

		SeedInventory knownEmpty = SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, 1L)
			.markObserved(SeedInventory.Location.BANK, 2L)
			.markObserved(SeedInventory.Location.SEED_VAULT, 3L)
			.build();
		assertFalse(knownEmpty.hasUnknownStoredStock());
	}
}
