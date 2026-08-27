package com.farmrunhelper;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(FarmRunHelperConfig.GROUP)
public interface FarmRunHelperConfig extends Config
{
	String GROUP = "farmrunhelper";
	String PATCH_TYPE_ORDER_KEY = "patchTypeOrder";

	@ConfigSection(
		name = "Herbs",
		description = "Choose the herb patches shown, routed, and highlighted",
		position = 3,
		closedByDefault = true
	)
	String HERB_PATCHES = "herbPatches";

	@ConfigSection(
		name = "Hops",
		description = "Choose the hops patches shown, routed, and highlighted",
		position = 4,
		closedByDefault = true
	)
	String HOPS_PATCHES = "hopsPatches";

	@ConfigSection(
		name = "Trees",
		description = "Choose the regular tree patches shown, routed, and highlighted",
		position = 5,
		closedByDefault = true
	)
	String TREE_PATCHES = "treePatches";

	@ConfigSection(
		name = "Fruit trees",
		description = "Choose the fruit tree patches shown, routed, and highlighted",
		position = 6,
		closedByDefault = true
	)
	String FRUIT_TREE_PATCHES = "fruitTreePatches";

	@ConfigSection(
		name = "Hardwood trees",
		description = "Choose the hardwood patches shown, routed, and highlighted",
		position = 7,
		closedByDefault = true
	)
	String HARDWOOD_TREE_PATCHES = "hardwoodTreePatches";

	@ConfigSection(
		name = "Calquat trees",
		description = "Choose the calquat patches shown, routed, and highlighted",
		position = 8,
		closedByDefault = true
	)
	String CALQUAT_PATCHES = "calquatPatches";

	@ConfigSection(
		name = "Coral",
		description = "Choose the Great Conch coral nurseries shown and routed",
		position = 9,
		closedByDefault = true
	)
	String CORAL_PATCHES = "coralPatches";

	@ConfigSection(
		name = "Seaweed",
		description = "Choose the seaweed patches shown, routed, and highlighted",
		position = 10,
		closedByDefault = true
	)
	String SEAWEED_PATCHES = "seaweedPatches";

	@ConfigSection(
		name = "Birdhouses",
		description = "Choose the Fossil Island birdhouses shown and routed",
		position = 11,
		closedByDefault = true
	)
	String BIRD_HOUSE_PATCHES = "birdHousePatches";

	@ConfigSection(
		name = "Run behavior",
		description = "Choose what a run includes and when it advances",
		position = 0
	)
	String RUN_PLANNING = "runPlanning";

	@ConfigSection(
		name = "Scene highlights",
		description = "Colors and visual reminders drawn over farming patches",
		position = 2,
		closedByDefault = true
	)
	String COMPOST_REMINDERS = "compostReminders";

	@ConfigSection(
		name = "Farming contracts",
		description = "Choose a Farming Guild crop to keep ready for a contract",
		position = 13
	)
	String FARMING_CONTRACTS = "farmingContracts";

	@ConfigSection(
		name = "Shortest Path",
		description = "Control how PatchMaster hands destinations to Shortest Path",
		position = 12,
		closedByDefault = true
	)
	String NAVIGATION = "navigation";

