package com.farmrunhelper;

final class PatchSelection
{
	private PatchSelection()
	{
	}

	static boolean isEnabled(FarmRunHelperConfig config, FarmPatch patch)
	{
		if (!isTypeEnabled(config, patch.getType()))
		{
			return false;
		}

		switch (patch)
		{
			case ARDOUGNE: return config.enableArdougne();
			case CATHERBY: return config.enableCatherby();
			case CIVITAS_ILLA_FORTIS: return config.enableCivitas();
			case FALADOR: return config.enableFalador();
			case FARMING_GUILD: return config.enableFarmingGuild();
			case HARMONY: return config.enableHarmony();
			case HOSIDIUS: return config.enableHosidius();
			case MORYTANIA: return config.enableMorytania();
			case TROLL_STRONGHOLD: return config.enableTrollStronghold();
			case WEISS: return config.enableWeiss();
			case HOPS_ALDARIN: return config.enableAldarinHops();
			case HOPS_ENTRANA: return config.enableEntranaHops();
			case HOPS_LUMBRIDGE: return config.enableLumbridgeHops();
			case HOPS_SEERS_VILLAGE: return config.enableSeersVillageHops();
			case HOPS_YANILLE: return config.enableYanilleHops();
			case TREE_AUBURNVALE: return config.enableAuburnvaleTree();
			case TREE_FALADOR: return config.enableFaladorTree();
			case TREE_FARMING_GUILD: return config.enableFarmingGuildTree();
			case TREE_GNOME_STRONGHOLD: return config.enableGnomeStrongholdTree();
			case TREE_LUMBRIDGE: return config.enableLumbridgeTree();
			case TREE_TAVERLEY: return config.enableTaverleyTree();
			case TREE_VARROCK: return config.enableVarrockTree();
			case FRUIT_TREE_BRIMHAVEN: return config.enableBrimhavenFruitTree();
			case FRUIT_TREE_CATHERBY: return config.enableCatherbyFruitTree();
			case FRUIT_TREE_FARMING_GUILD: return config.enableFarmingGuildFruitTree();
			case FRUIT_TREE_GNOME_STRONGHOLD: return config.enableGnomeStrongholdFruitTree();
			case FRUIT_TREE_KASTORI: return config.enableKastoriFruitTree();
			case FRUIT_TREE_LLETYA: return config.enableLletyaFruitTree();
			case FRUIT_TREE_GNOME_VILLAGE: return config.enableGnomeVillageFruitTree();
			case HARDWOOD_ANGLERS_RETREAT: return config.enableAnglersRetreatHardwood();
			case HARDWOOD_AVIUM_SAVANNAH: return config.enableAviumSavannahHardwood();
			case HARDWOOD_FOSSIL_ISLAND_EAST: return config.enableFossilIslandEastHardwood();
			case HARDWOOD_FOSSIL_ISLAND_MIDDLE: return config.enableFossilIslandMiddleHardwood();
			case HARDWOOD_FOSSIL_ISLAND_WEST: return config.enableFossilIslandWestHardwood();
			case CALQUAT_GREAT_CONCH: return config.enableGreatConchCalquat();
			case CALQUAT_KASTORI: return config.enableKastoriCalquat();
			case CALQUAT_TAI_BWO_WANNAI: return config.enableTaiBwoWannaiCalquat();
			case CORAL_EAST: return config.enableCoralEast();
			case CORAL_WEST: return config.enableCoralWest();
			case SEAWEED_NORTH: return config.enableSeaweedNorth();
			case SEAWEED_SOUTH: return config.enableSeaweedSouth();
			case BIRD_HOUSE_VALLEY_NORTH: return config.enableValleyNorthBirdHouse();
			case BIRD_HOUSE_VALLEY_SOUTH: return config.enableValleySouthBirdHouse();
			case BIRD_HOUSE_MEADOW_NORTH: return config.enableMeadowNorthBirdHouse();
			case BIRD_HOUSE_MEADOW_SOUTH: return config.enableMeadowSouthBirdHouse();
			default: return false;
		}
	}

	private static boolean isTypeEnabled(FarmRunHelperConfig config, PatchType type)
	{
		switch (type)
		{
			case HERB: return config.showHerbs();
			case HOPS: return config.showHops();
			case TREE: return config.showTrees();
			case FRUIT_TREE: return config.showFruitTrees();
			case HARDWOOD_TREE: return config.showHardwoodTrees();
			case CALQUAT: return config.showCalquatTrees();
			case CORAL: return config.showCoral();
			case SEAWEED: return config.showSeaweed();
			case BIRD_HOUSE: return config.showBirdHouses();
			default: return false;
		}
	}
}
