package com.farmrunhelper;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarbitID;

enum FarmPatch
{
	ARDOUGNE("Ardougne", PatchType.HERB, 10548, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(2663, 3375, 0)),
	CATHERBY("Catherby", PatchType.HERB, 11062, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(2810, 3462, 0)),
	CIVITAS_ILLA_FORTIS("Civitas illa Fortis", PatchType.HERB, 6192, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(1587, 3101, 0)),
	FALADOR("Falador", PatchType.HERB, 12083, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(3052, 3309, 0)),
	FARMING_GUILD("Farming Guild", PatchType.HERB, 4922, VarbitID.FARMING_TRANSMIT_E,
		new WorldPoint(1235, 3724, 0)),
	HARMONY("Harmony", PatchType.HERB, 15148, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(3789, 2840, 0)),
	HOSIDIUS("Hosidius", PatchType.HERB, 6967, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(1729, 3558, 0)),
	MORYTANIA("Morytania", PatchType.HERB, 14391, VarbitID.FARMING_TRANSMIT_D,
		new WorldPoint(3598, 3524, 0)),
	TROLL_STRONGHOLD("Troll Stronghold", PatchType.HERB, 11321, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2828, 3696, 0)),
	WEISS("Weiss", PatchType.HERB, 11325, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2847, 3933, 0)),

	TREE_AUBURNVALE("Auburnvale", PatchType.TREE, 5427, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(1367, 3322, 0)),
	TREE_FALADOR("Falador", PatchType.TREE, 11828, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3004, 3373, 0)),
	TREE_FARMING_GUILD("Farming Guild", PatchType.TREE, 4922, VarbitID.FARMING_TRANSMIT_G,
		new WorldPoint(1232, 3736, 0)),
	TREE_GNOME_STRONGHOLD("Gnome Stronghold", PatchType.TREE, 9781, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2436, 3415, 0)),
	TREE_LUMBRIDGE("Lumbridge", PatchType.TREE, 12594, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3193, 3231, 0)),
	TREE_TAVERLEY("Taverley", PatchType.TREE, 11573, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2936, 3438, 0)),
	TREE_VARROCK("Varrock", PatchType.TREE, 12854, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3229, 3459, 0)),

	FRUIT_TREE_BRIMHAVEN("Brimhaven", PatchType.FRUIT_TREE, 11058, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2765, 3213, 0)),
	FRUIT_TREE_CATHERBY("Catherby", PatchType.FRUIT_TREE, 11317, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2860, 3433, 0)),
	FRUIT_TREE_FARMING_GUILD("Farming Guild", PatchType.FRUIT_TREE, 4922, VarbitID.FARMING_TRANSMIT_K,
		new WorldPoint(1242, 3758, 0)),
	FRUIT_TREE_GNOME_STRONGHOLD("Gnome Stronghold", PatchType.FRUIT_TREE, 9781, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(2476, 3446, 0)),
	FRUIT_TREE_KASTORI("Kastori", PatchType.FRUIT_TREE, 5423, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(1350, 3057, 0)),
	FRUIT_TREE_LLETYA("Lletya", PatchType.FRUIT_TREE, 9265, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2347, 3162, 0)),
	FRUIT_TREE_GNOME_VILLAGE("Tree Gnome Village", PatchType.FRUIT_TREE, 9777, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2490, 3180, 0)),

	HARDWOOD_ANGLERS_RETREAT("Anglers' Retreat", PatchType.HARDWOOD_TREE, 9770, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2470, 2704, 0)),
	HARDWOOD_AVIUM_SAVANNAH("Avium Savannah", PatchType.HARDWOOD_TREE, 6702, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(1687, 2972, 0)),
	HARDWOOD_FOSSIL_ISLAND_EAST("Fossil Island — East", PatchType.HARDWOOD_TREE, 14651, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3715, 3835, 0)),
	HARDWOOD_FOSSIL_ISLAND_MIDDLE("Fossil Island — Middle", PatchType.HARDWOOD_TREE, 14651, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(3708, 3833, 0)),
	HARDWOOD_FOSSIL_ISLAND_WEST("Fossil Island — West", PatchType.HARDWOOD_TREE, 14651, VarbitID.FARMING_TRANSMIT_C,
		new WorldPoint(3702, 3837, 0)),

	CALQUAT_GREAT_CONCH("Great Conch", PatchType.CALQUAT, 12581, VarbitID.FARMING_TRANSMIT_C,
		new WorldPoint(3129, 2406, 0)),
	CALQUAT_KASTORI("Kastori", PatchType.CALQUAT, 5423, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(1366, 3033, 0)),
	CALQUAT_TAI_BWO_WANNAI("Tai Bwo Wannai", PatchType.CALQUAT, 11056, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(2795, 3102, 0)),

	CORAL_EAST("Great Conch — East", PatchType.CORAL, 12581, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3296, 8861, 0), new WorldPoint(3272, 2463, 0), 13194,
		"Climb the steps at the east Great Conch dock; the route continues in the nursery."),
	CORAL_WEST("Great Conch — West", PatchType.CORAL, 12581, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(3296, 8861, 0), new WorldPoint(3272, 2463, 0), 13194,
		"Climb the steps at the east Great Conch dock; the route continues in the nursery."),

	SEAWEED_NORTH("Fossil Island — North", PatchType.SEAWEED, 15008, VarbitID.FARMING_TRANSMIT_A,
		new WorldPoint(3730, 10271, 0), new WorldPoint(3734, 3893, 0), 15008,
		"Take the Mushroom Forest rowboat out to sea, then dive; the route continues underwater."),
	SEAWEED_SOUTH("Fossil Island — South", PatchType.SEAWEED, 15008, VarbitID.FARMING_TRANSMIT_B,
		new WorldPoint(3730, 10271, 0), new WorldPoint(3734, 3893, 0), 15008,
		"Take the Mushroom Forest rowboat out to sea, then dive; the route continues underwater.");

	private final String displayName;
	private final PatchType type;
	private final int timeTrackingRegionId;
	private final int varbitId;
	private final WorldPoint patchPoint;
	private final WorldPoint accessPoint;
	private final int interiorRegionId;
	private final String accessInstruction;

	FarmPatch(String displayName, PatchType type, int timeTrackingRegionId, int varbitId, WorldPoint patchPoint)
	{
		this(displayName, type, timeTrackingRegionId, varbitId, patchPoint, null, -1, null);
	}

	FarmPatch(
		String displayName,
		PatchType type,
		int timeTrackingRegionId,
		int varbitId,
		WorldPoint patchPoint,
		WorldPoint accessPoint,
		int interiorRegionId,
		String accessInstruction)
	{
		this.displayName = displayName;
		this.type = type;
		this.timeTrackingRegionId = timeTrackingRegionId;
		this.varbitId = varbitId;
		this.patchPoint = patchPoint;
		this.accessPoint = accessPoint;
		this.interiorRegionId = interiorRegionId;
		this.accessInstruction = accessInstruction;
	}

	String getDisplayName()
	{
		return displayName;
	}

	PatchType getType()
	{
		return type;
	}

	String getTimeTrackingKey()
	{
		return timeTrackingRegionId + "." + varbitId;
	}

	int getTimeTrackingRegionId()
	{
		return timeTrackingRegionId;
	}

	int getVarbitId()
	{
		return varbitId;
	}

	boolean supportsCompostReminder()
	{
		return type == PatchType.HERB || type == PatchType.SEAWEED;
	}

	boolean matchesSceneObject(int objectVarbitId, WorldPoint objectLocation)
	{
		if (varbitId != objectVarbitId || objectLocation == null)
		{
			return false;
		}

		return patchPoint.getRegionID() == objectLocation.getRegionID();
	}

	WorldPoint getNavigationTarget(WorldPoint playerLocation)
	{
		if (accessPoint != null && (playerLocation == null || playerLocation.getRegionID() != interiorRegionId))
		{
			return accessPoint;
		}
		return patchPoint;
	}

	String getAccessInstruction(WorldPoint playerLocation)
	{
		if (accessPoint != null && (playerLocation == null || playerLocation.getRegionID() != interiorRegionId))
		{
			return accessInstruction;
		}
		return null;
	}
}
