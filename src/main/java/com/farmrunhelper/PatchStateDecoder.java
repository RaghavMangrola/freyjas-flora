/*
 * Farming varbit mappings are derived from RuneLite's Time Tracking plugin.
 * RuneLite copyright (c) 2018 Abex and contributors, BSD 2-Clause licensed.
 */
package com.farmrunhelper;

final class PatchStateDecoder
{
	private static final String[] DISEASED_HERBS = {
		"Guam", "Marrentill", "Tarromin", "Harralander", "Ranarr", "Toadflax", "Irit",
		"Avantoe", "Kwuarm", "Snapdragon", "Cadantine", "Lantadyme", "Dwarf weed", "Torstol"
	};

	private static final HerbRange[] HERB_RANGES = {
		new HerbRange("Guam", 4, 8),
		new HerbRange("Marrentill", 11, 15),
		new HerbRange("Tarromin", 18, 22),
		new HerbRange("Harralander", 25, 29),
		new HerbRange("Ranarr", 32, 36),
		new HerbRange("Toadflax", 39, 43),
		new HerbRange("Irit", 46, 50),
		new HerbRange("Avantoe", 53, 57),
		new HerbRange("Huasca", 60, 64),
		new HerbRange("Kwuarm", 68, 72),
		new HerbRange("Snapdragon", 75, 79),
		new HerbRange("Cadantine", 82, 86),
		new HerbRange("Lantadyme", 89, 93),
		new HerbRange("Dwarf weed", 96, 100),
		new HerbRange("Torstol", 103, 107)
	};

	private static final int[] TREE_EMPTY_RANGES = {
		0, 7, 63, 72, 78, 79, 87, 88, 98, 99, 111, 112, 126, 136,
		142, 143, 151, 152, 162, 163, 175, 176, 190, 191, 198, 255
	};
	private static final WoodyCrop[] TREE_CROPS = {
		new WoodyCrop("Oak", 8, 11, 12, 5, 40, 1, 0,
			new int[]{13, 14}, new int[]{73, 75, 77, 77}, new int[]{137, 139, 141, 141}),
		new WoodyCrop("Willow", 15, 20, 21, 7, 40, 1, 0,
			new int[]{22, 23, 192, 197}, new int[]{80, 84, 86, 86}, new int[]{144, 148, 150, 150}),
		new WoodyCrop("Maple", 24, 31, 32, 9, 40, 1, 0,
			new int[]{33, 34}, new int[]{89, 95, 97, 97}, new int[]{153, 159, 161, 161}),
		new WoodyCrop("Yew", 35, 44, 45, 11, 40, 1, 0,
			new int[]{46, 47}, new int[]{100, 108, 110, 110}, new int[]{164, 172, 174, 174}),
		new WoodyCrop("Magic", 48, 59, 60, 13, 40, 1, 0,
			new int[]{61, 62}, new int[]{113, 123, 125, 125}, new int[]{177, 187, 189, 189})
	};

	private static final int[] FRUIT_TREE_EMPTY_RANGES = {0, 7, 62, 71, 126, 135, 190, 199, 254, 255};
	private static final WoodyCrop[] FRUIT_TREE_CROPS = {
		new WoodyCrop("Apple", 8, 13, 34, 7, 160, 7, 45,
			new int[]{14, 20, 33, 33}, new int[]{21, 26}, new int[]{27, 32}),
		new WoodyCrop("Banana", 35, 40, 61, 7, 160, 7, 45,
			new int[]{41, 47, 60, 60}, new int[]{48, 53}, new int[]{54, 59}),
		new WoodyCrop("Orange", 72, 77, 98, 7, 160, 7, 45,
			new int[]{78, 84, 97, 97}, new int[]{85, 90}, new int[]{91, 96}),
		new WoodyCrop("Curry", 99, 104, 125, 7, 160, 7, 45,
			new int[]{105, 111, 124, 124}, new int[]{112, 117}, new int[]{118, 123}),
		new WoodyCrop("Pineapple", 136, 141, 162, 7, 160, 7, 45,
			new int[]{142, 148, 161, 161}, new int[]{149, 154}, new int[]{155, 160}),
		new WoodyCrop("Papaya", 163, 168, 189, 7, 160, 7, 45,
			new int[]{169, 175, 188, 188}, new int[]{176, 181}, new int[]{182, 187}),
		new WoodyCrop("Palm", 200, 205, 226, 7, 160, 7, 45,
			new int[]{206, 212, 225, 225}, new int[]{213, 218}, new int[]{219, 224}),
		new WoodyCrop("Dragonfruit", 227, 232, 253, 7, 160, 7, 45,
			new int[]{233, 239, 252, 252}, new int[]{240, 245}, new int[]{246, 251})
	};

