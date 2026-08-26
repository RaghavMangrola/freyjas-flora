package com.farmrunhelper;

/** The two Farming Guild pre-plant plans from the Ironman farming guide. */
public enum FarmingContractTier
{
	MEDIUM("Medium"),
	HARD("Hard");

	private final String displayName;

	FarmingContractTier(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
