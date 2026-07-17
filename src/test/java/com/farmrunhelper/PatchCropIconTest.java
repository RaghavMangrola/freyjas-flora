package com.farmrunhelper;

import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PatchCropIconTest
{
	@Test
	public void mapsKnownCropsToTheirHarvestedProduct()
	{
		assertEquals(ItemID.UNIDENTIFIED_KWUARM, PatchCropIcon.itemIdFor(PatchType.HERB, "Kwuarm"));
		assertEquals(ItemID.MAGIC_LOGS,
			PatchCropIcon.itemIdFor(PatchType.TREE, "Magic"));
		assertEquals(ItemID.DRAGONFRUIT,
			PatchCropIcon.itemIdFor(PatchType.FRUIT_TREE, "Dragonfruit"));
		assertEquals(ItemID.ROSEWOOD_LOGS,
			PatchCropIcon.itemIdFor(PatchType.HARDWOOD_TREE, "Rosewood"));
		assertEquals(ItemID.CORAL_UMBRAL,
			PatchCropIcon.itemIdFor(PatchType.CORAL, "Umbral"));
		assertEquals(ItemID.GIANT_SEAWEED,
			PatchCropIcon.itemIdFor(PatchType.SEAWEED, "Seaweed"));
	}

	@Test
	public void fallsBackToARecognizableCategoryIconWhenTheCropIsUnknown()
	{
		assertEquals(ItemID.UNIDENTIFIED_GUAM, PatchCropIcon.itemIdFor(PatchType.HERB, "Unknown"));
		assertEquals(ItemID.OAK_LOGS, PatchCropIcon.itemIdFor(PatchType.TREE, "Empty"));
		assertEquals(ItemID.CORAL_ELKHORN, PatchCropIcon.itemIdFor(PatchType.CORAL, "Empty"));
	}
}
