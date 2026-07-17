package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatchStateDecoderTest
{
	@Test
	public void decodesHerbGrowthAndHarvestStates()
	{
		assertDecoded(PatchType.HERB, 32, "Ranarr", PatchState.GROWING, 0, 5, 20);
		assertDecoded(PatchType.HERB, 35, "Ranarr", PatchState.GROWING, 3, 5, 20);
		assertDecoded(PatchType.HERB, 36, "Ranarr", PatchState.READY, 4, 5, 20);
		assertDecoded(PatchType.HERB, 60, "Huasca", PatchState.GROWING, 0, 5, 20);
		assertDecoded(PatchType.HERB, 107, "Torstol", PatchState.READY, 4, 5, 20);
	}

	@Test
	public void decodesHerbAttentionStates()
	{
		assertDecoded(PatchType.HERB, 0, "Empty", PatchState.EMPTY, 0, 1, 0);
		assertDecoded(PatchType.HERB, 140, "Ranarr", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.HERB, 170, "Herb", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.HERB, 173, "Huasca", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.HERB, 192, "Goutweed", PatchState.GROWING, 0, 5, 20);
		assertDecoded(PatchType.HERB, 201, "Goutweed", PatchState.DEAD, 0, 0, 0);
		assertNull(PatchStateDecoder.decode(PatchType.HERB, 220));
	}

	@Test
	public void decodesRegularTreeStatesAndHealthChecks()
	{
		assertDecoded(PatchType.TREE, 0, "Empty", PatchState.EMPTY, 0, 1, 0);
		assertDecoded(PatchType.TREE, 8, "Oak", PatchState.GROWING, 0, 5, 40);
		assertDecoded(PatchType.TREE, 12, "Oak", PatchState.READY, 4, 5, 0);
		assertDecoded(PatchType.TREE, 13, "Oak", PatchState.READY, 0, 1, 0);
		assertDecoded(PatchType.TREE, 73, "Oak", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.TREE, 137, "Oak", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.TREE, 192, "Willow", PatchState.READY, 0, 1, 0);
		assertDecoded(PatchType.TREE, 60, "Magic", PatchState.READY, 12, 13, 0);
		assertNull(PatchStateDecoder.decode(PatchType.TREE, 76));
	}

	@Test
	public void decodesFruitTreeGrowthHarvestAndRegrowthStates()
	{
		assertDecoded(PatchType.FRUIT_TREE, 8, "Apple", PatchState.GROWING, 0, 7, 160);
		assertDecoded(PatchType.FRUIT_TREE, 34, "Apple", PatchState.READY, 6, 7, 0);
		assertDecoded(PatchType.FRUIT_TREE, 14, "Apple", PatchState.READY, 0, 7, 45);
		assertDecoded(PatchType.FRUIT_TREE, 20, "Apple", PatchState.READY, 6, 7, 45);
		assertDecoded(PatchType.FRUIT_TREE, 21, "Apple", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.FRUIT_TREE, 27, "Apple", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.FRUIT_TREE, 227, "Dragonfruit", PatchState.GROWING, 0, 7, 160);
		assertDecoded(PatchType.FRUIT_TREE, 253, "Dragonfruit", PatchState.READY, 6, 7, 0);
		assertDecoded(PatchType.FRUIT_TREE, 254, "Empty", PatchState.EMPTY, 0, 1, 0);
	}

	@Test
	public void decodesHardwoodTreeStates()
	{
		assertDecoded(PatchType.HARDWOOD_TREE, 8, "Teak", PatchState.GROWING, 0, 8, 640);
		assertDecoded(PatchType.HARDWOOD_TREE, 15, "Teak", PatchState.READY, 7, 8, 0);
		assertDecoded(PatchType.HARDWOOD_TREE, 16, "Teak", PatchState.READY, 0, 1, 0);
		assertDecoded(PatchType.HARDWOOD_TREE, 18, "Teak", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.HARDWOOD_TREE, 24, "Teak", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.HARDWOOD_TREE, 105, "Rosewood", PatchState.GROWING, 0, 10, 640);
		assertDecoded(PatchType.HARDWOOD_TREE, 133, "Empty", PatchState.EMPTY, 0, 1, 0);
	}

	@Test
	public void decodesCalquatStates()
	{
		assertDecoded(PatchType.CALQUAT, 0, "Empty", PatchState.EMPTY, 0, 1, 0);
		assertDecoded(PatchType.CALQUAT, 4, "Calquat", PatchState.GROWING, 0, 9, 160);
		assertDecoded(PatchType.CALQUAT, 34, "Calquat", PatchState.READY, 8, 9, 0);
		assertDecoded(PatchType.CALQUAT, 12, "Calquat", PatchState.READY, 0, 7, 0);
		assertDecoded(PatchType.CALQUAT, 19, "Calquat", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.CALQUAT, 26, "Calquat", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.CALQUAT, 35, "Empty", PatchState.EMPTY, 0, 1, 0);
	}

	@Test
	public void decodesCoralStates()
	{
		assertDecoded(PatchType.CORAL, 4, "Elkhorn", PatchState.GROWING, 0, 5, 40);
		assertDecoded(PatchType.CORAL, 8, "Elkhorn", PatchState.READY, 4, 5, 40);
		assertDecoded(PatchType.CORAL, 20, "Pillar", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.CORAL, 30, "Umbral", PatchState.READY, 4, 5, 40);
		assertDecoded(PatchType.CORAL, 36, "Umbral", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.CORAL, 37, "Empty", PatchState.EMPTY, 0, 1, 0);
	}

	@Test
	public void decodesSeaweedStates()
	{
		assertDecoded(PatchType.SEAWEED, 4, "Seaweed", PatchState.GROWING, 0, 5, 10);
		assertDecoded(PatchType.SEAWEED, 8, "Seaweed", PatchState.READY, 4, 5, 10);
		assertDecoded(PatchType.SEAWEED, 11, "Seaweed", PatchState.DISEASED, 0, 0, 0);
		assertDecoded(PatchType.SEAWEED, 14, "Seaweed", PatchState.DEAD, 0, 0, 0);
		assertDecoded(PatchType.SEAWEED, 17, "Empty", PatchState.EMPTY, 0, 1, 0);
	}

	private static void assertDecoded(
		PatchType type,
		int value,
		String crop,
		PatchState state,
		int stage,
		int stages,
		int tickRate)
	{
		DecodedPatchState decoded = PatchStateDecoder.decode(type, value);
		assertEquals(crop, decoded.getCropName());
		assertEquals(state, decoded.getState());
		assertEquals(stage, decoded.getStage());
		assertEquals(stages, decoded.getStages());
		assertEquals(tickRate, decoded.getTickRateMinutes());
	}
}