	private static final int[] HARDWOOD_EMPTY_RANGES = {0, 7, 133, 255};
	private static final WoodyCrop[] HARDWOOD_CROPS = {
		new WoodyCrop("Teak", 8, 14, 15, 8, 640, 1, 0,
			new int[]{16, 17}, new int[]{18, 23}, new int[]{24, 29}),
		new WoodyCrop("Mahogany", 30, 37, 38, 9, 640, 1, 0,
			new int[]{39, 40}, new int[]{41, 47}, new int[]{48, 54}),
		new WoodyCrop("Camphor", 55, 62, 63, 9, 640, 1, 0,
			new int[]{64, 65}, new int[]{66, 72}, new int[]{73, 79}),
		new WoodyCrop("Ironwood", 80, 87, 88, 9, 640, 1, 0,
			new int[]{89, 90}, new int[]{91, 97}, new int[]{98, 104}),
		new WoodyCrop("Rosewood", 105, 113, 114, 10, 640, 1, 0,
			new int[]{115, 116}, new int[]{117, 124}, new int[]{125, 132})
	};

	private static final int[] CALQUAT_EMPTY_RANGES = {0, 3, 35, 255};
	private static final WoodyCrop[] CALQUAT_CROPS = {
		new WoodyCrop("Calquat", 4, 11, 34, 9, 160, 7, 0,
			new int[]{12, 18}, new int[]{19, 25}, new int[]{26, 33})
	};

	private PatchStateDecoder()
	{
	}

	static DecodedPatchState decode(PatchType type, int value)
	{
			switch (type)
		{
			case HERB:
				return decodeHerb(value);
			case TREE:
				return decodeWoody(value, TREE_EMPTY_RANGES, TREE_CROPS);
			case FRUIT_TREE:
				return decodeWoody(value, FRUIT_TREE_EMPTY_RANGES, FRUIT_TREE_CROPS);
			case HARDWOOD_TREE:
				return decodeWoody(value, HARDWOOD_EMPTY_RANGES, HARDWOOD_CROPS);
			case CALQUAT:
				return decodeWoody(value, CALQUAT_EMPTY_RANGES, CALQUAT_CROPS);
			case CORAL:
				return decodeCoral(value);
			case SEAWEED:
				return decodeSeaweed(value);
			default:
				return null;
		}
	}

