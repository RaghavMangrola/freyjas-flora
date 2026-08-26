package com.farmrunhelper;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarbitID;

/** Contract-only Farming Guild patches; these do not participate in farm runs. */
enum FarmingGuildContractPatch
{
	ALLOTMENT_NORTH(FarmingGuildContractPatchType.ALLOTMENT, VarbitID.FARMING_TRANSMIT_C),
	ALLOTMENT_SOUTH(FarmingGuildContractPatchType.ALLOTMENT, VarbitID.FARMING_TRANSMIT_D),
	FLOWER(FarmingGuildContractPatchType.FLOWER, VarbitID.FARMING_TRANSMIT_H),
	BUSH(FarmingGuildContractPatchType.BUSH, VarbitID.FARMING_TRANSMIT_B),
	CACTUS(FarmingGuildContractPatchType.CACTUS, VarbitID.FARMING_TRANSMIT_F),
	CELASTRUS(FarmingGuildContractPatchType.CELASTRUS, VarbitID.FARMING_TRANSMIT_L),
	REDWOOD(FarmingGuildContractPatchType.REDWOOD, VarbitID.FARMING_TRANSMIT_I),
	HERB(FarmingGuildContractPatchType.HERB, VarbitID.FARMING_TRANSMIT_E),
	TREE(FarmingGuildContractPatchType.TREE, VarbitID.FARMING_TRANSMIT_G),
	FRUIT_TREE(FarmingGuildContractPatchType.FRUIT_TREE, VarbitID.FARMING_TRANSMIT_K);

	private static final int FARMING_GUILD_REGION_ID = 4922;
	private final FarmingGuildContractPatchType type;
	private final int varbitId;

	FarmingGuildContractPatch(FarmingGuildContractPatchType type, int varbitId)
	{
		this.type = type;
		this.varbitId = varbitId;
	}

	FarmingGuildContractPatchType getType()
	{
		return type;
	}

	String getTimeTrackingKey()
	{
		return FARMING_GUILD_REGION_ID + "." + varbitId;
	}

	boolean matchesSceneObject(int objectVarbitId, WorldPoint objectLocation)
	{
		return objectVarbitId == varbitId
			&& objectLocation != null
			&& objectLocation.getRegionID() == FARMING_GUILD_REGION_ID;
	}

	boolean isEmptyValue(int value)
	{
		switch (this)
		{
			case ALLOTMENT_NORTH:
			case ALLOTMENT_SOUTH:
				return inAnyRange(value, new int[]{
					0, 5, 74, 76, 81, 83, 88, 90, 95, 97, 104, 106, 113, 115,
					124, 127, 141, 141, 145, 148, 152, 155, 159, 162, 168, 171,
					177, 180, 188, 192, 205, 205, 212, 212, 216, 219, 223, 226,
					232, 235, 241, 244, 252, 255
				});
			case FLOWER:
				return !inAnyRange(value, new int[]{
					8, 12, 13, 17, 18, 22, 23, 27, 28, 32, 37, 41,
					72, 75, 77, 80, 82, 85, 87, 90, 92, 95, 101, 104,
					137, 139, 142, 144, 147, 149, 152, 154, 157, 159, 166, 168,
					201, 204, 206, 209, 211, 214, 216, 219, 221, 224, 230, 233
				});
			case BUSH:
				return !inAnyRange(value, new int[]{
					5, 63, 70, 74, 80, 85, 91, 97, 103, 110, 116, 123,
					134, 138, 144, 149, 155, 161, 167, 174, 180, 187, 197, 225,
					250, 255
				});
			case CACTUS:
				return value < 8 || value > 58;
			case CELASTRUS:
				return value < 8 || value > 28;
			case REDWOOD:
				return !inAnyRange(value, new int[]{8, 37, 41, 55});
			default:
				return false;
		}
	}

	private static boolean inAnyRange(int value, int[] ranges)
	{
		for (int index = 0; index < ranges.length; index += 2)
		{
			if (value >= ranges[index] && value <= ranges[index + 1])
			{
				return true;
			}
		}
		return false;
	}
}

enum FarmingGuildContractPatchType
{
	ALLOTMENT,
	FLOWER,
	BUSH,
	CACTUS,
	CELASTRUS,
	REDWOOD,
	HERB,
	TREE,
	FRUIT_TREE
}
