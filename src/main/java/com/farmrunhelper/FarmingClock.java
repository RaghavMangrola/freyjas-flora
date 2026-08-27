package com.farmrunhelper;

final class FarmingClock
{
	private FarmingClock()
	{
	}

	static long readyAt(
		int tickRateMinutes,
		int stage,
		int stages,
		long observedAt,
		Integer offsetPrecisionMinutes,
		Integer offsetMinutes)
	{
		if (tickRateMinutes <= 0 || observedAt <= 0 || stage >= stages - 1)
		{
			return 0;
		}

		long observedTick = tickTime(
			tickRateMinutes,
			0,
			observedAt,
			offsetPrecisionMinutes,
			offsetMinutes);

		return tickTime(
			tickRateMinutes,
			stages - 1 - stage,
			observedTick,
			offsetPrecisionMinutes,
			offsetMinutes);
	}

	static long tickTime(
		int tickRateMinutes,
		int ticks,
		long requestedTime,
		Integer offsetPrecisionMinutes,
		Integer offsetMinutes)
	{
		long offsetSeconds = 0;
		if (offsetPrecisionMinutes != null && offsetMinutes != null
			&& (offsetPrecisionMinutes >= tickRateMinutes || offsetPrecisionMinutes >= 40))
		{
			offsetSeconds = Math.floorMod(offsetMinutes, tickRateMinutes) * 60L;
		}

		long adjustedTime = requestedTime + offsetSeconds;
		long tickSeconds = tickRateMinutes * 60L;
		long currentTick = adjustedTime - Math.floorMod(adjustedTime, tickSeconds);
		return currentTick + ticks * tickSeconds - offsetSeconds;
	}
}
