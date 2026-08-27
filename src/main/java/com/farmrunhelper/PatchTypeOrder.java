package com.farmrunhelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class PatchTypeOrder
{
	private static final List<PatchType> DEFAULT_ORDER;
	static final String DEFAULT_SERIALIZED;

	static
	{
		List<PatchType> defaultOrder = new ArrayList<>();
		Collections.addAll(defaultOrder, PatchType.values());
		DEFAULT_ORDER = Collections.unmodifiableList(defaultOrder);
		DEFAULT_SERIALIZED = serialize(DEFAULT_ORDER);
	}

	private PatchTypeOrder()
	{
	}

	static List<PatchType> parse(String serialized)
	{
		List<PatchType> parsed = new ArrayList<>();
		if (serialized != null)
		{
			for (String token : serialized.split(","))
			{
				try
				{
					parsed.add(PatchType.valueOf(token.trim().toUpperCase(Locale.ROOT)));
				}
				catch (IllegalArgumentException ignored)
				{
					// Ignore retired or malformed values and append missing types below.
				}
			}
		}
		return normalize(parsed);
	}

	static String serialize(List<PatchType> order)
	{
		StringBuilder serialized = new StringBuilder();
		for (PatchType type : normalize(order))
		{
			if (serialized.length() > 0)
			{
				serialized.append(',');
			}
			serialized.append(type.name());
		}
		return serialized.toString();
	}

	static List<FarmPatch> orderedPatches(String serialized)
	{
		List<FarmPatch> patches = new ArrayList<>();
		for (PatchType type : parse(serialized))
		{
			for (FarmPatch patch : FarmPatch.values())
			{
				if (patch.getType() == type)
				{
					patches.add(patch);
				}
			}
		}
		return patches;
	}

	static List<FarmPatch> orderedEnabledPatches(String serialized, FarmRunHelperConfig config)
	{
		List<FarmPatch> patches = new ArrayList<>();
		for (FarmPatch patch : orderedPatches(serialized))
		{
			if (PatchSelection.isEnabled(config, patch))
			{
				patches.add(patch);
			}
		}
		return patches;
	}

	private static List<PatchType> normalize(List<PatchType> order)
	{
		List<PatchType> normalized = new ArrayList<>();
		Set<PatchType> seen = EnumSet.noneOf(PatchType.class);
		if (order != null)
		{
			for (PatchType type : order)
			{
				if (type != null && seen.add(type))
				{
					normalized.add(type);
				}
			}
		}

		for (PatchType type : DEFAULT_ORDER)
		{
			if (seen.add(type))
			{
				normalized.add(type);
			}
		}
		return normalized;
	}
}