	@ConfigItem(
		keyName = "showHerbs",
		name = "Include herbs",
		description = "Include herb patches in the panel, run planner, and scene highlights",
		section = HERB_PATCHES,
		position = 0
	)
	default boolean showHerbs()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHops",
		name = "Include hops",
		description = "Include hops patches in the panel, run planner, and scene highlights",
		section = HOPS_PATCHES,
		position = 0
	)
	default boolean showHops()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTrees",
		name = "Include trees",
		description = "Include regular tree patches in the panel, run planner, and scene highlights",
		section = TREE_PATCHES,
		position = 0
	)
	default boolean showTrees()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFruitTrees",
		name = "Include fruit trees",
		description = "Include fruit tree patches in the panel, run planner, and scene highlights",
		section = FRUIT_TREE_PATCHES,
		position = 0
	)
	default boolean showFruitTrees()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showHardwoodTrees",
		name = "Include hardwood trees",
		description = "Include hardwood patches in the panel, run planner, and scene highlights",
		section = HARDWOOD_TREE_PATCHES,
		position = 0
	)
	default boolean showHardwoodTrees()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCalquatTrees",
		name = "Include calquat trees",
		description = "Include calquat patches in the panel, run planner, and scene highlights",
		section = CALQUAT_PATCHES,
		position = 0
	)
	default boolean showCalquatTrees()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCoral",
		name = "Include coral",
		description = "Include Great Conch coral nurseries in the panel and run planner",
		section = CORAL_PATCHES,
		position = 0
	)
	default boolean showCoral()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showSeaweed",
		name = "Include seaweed",
		description = "Include seaweed patches in the panel, run planner, and scene highlights",
		section = SEAWEED_PATCHES,
		position = 0
	)
	default boolean showSeaweed()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBirdHouses",
		name = "Include birdhouses",
		description = "Include Fossil Island birdhouses in the panel and run planner",
		section = BIRD_HOUSE_PATCHES,
		position = 0
	)
	default boolean showBirdHouses()
	{
		return true;
	}

	@ConfigItem(keyName = "enableArdougne", name = "Ardougne", description = "Include the Ardougne herb patch", section = HERB_PATCHES, position = 1)
	default boolean enableArdougne()
	{
		return true;
	}

	@ConfigItem(keyName = "enableCatherby", name = "Catherby", description = "Include the Catherby herb patch", section = HERB_PATCHES, position = 2)
	default boolean enableCatherby()
	{
		return true;
	}

	@ConfigItem(keyName = "enableCivitas", name = "Civitas illa Fortis", description = "Include the Civitas illa Fortis herb patch", section = HERB_PATCHES, position = 3)
	default boolean enableCivitas()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFalador", name = "Falador", description = "Include the Falador herb patch", section = HERB_PATCHES, position = 4)
	default boolean enableFalador()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFarmingGuild", name = "Farming Guild", description = "Include the Farming Guild herb patch", section = HERB_PATCHES, position = 5)
	default boolean enableFarmingGuild()
	{
		return true;
	}

	@ConfigItem(keyName = "enableHarmony", name = "Harmony", description = "Include the Harmony herb patch", section = HERB_PATCHES, position = 6)
	default boolean enableHarmony()
	{
		return true;
	}

	@ConfigItem(keyName = "enableHosidius", name = "Hosidius", description = "Include the Hosidius herb patch", section = HERB_PATCHES, position = 7)
	default boolean enableHosidius()
	{
		return true;
	}

	@ConfigItem(keyName = "enableMorytania", name = "Morytania", description = "Include the Morytania herb patch", section = HERB_PATCHES, position = 8)
	default boolean enableMorytania()
	{
		return true;
	}

	@ConfigItem(keyName = "enableTrollStronghold", name = "Troll Stronghold", description = "Include the Troll Stronghold herb patch", section = HERB_PATCHES, position = 9)
	default boolean enableTrollStronghold()
	{
		return true;
	}

	@ConfigItem(keyName = "enableWeiss", name = "Weiss", description = "Include the Weiss herb patch", section = HERB_PATCHES, position = 10)
	default boolean enableWeiss()
	{
		return true;
	}

	@ConfigItem(keyName = "enableAldarinHops", name = "Aldarin", description = "Include the Aldarin hops patch", section = HOPS_PATCHES, position = 1)
	default boolean enableAldarinHops()
	{
		return true;
	}

	@ConfigItem(keyName = "enableEntranaHops", name = "Entrana", description = "Include the Entrana hops patch", section = HOPS_PATCHES, position = 2)
	default boolean enableEntranaHops()
	{
		return true;
	}

	@ConfigItem(keyName = "enableLumbridgeHops", name = "Lumbridge", description = "Include the Lumbridge hops patch", section = HOPS_PATCHES, position = 3)
	default boolean enableLumbridgeHops()
	{
		return true;
	}

	@ConfigItem(keyName = "enableSeersVillageHops", name = "Seers' Village", description = "Include the Seers' Village hops patch", section = HOPS_PATCHES, position = 4)
	default boolean enableSeersVillageHops()
	{
		return true;
	}

	@ConfigItem(keyName = "enableYanilleHops", name = "Yanille", description = "Include the Yanille hops patch", section = HOPS_PATCHES, position = 5)
	default boolean enableYanilleHops()
	{
		return true;
	}

	@ConfigItem(keyName = "enableAuburnvaleTree", name = "Auburnvale", description = "Include the Auburnvale tree patch", section = TREE_PATCHES, position = 1)
	default boolean enableAuburnvaleTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFaladorTree", name = "Falador", description = "Include the Falador tree patch", section = TREE_PATCHES, position = 2)
	default boolean enableFaladorTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFarmingGuildTree", name = "Farming Guild", description = "Include the Farming Guild tree patch", section = TREE_PATCHES, position = 3)
	default boolean enableFarmingGuildTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableGnomeStrongholdTree", name = "Gnome Stronghold", description = "Include the Gnome Stronghold tree patch", section = TREE_PATCHES, position = 4)
	default boolean enableGnomeStrongholdTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableLumbridgeTree", name = "Lumbridge", description = "Include the Lumbridge tree patch", section = TREE_PATCHES, position = 5)
	default boolean enableLumbridgeTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableTaverleyTree", name = "Taverley", description = "Include the Taverley tree patch", section = TREE_PATCHES, position = 6)
	default boolean enableTaverleyTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableVarrockTree", name = "Varrock", description = "Include the Varrock tree patch", section = TREE_PATCHES, position = 7)
	default boolean enableVarrockTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableBrimhavenFruitTree", name = "Brimhaven", description = "Include the Brimhaven fruit tree patch", section = FRUIT_TREE_PATCHES, position = 1)
	default boolean enableBrimhavenFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableCatherbyFruitTree", name = "Catherby", description = "Include the Catherby fruit tree patch", section = FRUIT_TREE_PATCHES, position = 2)
	default boolean enableCatherbyFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFarmingGuildFruitTree", name = "Farming Guild", description = "Include the Farming Guild fruit tree patch", section = FRUIT_TREE_PATCHES, position = 3)
	default boolean enableFarmingGuildFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableGnomeStrongholdFruitTree", name = "Gnome Stronghold", description = "Include the Gnome Stronghold fruit tree patch", section = FRUIT_TREE_PATCHES, position = 4)
	default boolean enableGnomeStrongholdFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableKastoriFruitTree", name = "Kastori", description = "Include the Kastori fruit tree patch", section = FRUIT_TREE_PATCHES, position = 5)
	default boolean enableKastoriFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableLletyaFruitTree", name = "Lletya", description = "Include the Lletya fruit tree patch", section = FRUIT_TREE_PATCHES, position = 6)
	default boolean enableLletyaFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableGnomeVillageFruitTree", name = "Tree Gnome Village", description = "Include the Tree Gnome Village fruit tree patch", section = FRUIT_TREE_PATCHES, position = 7)
	default boolean enableGnomeVillageFruitTree()
	{
		return true;
	}

	@ConfigItem(keyName = "enableAnglersRetreatHardwood", name = "Anglers' Retreat", description = "Include the Anglers' Retreat hardwood tree patch", section = HARDWOOD_TREE_PATCHES, position = 1)
	default boolean enableAnglersRetreatHardwood()
	{
		return true;
	}

	@ConfigItem(keyName = "enableAviumSavannahHardwood", name = "Avium Savannah", description = "Include the Avium Savannah hardwood tree patch", section = HARDWOOD_TREE_PATCHES, position = 2)
	default boolean enableAviumSavannahHardwood()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFossilIslandEastHardwood", name = "Fossil Island — East", description = "Include the east Fossil Island hardwood tree patch", section = HARDWOOD_TREE_PATCHES, position = 3)
	default boolean enableFossilIslandEastHardwood()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFossilIslandMiddleHardwood", name = "Fossil Island — Middle", description = "Include the middle Fossil Island hardwood tree patch", section = HARDWOOD_TREE_PATCHES, position = 4)
	default boolean enableFossilIslandMiddleHardwood()
	{
		return true;
	}

	@ConfigItem(keyName = "enableFossilIslandWestHardwood", name = "Fossil Island — West", description = "Include the west Fossil Island hardwood tree patch", section = HARDWOOD_TREE_PATCHES, position = 5)
	default boolean enableFossilIslandWestHardwood()
	{
		return true;
	}

	@ConfigItem(keyName = "enableGreatConchCalquat", name = "Great Conch", description = "Include the Great Conch calquat patch", section = CALQUAT_PATCHES, position = 1)
	default boolean enableGreatConchCalquat()
	{
		return true;
	}

	@ConfigItem(keyName = "enableKastoriCalquat", name = "Kastori", description = "Include the Kastori calquat patch", section = CALQUAT_PATCHES, position = 2)
	default boolean enableKastoriCalquat()
	{
		return true;
	}

	@ConfigItem(keyName = "enableTaiBwoWannaiCalquat", name = "Tai Bwo Wannai", description = "Include the Tai Bwo Wannai calquat patch", section = CALQUAT_PATCHES, position = 3)
	default boolean enableTaiBwoWannaiCalquat()
	{
		return true;
	}

	@ConfigItem(keyName = "enableCoralEast", name = "Great Conch — East", description = "Include the east Great Conch coral nursery", section = CORAL_PATCHES, position = 1)
	default boolean enableCoralEast()
	{
		return true;
	}

	@ConfigItem(keyName = "enableCoralWest", name = "Great Conch — West", description = "Include the west Great Conch coral nursery", section = CORAL_PATCHES, position = 2)
	default boolean enableCoralWest()
	{
		return true;
	}

	@ConfigItem(keyName = "enableSeaweedNorth", name = "Fossil Island — North", description = "Include the north Fossil Island seaweed patch", section = SEAWEED_PATCHES, position = 1)
	default boolean enableSeaweedNorth()
	{
		return true;
	}

	@ConfigItem(keyName = "enableSeaweedSouth", name = "Fossil Island — South", description = "Include the south Fossil Island seaweed patch", section = SEAWEED_PATCHES, position = 2)
	default boolean enableSeaweedSouth()
	{
		return true;
	}

	@ConfigItem(keyName = "enableValleyNorthBirdHouse", name = "Verdant Valley — Northeast", description = "Include the northeast Verdant Valley birdhouse", section = BIRD_HOUSE_PATCHES, position = 1)
	default boolean enableValleyNorthBirdHouse()
	{
		return true;
	}

	@ConfigItem(keyName = "enableValleySouthBirdHouse", name = "Verdant Valley — Southwest", description = "Include the southwest Verdant Valley birdhouse", section = BIRD_HOUSE_PATCHES, position = 2)
	default boolean enableValleySouthBirdHouse()
	{
		return true;
	}

	@ConfigItem(keyName = "enableMeadowNorthBirdHouse", name = "Mushroom Meadow — North", description = "Include the north Mushroom Meadow birdhouse", section = BIRD_HOUSE_PATCHES, position = 3)
	default boolean enableMeadowNorthBirdHouse()
	{
		return true;
	}

	@ConfigItem(keyName = "enableMeadowSouthBirdHouse", name = "Mushroom Meadow — South", description = "Include the south Mushroom Meadow birdhouse", section = BIRD_HOUSE_PATCHES, position = 4)
	default boolean enableMeadowSouthBirdHouse()
	{
		return true;
	}

	@ConfigItem(
		keyName = PATCH_TYPE_ORDER_KEY,
		name = "Patch type order",
		description = "Saved order of patch types in the PatchMaster panel and run planner",
		hidden = true
	)
	default String patchTypeOrder()
	{
		return PatchTypeOrder.DEFAULT_SERIALIZED;
	}

	@ConfigItem(
		keyName = "includeEmpty",
		name = "Include empty patches",
		description = "Include empty patches in ready runs so they can be planted",
		section = RUN_PLANNING,
		position = 0
	)
	default boolean includeEmpty()
	{
		return true;
	}

	@ConfigItem(
		keyName = "includeUnknown",
		name = "Include unknown patches",
		description = "Include unvisited patches so Time Tracking can learn their state",
		section = RUN_PLANNING,
		position = 1
	)
	default boolean includeUnknown()
	{
		return true;
	}

	@ConfigItem(
		keyName = "skipEmptyWithoutSeed",
		name = "Require planting item",
		description = "Route to an empty patch only when its seed, sapling, or spore is carried",
		section = RUN_PLANNING,
		position = 2
	)
	default boolean skipEmptyWithoutSeed()
	{
		return false;
	}

	@ConfigItem(
		keyName = "autoAdvance",
		name = "Advance after replant",
		description = "During a started run, route to the next patch after Time Tracking observes the current patch growing",
		section = RUN_PLANNING,
		position = 3
	)
	default boolean autoAdvance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "waitForCompostBeforeAutoAdvance",
		name = "Wait for compost",
		description = "For compostable farming patches, wait until Time Tracking records compost before auto-advancing",
		section = RUN_PLANNING,
		position = 4
	)
	default boolean waitForCompostBeforeAutoAdvance()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCompostReminders",
		name = "Show scene highlights",
		description = "Draw state outlines and item reminders over nearby farming patches",
		section = COMPOST_REMINDERS,
		position = 0
	)
	default boolean showCompostReminders()
	{
		return true;
	}

	@ConfigItem(
		keyName = "compostReminderColor",
		name = "Needs compost color",
		description = "Color used when a growing herb or seaweed patch still needs compost",
		section = COMPOST_REMINDERS,
		position = 1
	)
	default Color compostReminderColor()
	{
		return PatchMasterTheme.COMPOST;
	}

	@ConfigItem(
		keyName = "readyPatchColor",
		name = "Ready crop color",
		description = "Ripe crop highlight color",
		section = COMPOST_REMINDERS,
		position = 2
	)
	default Color readyPatchColor()
	{
		return PatchMasterTheme.READY;
	}

	@ConfigItem(
		keyName = "emptyPatchColor",
		name = "Empty patch color",
		description = "Tilled soil highlight color",
		section = COMPOST_REMINDERS,
		position = 3
	)
	default Color emptyPatchColor()
	{
		return PatchMasterTheme.EMPTY;
	}

	@ConfigItem(
		keyName = "deadPatchColor",
		name = "Dead crop color",
		description = "Withered crop highlight color",
		section = COMPOST_REMINDERS,
		position = 4
	)
	default Color deadPatchColor()
	{
		return PatchMasterTheme.DEAD;
	}

	@ConfigItem(
		keyName = "diseasedPatchColor",
		name = "Diseased crop color",
		description = "Diseased crop highlight color",
		section = COMPOST_REMINDERS,
		position = 5
	)
	default Color diseasedPatchColor()
	{
		return PatchMasterTheme.DISEASED;
	}

	@ConfigItem(
		keyName = "treeReadyAction",
		name = "Tree ready icon",
		description = "Icon shown on ready regular, fruit, hardwood, and calquat trees",
		section = COMPOST_REMINDERS,
		position = 6
	)
	default TreeReadyAction treeReadyAction()
	{
		return TreeReadyAction.MAGIC_SECATEURS;
	}

	@ConfigItem(
		keyName = "farmingContractTier",
		name = "Pre-plant contract tier",
		description = "Choose the medium or hard Farming Guild contract pre-plant plan",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 0
	)
	default FarmingContractTier farmingContractTier()
	{
		return FarmingContractTier.HARD;
	}

	@ConfigItem(
		keyName = "hardContractHerb",
		name = "Hard herb",
		description = "Herb used by the hard contract pre-plant plan",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 1
	)
	default FarmingContractOptions.HardHerb hardContractHerb()
	{
		return FarmingContractOptions.HardHerb.CADANTINE;
	}

	@ConfigItem(
		keyName = "hardContractTree",
		name = "Hard tree",
		description = "Choose Maple tree or Yew tree for the hard contract tree spot",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 2
	)
	default FarmingContractOptions.HardTree hardContractTree()
	{
		return FarmingContractOptions.HardTree.MAPLE;
	}

	@ConfigItem(
		keyName = "hardContractBush",
		name = "Hard bush",
		description = "Bush used by the hard contract pre-plant plan",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 3
	)
	default FarmingContractOptions.HardBush hardContractBush()
	{
		return FarmingContractOptions.HardBush.POISON_IVY;
	}

	@ConfigItem(
		keyName = "mediumContractBush",
		name = "Medium bush",
		description = "Bush used by the medium contract pre-plant plan",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 4
	)
	default FarmingContractOptions.MediumBush mediumContractBush()
	{
		return FarmingContractOptions.MediumBush.WHITEBERRIES;
	}

	@ConfigItem(
		keyName = "farmingContractPreplantColor",
		name = "Pre-plant reminder color",
		description = "Color used for Farming Guild contract pre-plant reminders",
		section = FARMING_CONTRACTS,
		hidden = true,
		position = 5
	)
	default Color farmingContractPreplantColor()
	{
		return PatchMasterTheme.ROUTING;
	}

	@ConfigItem(
		keyName = "clearPathOnShutdown",
		name = "Clear route when disabled",
		description = "Ask Shortest Path to clear its active route when PatchMaster stops",
		section = NAVIGATION,
		position = 0
	)
	default boolean clearPathOnShutdown()
	{
		return false;
	}
}
