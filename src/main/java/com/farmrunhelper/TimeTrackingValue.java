package com.farmrunhelper;

import java.util.Optional;

final class TimeTrackingValue
{
	private final int varbitValue;
	private final long observedAt;

	private TimeTrackingValue(int varbitValue, long observedAt)
	{
		this.varbitValue = varbitValue;
		this.observedAt = observedAt;
	}

	static Optional<TimeTrackingValue> parse(String storedValue)
	{
		if (storedValue == null)
		{
			return Optional.empty();
		}

		String[] parts = storedValue.split(":", -1);
		if (parts.length != 2)
		{
			return Optional.empty();
		}

		try
		{
			int value = Integer.parseInt(parts[0]);
			long timestamp = Long.parseLong(parts[1]);
			if (value < 0 || value > 255 || timestamp <= 0)
			{
				return Optional.empty();
			}
			return Optional.of(new TimeTrackingValue(value, timestamp));
		}
		catch (NumberFormatException ignored)
		{
			return Optional.empty();
		}
	}

	int getVarbitValue()
	{
		return varbitValue;
	}

	long getObservedAt()
	{
		return observedAt;
	}
}
