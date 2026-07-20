package com.farmrunhelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.gameval.ItemID;

final class PatchCropIcon
{
	private static final Map<String, Integer> HARVEST_ITEM_IDS;

	static
	{
		Map<String, Integer> itemIds = new HashMap<>();
		itemIds.put("Guam", ItemID.UNIDENTIFIED_GUAM);
		itemIds.put("Marrentill", ItemID.UNIDENTIFIED_MARENTILL);
		itemIds.put("Tarromin", ItemID.UNIDENTIFIED_TARROMIN);
		itemIds.put("Harralander", ItemID.UNIDENTIFIED_HARRALANDER);
		itemIds.put("Ranarr", ItemID.UNIDENTIFIED_RANARR);
		itemIds.put("Toadflax", ItemID.UNIDENTIFIED_TOADFLAX);
		itemIds.put("Irit", ItemID.UNIDENTIFIED_IRIT);
		itemIds.put("Avantoe", ItemID.UNIDENTIFIED_AVANTOE);
		itemIds.put("Huasca", ItemID.UNIDENTIFIED_HUASCA);
		itemIds.put("Kwuarm", ItemID.UNIDENTIFIED_KWUARM);
		itemIds.put("Snapdragon", ItemID.UNIDENTIFIED_SNAPDRAGON);
		itemIds.put("Cadantine", ItemID.UNIDENTIFIED_CADANTINE);
		itemIds.put("Lantadyme", ItemID.UNIDENTIFIED_LANTADYME);
		itemIds.put("Dwarf weed", ItemID.UNIDENTIFIED_DWARF_WEED);
		itemIds.put("Torstol", ItemID.UNIDENTIFIED_TORSTOL);
		itemIds.put("Goutweed", ItemID.EADGAR_GOUTWEED_HERB);

		itemIds.put("Barley", ItemID.BARLEY);
		itemIds.put("Hammerstone", ItemID.HAMMERSTONE_HOPS);
		itemIds.put("Asgarnian", ItemID.ASGARNIAN_HOPS);
		itemIds.put("Jute", ItemID.JUTE_FIBRE);
		itemIds.put("Yanillian", ItemID.YANILLIAN_HOPS);
		itemIds.put("Flax", ItemID.FLAX);
		itemIds.put("Krandorian", ItemID.KRANDORIAN_HOPS);
		itemIds.put("Wildblood", ItemID.WILDBLOOD_HOPS);
		itemIds.put("Hemp", ItemID.HEMP);
		itemIds.put("Cotton", ItemID.COTTON_BOLL);

		itemIds.put("Oak", ItemID.OAK_LOGS);
		itemIds.put("Willow", ItemID.WILLOW_LOGS);
		itemIds.put("Maple", ItemID.MAPLE_LOGS);
		itemIds.put("Yew", ItemID.YEW_LOGS);
		itemIds.put("Magic", ItemID.MAGIC_LOGS);

		itemIds.put("Apple", ItemID.COOKING_APPLE);
		itemIds.put("Banana", ItemID.BANANA);
		itemIds.put("Orange", ItemID.ORANGE);
		itemIds.put("Curry", ItemID.CURRY_LEAF);
		itemIds.put("Pineapple", ItemID.PINEAPPLE);
		itemIds.put("Papaya", ItemID.PAPAYA);
		itemIds.put("Palm", ItemID.COCONUT);
		itemIds.put("Dragonfruit", ItemID.DRAGONFRUIT);

		itemIds.put("Teak", ItemID.TEAK_LOGS);
		itemIds.put("Mahogany", ItemID.MAHOGANY_LOGS);
		itemIds.put("Camphor", ItemID.CAMPHOR_LOGS);
		itemIds.put("Ironwood", ItemID.IRONWOOD_LOGS);
		itemIds.put("Rosewood", ItemID.ROSEWOOD_LOGS);
		itemIds.put("Calquat", ItemID.CALQUAT_FRUIT);

		itemIds.put("Elkhorn", ItemID.CORAL_ELKHORN);
		itemIds.put("Pillar", ItemID.CORAL_PILLAR);
		itemIds.put("Umbral", ItemID.CORAL_UMBRAL);
		itemIds.put("Seaweed", ItemID.GIANT_SEAWEED);

		itemIds.put("Bird house", ItemID.BIRDHOUSE_NORMAL);
		itemIds.put("Oak bird house", ItemID.BIRDHOUSE_OAK);
		itemIds.put("Willow bird house", ItemID.BIRDHOUSE_WILLOW);
		itemIds.put("Teak bird house", ItemID.BIRDHOUSE_TEAK);
		itemIds.put("Maple bird house", ItemID.BIRDHOUSE_MAPLE);
		itemIds.put("Mahogany bird house", ItemID.BIRDHOUSE_MAHOGANY);
		itemIds.put("Yew bird house", ItemID.BIRDHOUSE_YEW);
		itemIds.put("Magic bird house", ItemID.BIRDHOUSE_MAGIC);
		itemIds.put("Redwood bird house", ItemID.BIRDHOUSE_REDWOOD);
		HARVEST_ITEM_IDS = Collections.unmodifiableMap(itemIds);
	}

	private PatchCropIcon()
	{
	}

	static int itemIdFor(PatchType type, String cropName)
	{
		Integer itemId = HARVEST_ITEM_IDS.get(cropName);
		return itemId == null ? fallbackItemId(type) : itemId;
	}

	private static int fallbackItemId(PatchType type)
	{
		switch (type)
		{
			case HOPS:
				return ItemID.BARLEY;
			case TREE:
				return ItemID.OAK_LOGS;
			case FRUIT_TREE:
				return ItemID.COOKING_APPLE;
			case HARDWOOD_TREE:
				return ItemID.TEAK_LOGS;
			case CALQUAT:
				return ItemID.CALQUAT_FRUIT;
			case CORAL:
				return ItemID.CORAL_ELKHORN;
			case SEAWEED:
				return ItemID.GIANT_SEAWEED;
			case BIRD_HOUSE:
				return ItemID.BIRDHOUSE_NORMAL;
			case HERB:
			default:
				return ItemID.UNIDENTIFIED_GUAM;
		}
	}
}
