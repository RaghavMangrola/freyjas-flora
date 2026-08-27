package com.farmrunhelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SeedInventory
{
	enum Location
	{
		INVENTORY("Inventory"),
		BANK("Bank"),
		SEED_VAULT("Seed Vault");

		private final String displayName;

		Location(String displayName)
		{
			this.displayName = displayName;
		}

		String getDisplayName()
		{
			return displayName;
		}
	}

	static final class ItemCount
	{
		private final String name;
		private final long quantity;

		private ItemCount(String name, long quantity)
		{
			this.name = name;
			this.quantity = quantity;
		}

		String getName()
		{
			return name;
		}

		long getQuantity()
		{
			return quantity;
		}
	}

	static final class Builder
	{
		private final EnumMap<Location, EnumMap<PatchType, Long>> allCounts = new EnumMap<>(Location.class);
		private final EnumMap<Location, EnumMap<PatchType, Long>> plantableCounts = new EnumMap<>(Location.class);
		private final EnumMap<Location, Map<String, Long>> items = new EnumMap<>(Location.class);
		private final EnumMap<Location, Long> updatedAt = new EnumMap<>(Location.class);

		private Builder()
		{
			for (Location location : Location.values())
			{
				allCounts.put(location, new EnumMap<>(PatchType.class));
				plantableCounts.put(location, new EnumMap<>(PatchType.class));
				items.put(location, new LinkedHashMap<>());
				updatedAt.put(location, -1L);
			}
		}

		Builder markObserved(Location location, long updatedAtMs)
		{
			updatedAt.put(location, updatedAtMs);
			return this;
		}

		Builder add(
			Location location,
			PatchType patchType,
			boolean directlyPlantable,
			String name,
			long quantity)
		{
			if (quantity <= 0L)
			{
				return this;
			}

			items.get(location).merge(name, quantity, Long::sum);
			if (patchType != null)
			{
				allCounts.get(location).merge(patchType, quantity, Long::sum);
				if (directlyPlantable)
				{
					plantableCounts.get(location).merge(patchType, quantity, Long::sum);
				}
			}
			return this;
		}

		private Builder addPlantableTotal(PatchType patchType, long quantity)
		{
			if (quantity > 0L)
			{
				allCounts.get(Location.INVENTORY).put(patchType, quantity);
				plantableCounts.get(Location.INVENTORY).put(patchType, quantity);
			}
			return this;
		}

		SeedInventory build()
		{
			return new SeedInventory(this);
		}
	}

	private final EnumMap<Location, EnumMap<PatchType, Long>> allCounts;
	private final EnumMap<Location, EnumMap<PatchType, Long>> plantableCounts;
	private final EnumMap<Location, List<ItemCount>> items;
	private final EnumMap<Location, Long> updatedAt;

	SeedInventory(
		int herbSeeds,
		int treeSaplings,
		int fruitTreeSaplings,
		int hardwoodSaplings,
		int calquatSaplings,
		int seaweedSpores)
	{
		this(legacyBuilder(
			herbSeeds,
			treeSaplings,
			fruitTreeSaplings,
			hardwoodSaplings,
			calquatSaplings,
			seaweedSpores));
	}

	private SeedInventory(Builder builder)
	{
		allCounts = copyCounts(builder.allCounts);
		plantableCounts = copyCounts(builder.plantableCounts);
		items = new EnumMap<>(Location.class);
		updatedAt = new EnumMap<>(builder.updatedAt);

		for (Location location : Location.values())
		{
			List<ItemCount> locationItems = new ArrayList<>();
			for (Map.Entry<String, Long> entry : builder.items.get(location).entrySet())
			{
				locationItems.add(new ItemCount(entry.getKey(), entry.getValue()));
			}
			locationItems.sort(Comparator.comparing(ItemCount::getName, String.CASE_INSENSITIVE_ORDER));
			items.put(location, Collections.unmodifiableList(locationItems));
		}
	}

	private static EnumMap<Location, EnumMap<PatchType, Long>> copyCounts(
		EnumMap<Location, EnumMap<PatchType, Long>> source)
	{
		EnumMap<Location, EnumMap<PatchType, Long>> copy = new EnumMap<>(Location.class);
		for (Location location : Location.values())
		{
			copy.put(location, new EnumMap<>(source.get(location)));
		}
		return copy;
	}

	private static Builder legacyBuilder(
		int herbSeeds,
		int treeSaplings,
		int fruitTreeSaplings,
		int hardwoodSaplings,
		int calquatSaplings,
		int seaweedSpores)
	{
		return builder()
			.markObserved(Location.INVENTORY, 0L)
			.addPlantableTotal(PatchType.HERB, herbSeeds)
			.addPlantableTotal(PatchType.TREE, treeSaplings)
			.addPlantableTotal(PatchType.FRUIT_TREE, fruitTreeSaplings)
			.addPlantableTotal(PatchType.HARDWOOD_TREE, hardwoodSaplings)
			.addPlantableTotal(PatchType.CALQUAT, calquatSaplings)
			.addPlantableTotal(PatchType.SEAWEED, seaweedSpores);
	}

	static Builder builder()
	{
		return new Builder();
	}

	static SeedInventory empty()
	{
		return builder().markObserved(Location.INVENTORY, 0L).build();
	}

	boolean canPlant(PatchType type)
	{
		if (!requiresPlantingItem(type))
		{
			return true;
		}
		return getPlantableCount(Location.INVENTORY, type) > 0L;
	}

	String getStoredPlantingLocations(PatchType type)
	{
		boolean bank = getPlantableCount(Location.BANK, type) > 0L;
		boolean vault = getPlantableCount(Location.SEED_VAULT, type) > 0L;
		if (bank && vault)
		{
			return "Bank / Seed Vault";
		}
		if (bank)
		{
			return "Bank";
		}
		return vault ? "Seed Vault" : null;
	}

	boolean hasSeedToPrepare(PatchType type)
	{
		long total = 0L;
		long plantable = 0L;
		for (Location location : Location.values())
		{
			total += getCount(allCounts, location, type);
			plantable += getPlantableCount(location, type);
		}
		return total > plantable;
	}

	boolean hasUnknownStoredStock()
	{
		return !isObserved(Location.BANK) || !isObserved(Location.SEED_VAULT);
	}

	boolean isObserved(Location location)
	{
		return updatedAt.get(location) >= 0L;
	}

	long getUpdatedAt(Location location)
	{
		return updatedAt.get(location);
	}

	List<ItemCount> getItems(Location location)
	{
		return items.get(location);
	}

	long getTotal(Location location)
	{
		long total = 0L;
		for (ItemCount item : items.get(location))
		{
			total += item.getQuantity();
		}
		return total;
	}

	private long getPlantableCount(Location location, PatchType type)
	{
		return getCount(plantableCounts, location, type);
	}

	private static long getCount(
		EnumMap<Location, EnumMap<PatchType, Long>> counts,
		Location location,
		PatchType type)
	{
		Long value = counts.get(location).get(type);
		return value == null ? 0L : value;
	}

	private static boolean requiresPlantingItem(PatchType type)
	{
		switch (type)
		{
			case HERB:
			case HOPS:
			case TREE:
			case FRUIT_TREE:
			case HARDWOOD_TREE:
			case CALQUAT:
			case SEAWEED:
				return true;
			default:
				return false;
		}
	}

}
