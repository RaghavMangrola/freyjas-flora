package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FarmingClockTest
{
	@Test
	public void calculatesCompletionFromObservedFarmingTick()
	{
		assertEquals(6000L, FarmingClock.readyAt(20, 0, 5, 1210L, null, null));
		assertEquals(2400L, FarmingClock.readyAt(20, 3, 5, 1210L, null, null));
	}

	@Test
	public void honorsTimeTrackingOffsetPrecision()
	{
		assertEquals(900L, FarmingClock.tickTime(20, 0, 1210L, 40, 5));
		assertEquals(5700L, FarmingClock.readyAt(20, 0, 5, 1210L, 40, 5));
	}

	@Test
	public void terminalAndTimelessStatesHaveNoCompletionTimestamp()
	{
		assertEquals(0L, FarmingClock.readyAt(20, 4, 5, 1210L, null, null));
		assertEquals(0L, FarmingClock.readyAt(0, 0, 1, 1210L, null, null));
	}
}