	private static DecodedPatchState decodeHerb(int value)
	{
		if (isHerbPatchEmpty(value))
		{
			return state("Empty", PatchState.EMPTY, 0, 1, 0);
		}

		for (HerbRange herb : HERB_RANGES)
		{
			if (value >= herb.growingStart && value <= herb.growingStart + 3)
			{
				return state(herb.name, PatchState.GROWING, value - herb.growingStart, 5, 20);
			}
			if (value >= herb.harvestStart && value <= herb.harvestStart + 2)
			{
				return state(herb.name, PatchState.READY, 4, 5, 20);
			}
		}

		if (value >= 128 && value <= 169)
		{
			return state(DISEASED_HERBS[(value - 128) / 3], PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 170 && value <= 172)
		{
			return state("Herb", PatchState.DEAD, 0, 0, 0);
		}
		if (value >= 173 && value <= 175)
		{
			return state("Huasca", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 192 && value <= 195)
		{
			return state("Goutweed", PatchState.GROWING, value - 192, 5, 20);
		}
		if (value >= 196 && value <= 197)
		{
			return state("Goutweed", PatchState.READY, 4, 5, 20);
		}
		if (value >= 198 && value <= 200)
		{
			return state("Goutweed", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 201 && value <= 203)
		{
			return state("Goutweed", PatchState.DEAD, 0, 0, 0);
		}

		return null;
	}

	private static DecodedPatchState decodeWoody(int value, int[] emptyRanges, WoodyCrop[] crops)
	{
		if (matchesAnyRange(value, emptyRanges))
		{
			return state("Empty", PatchState.EMPTY, 0, 1, 0);
		}

		for (WoodyCrop crop : crops)
		{
			DecodedPatchState decoded = crop.decode(value);
			if (decoded != null)
			{
				return decoded;
			}
		}
		return null;
	}

	private static boolean matchesAnyRange(int value, int[] ranges)
	{
		for (int i = 0; i < ranges.length; i += 2)
		{
			if (value >= ranges[i] && value <= ranges[i + 1])
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isHerbPatchEmpty(int value)
	{
		return (value >= 0 && value <= 3)
			|| value == 67
			|| (value >= 176 && value <= 191)
			|| (value >= 204 && value <= 219)
			|| (value >= 221 && value <= 255);
	}

	private static DecodedPatchState decodeCoral(int value)
	{
		if ((value >= 0 && value <= 3) || (value >= 37 && value <= 255))
		{
			return state("Empty", PatchState.EMPTY, 0, 1, 0);
		}
		if (value >= 4 && value <= 7)
		{
			return state("Elkhorn", PatchState.GROWING, value - 4, 5, 40);
		}
		if (value == 8)
		{
			return state("Elkhorn", PatchState.READY, 4, 5, 40);
		}
		if (value >= 9 && value <= 11)
		{
			return state("Elkhorn", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 12 && value <= 14)
		{
			return state("Elkhorn", PatchState.DEAD, 0, 0, 0);
		}
		if (value >= 15 && value <= 18)
		{
			return state("Pillar", PatchState.GROWING, value - 15, 5, 40);
		}
		if (value == 19)
		{
			return state("Pillar", PatchState.READY, 4, 5, 40);
		}
		if (value >= 20 && value <= 22)
		{
			return state("Pillar", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 23 && value <= 25)
		{
			return state("Pillar", PatchState.DEAD, 0, 0, 0);
		}
		if (value >= 26 && value <= 29)
		{
			return state("Umbral", PatchState.GROWING, value - 26, 5, 40);
		}
		if (value == 30)
		{
			return state("Umbral", PatchState.READY, 4, 5, 40);
		}
		if (value >= 31 && value <= 33)
		{
			return state("Umbral", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 34 && value <= 36)
		{
			return state("Umbral", PatchState.DEAD, 0, 0, 0);
		}
		return null;
	}

	private static DecodedPatchState decodeSeaweed(int value)
	{
		if ((value >= 0 && value <= 3) || (value >= 17 && value <= 255))
		{
			return state("Empty", PatchState.EMPTY, 0, 1, 0);
		}
		if (value >= 4 && value <= 7)
		{
			return state("Seaweed", PatchState.GROWING, value - 4, 5, 10);
		}
		if (value >= 8 && value <= 10)
		{
			return state("Seaweed", PatchState.READY, 4, 5, 10);
		}
		if (value >= 11 && value <= 13)
		{
			return state("Seaweed", PatchState.DISEASED, 0, 0, 0);
		}
		if (value >= 14 && value <= 16)
		{
			return state("Seaweed", PatchState.DEAD, 0, 0, 0);
		}
		return null;
	}

	private static DecodedPatchState state(
		String cropName,
		PatchState patchState,
		int stage,
		int stages,
		int tickRateMinutes)
	{
		return new DecodedPatchState(cropName, patchState, stage, stages, tickRateMinutes);
	}

	private static final class HerbRange
	{
		private final String name;
		private final int growingStart;
		private final int harvestStart;

		private HerbRange(String name, int growingStart, int harvestStart)
		{
			this.name = name;
			this.growingStart = growingStart;
			this.harvestStart = harvestStart;
		}
	}

	private static final class WoodyCrop
	{
		private final String name;
		private final int growingStart;
		private final int growingEnd;
		private final int healthCheckValue;
		private final int growthStages;
		private final int growthTickMinutes;
		private final int harvestStages;
		private final int regrowTickMinutes;
		private final int[] readyRanges;
		private final int[] diseasedRanges;
		private final int[] deadRanges;

		private WoodyCrop(
			String name,
			int growingStart,
			int growingEnd,
			int healthCheckValue,
			int growthStages,
			int growthTickMinutes,
			int harvestStages,
			int regrowTickMinutes,
			int[] readyRanges,
			int[] diseasedRanges,
			int[] deadRanges)
		{
			this.name = name;
			this.growingStart = growingStart;
			this.growingEnd = growingEnd;
			this.healthCheckValue = healthCheckValue;
			this.growthStages = growthStages;
			this.growthTickMinutes = growthTickMinutes;
			this.harvestStages = harvestStages;
			this.regrowTickMinutes = regrowTickMinutes;
			this.readyRanges = readyRanges;
			this.diseasedRanges = diseasedRanges;
			this.deadRanges = deadRanges;
		}

		private DecodedPatchState decode(int value)
		{
			if (value >= growingStart && value <= growingEnd)
			{
				return state(name, PatchState.GROWING, value - growingStart, growthStages, growthTickMinutes);
			}
			if (value == healthCheckValue)
			{
				return state(name, PatchState.READY, growthStages - 1, growthStages, 0);
			}
			if (matchesAnyRange(value, readyRanges))
			{
				int stage = value >= readyRanges[0] && value <= readyRanges[1]
					? Math.min(harvestStages - 1, value - readyRanges[0])
					: 0;
				return state(name, PatchState.READY, stage, harvestStages, regrowTickMinutes);
			}
			if (matchesAnyRange(value, diseasedRanges))
			{
				return state(name, PatchState.DISEASED, 0, 0, 0);
			}
			if (matchesAnyRange(value, deadRanges))
			{
				return state(name, PatchState.DEAD, 0, 0, 0);
			}
			return null;
		}
	}
}
