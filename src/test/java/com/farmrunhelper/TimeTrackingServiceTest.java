package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeTrackingServiceTest
{
	@Test
	public void birdHousesBecomeReadyFiftyMinutesAfterTheRecordedChange()
	{
		assertEquals(4_000L, TimeTrackingService.birdHouseReadyAt(1_000L));

		PatchPrediction prediction = PatchPrediction.known(
			"Yew bird house",
			PatchState.GROWING,
			1_000L,
			TimeTrackingService.birdHouseReadyAt(1_000L));
		assertEquals(PatchState.GROWING, prediction.getEffectiveState(3_999L));
		assertEquals(PatchState.READY, prediction.getEffectiveState(4_000L));
	}
}
