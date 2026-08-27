package com.farmrunhelper;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.gameval.ItemID;

/** Resolves the selected guide plan into one crop for each Farming Guild patch. */
final class FarmingContractPreplant
{
	static final String DEVELOPMENT_PROPERTY = "freyjas.flora.farmingContracts";
	private static final Crop IRIT = tracked("Irit", FarmingGuildContractPatch.HERB, FarmPatch.FARMING_GUILD, ItemID.UNIDENTIFIED_IRIT);
	private static final Crop CADANTINE = tracked("Cadantine", FarmingGuildContractPatch.HERB, FarmPatch.FARMING_GUILD, ItemID.UNIDENTIFIED_CADANTINE);
	private static final Crop LANTADYME = tracked("Lantadyme", FarmingGuildContractPatch.HERB, FarmPatch.FARMING_GUILD, ItemID.UNIDENTIFIED_LANTADYME);
	private static final Crop SNAPDRAGON = tracked("Snapdragon", FarmingGuildContractPatch.HERB, FarmPatch.FARMING_GUILD, ItemID.UNIDENTIFIED_SNAPDRAGON);
	private static final Crop MAPLE = tracked("Maple tree", FarmingGuildContractPatch.TREE, FarmPatch.TREE_FARMING_GUILD, ItemID.MAPLE_LOGS);
	private static final Crop YEW = tracked("Yew tree", FarmingGuildContractPatch.TREE, FarmPatch.TREE_FARMING_GUILD, ItemID.YEW_LOGS);
	private static final Crop PALM = tracked("Palm tree", FarmingGuildContractPatch.FRUIT_TREE, FarmPatch.FRUIT_TREE_FARMING_GUILD, ItemID.COCONUT);
	private static final Crop WHITEBERRIES = raw("Whiteberries", FarmingGuildContractPatch.BUSH, ItemID.WHITE_BERRIES);
	private static final Crop JANGERBERRIES = raw("Jangerberries", FarmingGuildContractPatch.BUSH, ItemID.JANGERBERRIES);
	private static final Crop POISON_IVY = raw("Poison ivy", FarmingGuildContractPatch.BUSH, ItemID.POISONIVY_BERRIES);
	private static final Crop CACTUS = raw("Cactus", FarmingGuildContractPatch.CACTUS, ItemID.CACTUS_SPINE);
	private static final Crop POTATO_CACTUS = raw("Potato cactus", FarmingGuildContractPatch.CACTUS, ItemID.CACTUS_POTATO);
	private static final Crop WHITE_LILY = raw("White lily", FarmingGuildContractPatch.FLOWER, ItemID.WHITELILLY);
	private static final Crop WATERMELON = raw("Watermelon", FarmingGuildContractPatch.ALLOTMENT_NORTH, ItemID.WATERMELON);
	private static final Crop STRAWBERRIES = raw("Strawberries", FarmingGuildContractPatch.ALLOTMENT_SOUTH, ItemID.STRAWBERRY);
	private static final Crop SNAPE_GRASS = raw("Snape grass", FarmingGuildContractPatch.ALLOTMENT_SOUTH, ItemID.SNAPE_GRASS);

	private FarmingContractPreplant()
	{
	}

	static boolean isDevelopmentEnabled()
	{
		return Boolean.getBoolean(DEVELOPMENT_PROPERTY);
	}

	static List<Crop> crops(FarmRunHelperConfig config)
	{
		if (config.farmingContractTier() == FarmingContractTier.MEDIUM)
		{
			return Arrays.asList(
				IRIT,
				MAPLE,
				mediumBush(config.mediumContractBush()),
				CACTUS,
				WHITE_LILY,
				WATERMELON,
				STRAWBERRIES);
		}

		return Arrays.asList(
			hardHerb(config.hardContractHerb()),
			hardTree(config.hardContractTree()),
			PALM,
			hardBush(config.hardContractBush()),
			POTATO_CACTUS,
			WHITE_LILY,
			WATERMELON,
			SNAPE_GRASS);
	}

	private static Crop hardHerb(FarmingContractOptions.HardHerb herb)
	{
		switch (herb)
		{
			case LANTADYME: return LANTADYME;
			case SNAPDRAGON: return SNAPDRAGON;
			case CADANTINE:
			default: return CADANTINE;
		}
	}

	private static Crop hardTree(FarmingContractOptions.HardTree tree)
	{
		return tree == FarmingContractOptions.HardTree.YEW ? YEW : MAPLE;
	}

	private static Crop hardBush(FarmingContractOptions.HardBush bush)
	{
		return bush == FarmingContractOptions.HardBush.WHITEBERRIES ? WHITEBERRIES : POISON_IVY;
	}

	private static Crop mediumBush(FarmingContractOptions.MediumBush bush)
	{
		return bush == FarmingContractOptions.MediumBush.JANGERBERRIES ? JANGERBERRIES : WHITEBERRIES;
	}

	private static Crop tracked(String name, FarmingGuildContractPatch patch, FarmPatch trackedPatch, int itemId)
	{
		return new Crop(name, patch, trackedPatch, itemId);
	}

	private static Crop raw(String name, FarmingGuildContractPatch patch, int itemId)
	{
		return new Crop(name, patch, null, itemId);
	}

	static final class Crop
	{
		private final String displayName;
		private final FarmingGuildContractPatch patch;
		private final FarmPatch trackedPatch;
		private final int itemId;

		private Crop(String displayName, FarmingGuildContractPatch patch, FarmPatch trackedPatch, int itemId)
		{
			this.displayName = displayName;
			this.patch = patch;
			this.trackedPatch = trackedPatch;
			this.itemId = itemId;
		}

		String getDisplayName()
		{
			return displayName;
		}

		FarmingGuildContractPatch getPatch()
		{
			return patch;
		}

		int getItemId()
		{
			return itemId;
		}

		boolean isAllotment()
		{
			return patch == FarmingGuildContractPatch.ALLOTMENT_NORTH
				|| patch == FarmingGuildContractPatch.ALLOTMENT_SOUTH;
		}

		boolean needsPreplant(TimeTrackingService timeTrackingService, long now)
		{
			if (trackedPatch != null)
			{
				PatchPrediction prediction = timeTrackingService.predict(trackedPatch);
				return prediction.getEffectiveState(now) == PatchState.EMPTY;
			}

			Integer observedValue = timeTrackingService.getObservedVarbitValue(patch);
			return observedValue == null || patch.isEmptyValue(observedValue);
		}
	}
}
