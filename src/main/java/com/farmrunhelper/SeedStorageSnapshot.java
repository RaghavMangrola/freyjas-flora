package com.farmrunhelper;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

final class SeedStorageSnapshot
{
	private static final String VERSION = "1";

	private final long updatedAtMs;
	private final Map<Integer, Integer> itemQuantities;

	private SeedStorageSnapshot(long updatedAtMs, Map<Integer, Integer> itemQuantities)
	{
		this.updatedAtMs = updatedAtMs;
		this.itemQuantities = Collections.unmodifiableMap(new TreeMap<>(itemQuantities));
	}

	static SeedStorageSnapshot unknown()
	{
		return new SeedStorageSnapshot(-1L, Collections.emptyMap());
	}

	static SeedStorageSnapshot observed(long updatedAtMs, Map<Integer, Integer> itemQuantities)
	{
		return new SeedStorageSnapshot(updatedAtMs, itemQuantities);
	}

	static SeedStorageSnapshot parse(String value)
	{
		if (value == null || value.isEmpty())
		{
			return unknown();
		}

		String[] fields = value.split(";", -1);
		if (fields.length != 3 || !VERSION.equals(fields[0]))
		{
			return unknown();
		}

		long updatedAt;
		try
		{
			updatedAt = Long.parseLong(fields[1]);
		}
		catch (NumberFormatException ex)
		{
			return unknown();
		}

		if (updatedAt < 0L)
		{
			return unknown();
		}

		Map<Integer, Integer> items = new TreeMap<>();
		if (!fields[2].isEmpty())
		{
			for (String entry : fields[2].split(","))
			{
				String[] pair = entry.split(":", -1);
				if (pair.length != 2)
				{
					continue;
				}

				try
				{
					int itemId = Integer.parseInt(pair[0]);
					int quantity = Integer.parseInt(pair[1]);
					if (itemId >= 0 && quantity > 0)
					{
						items.merge(itemId, quantity, SeedStorageSnapshot::saturatedAdd);
					}
				}
				catch (NumberFormatException ignored)
				{
					// Keep the valid entries from a partially damaged snapshot.
				}
			}
		}

		return observed(updatedAt, items);
	}

	private static int saturatedAdd(int left, int right)
	{
		return left > Integer.MAX_VALUE - right ? Integer.MAX_VALUE : left + right;
	}

	boolean isObserved()
	{
		return updatedAtMs >= 0L;
	}

	long getUpdatedAtMs()
	{
		return updatedAtMs;
	}

	Map<Integer, Integer> getItemQuantities()
	{
		return itemQuantities;
	}

	boolean hasSameItems(Map<Integer, Integer> other)
	{
		return itemQuantities.equals(other);
	}

	String serialize()
	{
		StringBuilder items = new StringBuilder();
		for (Map.Entry<Integer, Integer> entry : itemQuantities.entrySet())
		{
			if (items.length() > 0)
			{
				items.append(',');
			}
			items.append(entry.getKey()).append(':').append(entry.getValue());
		}
		return VERSION + ";" + updatedAtMs + ";" + items;
	}
}
