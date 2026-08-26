package com.farmrunhelper;

/** Configurable choices within the guide's medium and hard pre-plant plans. */
public final class FarmingContractOptions
{
	private FarmingContractOptions()
	{
	}

	public enum HardHerb
	{
		CADANTINE("Cadantine"),
		LANTADYME("Lantadyme"),
		SNAPDRAGON("Snapdragon");

		private final String displayName;

		HardHerb(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	public enum HardTree
	{
		MAPLE("Maple tree"),
		YEW("Yew tree");

		private final String displayName;

		HardTree(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	public enum HardBush
	{
		POISON_IVY("Poison ivy"),
		WHITEBERRIES("Whiteberries");

		private final String displayName;

		HardBush(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	public enum MediumBush
	{
		WHITEBERRIES("Whiteberries"),
		JANGERBERRIES("Jangerberries");

		private final String displayName;

		MediumBush(String displayName)
		{
			this.displayName = displayName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}
}
