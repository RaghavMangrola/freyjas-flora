package com.farmrunhelper;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PatchTypeOrderTest
{
	@Test
	public void defaultOrderMatchesTheExistingFarmRunOrder()
	{
		assertEquals(
			Arrays.asList(
				PatchType.HERB,
				PatchType.TREE,
				PatchType.FRUIT_TREE,
				PatchType.HARDWOOD_TREE,
				PatchType.CALQUAT,
				PatchType.CORAL,
				PatchType.SEAWEED),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED));
	}

	@Test
	public void normalizesMalformedDuplicateAndMissingValues()
	{
		assertEquals(
			Arrays.asList(
				PatchType.SEAWEED,
				PatchType.HERB,
				PatchType.TREE,
				PatchType.FRUIT_TREE,
				PatchType.HARDWOOD_TREE,
				PatchType.CALQUAT,
				PatchType.CORAL),
			PatchTypeOrder.parse("seaweed,HERB,seaweed,retired,,TREE"));
	}

	@Test
	public void serializesOnlyOneOfEveryPatchType()
	{
		assertEquals(
			"CORAL,HERB,TREE,FRUIT_TREE,HARDWOOD_TREE,CALQUAT,SEAWEED",
			PatchTypeOrder.serialize(Arrays.asList(
				PatchType.CORAL,
				PatchType.CORAL,
				null,
				PatchType.HERB)));
	}

	@Test
	public void ordersFamiliesWithoutChangingTheirInternalPatchOrder()
	{
		List<FarmPatch> patches = PatchTypeOrder.orderedPatches("SEAWEED,HERB");

		assertEquals(FarmPatch.SEAWEED_NORTH, patches.get(0));
		assertEquals(FarmPatch.SEAWEED_SOUTH, patches.get(1));
		assertEquals(FarmPatch.ARDOUGNE, patches.get(2));
		assertEquals(FarmPatch.CATHERBY, patches.get(3));
		assertEquals(FarmPatch.WEISS, patches.get(11));
		assertEquals(FarmPatch.TREE_AUBURNVALE, patches.get(12));
		assertEquals(FarmPatch.values().length, patches.size());
	}

	@Test
	public void preservesDisabledFamiliesInTheSavedOrderButOmitsThemFromRuns()
	{
		FarmRunHelperConfig config = new FarmRunHelperConfig()
		{
			@Override
			public boolean showSeaweed()
			{
				return false;
			}

			@Override
			public boolean enableArdougne()
			{
				return false;
			}
		};

		String serialized = "SEAWEED,HERB,TREE";
		List<FarmPatch> patches = PatchTypeOrder.orderedEnabledPatches(serialized, config);

		assertEquals(PatchType.SEAWEED, PatchTypeOrder.parse(serialized).get(0));
		assertEquals(FarmPatch.CATHERBY, patches.get(0));
		assertFalse(patches.contains(FarmPatch.SEAWEED_NORTH));
		assertFalse(patches.contains(FarmPatch.SEAWEED_SOUTH));
		assertFalse(patches.contains(FarmPatch.ARDOUGNE));
	}
}
