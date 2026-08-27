package com.farmrunhelper;

import java.lang.reflect.Modifier;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FarmingContractPreplantTest
{
	@Test
	public void exposesOnlyMediumAndHardContractTierChoices()
	{
		assertEquals(2, FarmingContractTier.values().length);
		assertTrue(Modifier.isPublic(FarmingContractTier.class.getModifiers()));
	}

	@Test
	public void requiresTheExplicitDeveloperProperty()
	{
		String previous = System.getProperty(FarmingContractPreplant.DEVELOPMENT_PROPERTY);
		try
		{
			System.clearProperty(FarmingContractPreplant.DEVELOPMENT_PROPERTY);
			assertTrue(!FarmingContractPreplant.isDevelopmentEnabled());
			System.setProperty(FarmingContractPreplant.DEVELOPMENT_PROPERTY, "true");
			assertTrue(FarmingContractPreplant.isDevelopmentEnabled());
		}
		finally
		{
			if (previous == null)
			{
				System.clearProperty(FarmingContractPreplant.DEVELOPMENT_PROPERTY);
			}
			else
			{
				System.setProperty(FarmingContractPreplant.DEVELOPMENT_PROPERTY, previous);
			}
		}
	}

	@Test
	public void hardPlanUsesEveryGuildPatchAndHonoursTreeAndHerbChoices()
	{
		FarmRunHelperConfig config = new FarmRunHelperConfig()
		{
			@Override public FarmingContractTier farmingContractTier() { return FarmingContractTier.HARD; }
			@Override public FarmingContractOptions.HardHerb hardContractHerb() { return FarmingContractOptions.HardHerb.SNAPDRAGON; }
			@Override public FarmingContractOptions.HardTree hardContractTree() { return FarmingContractOptions.HardTree.YEW; }
		};

		List<FarmingContractPreplant.Crop> crops = FarmingContractPreplant.crops(config);
		assertEquals(8, crops.size());
		assertEquals("Snapdragon", crops.get(0).getDisplayName());
		assertEquals("Yew tree", crops.get(1).getDisplayName());
		assertTrue(crops.get(1).getItemId() > 0);
		assertEquals(FarmingGuildContractPatch.ALLOTMENT_NORTH, crops.get(6).getPatch());
		assertTrue(crops.get(6).isAllotment());
		assertEquals(FarmingGuildContractPatch.ALLOTMENT_SOUTH, crops.get(7).getPatch());
	}

	@Test
	public void mediumPlanUsesItsOwnAllotmentAndBushChoices()
	{
		FarmRunHelperConfig config = new FarmRunHelperConfig()
		{
			@Override public FarmingContractTier farmingContractTier() { return FarmingContractTier.MEDIUM; }
			@Override public FarmingContractOptions.MediumBush mediumContractBush() { return FarmingContractOptions.MediumBush.JANGERBERRIES; }
		};

		List<FarmingContractPreplant.Crop> crops = FarmingContractPreplant.crops(config);
		assertEquals(7, crops.size());
		assertEquals("Irit", crops.get(0).getDisplayName());
		assertEquals("Jangerberries", crops.get(2).getDisplayName());
		assertTrue(crops.get(2).getItemId() > 0);
		assertEquals("Strawberries", crops.get(6).getDisplayName());
	}

	@Test
	public void onlyEmptyContractPatchesRequestAPreplantIcon()
	{
		assertTrue(FarmingGuildContractPatch.ALLOTMENT_NORTH.isEmptyValue(0));
		assertTrue(FarmingGuildContractPatch.ALLOTMENT_NORTH.isEmptyValue(252));
		assertTrue(!FarmingGuildContractPatch.ALLOTMENT_NORTH.isEmptyValue(52));
		assertTrue(FarmingGuildContractPatch.CACTUS.isEmptyValue(0));
		assertTrue(!FarmingGuildContractPatch.CACTUS.isEmptyValue(39));
		assertTrue(FarmingGuildContractPatch.BUSH.isEmptyValue(64));
		assertTrue(!FarmingGuildContractPatch.BUSH.isEmptyValue(205));
	}

	@Test
	public void anchorsAllotmentIconsInsideTheConcaveCornerOfAnLShapedClickbox()
	{
		Path2D patch = new Path2D.Double();
		patch.moveTo(0, 0);
		patch.lineTo(100, 0);
		patch.lineTo(100, 40);
		patch.lineTo(40, 40);
		patch.lineTo(40, 100);
		patch.lineTo(0, 100);
		patch.closePath();

		Point2D anchor = CompostReminderOverlay.allotmentIconCenter(patch, 40);
		assertEquals(40.0, anchor.getX(), 0.01);
		assertEquals(40.0, anchor.getY(), 0.01);
	}
}
