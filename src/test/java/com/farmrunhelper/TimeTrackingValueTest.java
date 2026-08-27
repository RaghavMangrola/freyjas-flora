package com.farmrunhelper;

import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeTrackingValueTest
{
	@Test
	public void parsesRuneLiteTimeTrackingRecord()
	{
		Optional<TimeTrackingValue> parsed = TimeTrackingValue.parse("36:1720000000");
		assertTrue(parsed.isPresent());
		assertEquals(36, parsed.get().getVarbitValue());
		assertEquals(1720000000L, parsed.get().getObservedAt());
	}

	@Test
	public void rejectsMalformedOrOutOfRangeRecords()
	{
		assertFalse(TimeTrackingValue.parse(null).isPresent());
		assertFalse(TimeTrackingValue.parse("").isPresent());
		assertFalse(TimeTrackingValue.parse("36").isPresent());
		assertFalse(TimeTrackingValue.parse("abc:1720000000").isPresent());
		assertFalse(TimeTrackingValue.parse("256:1720000000").isPresent());
		assertFalse(TimeTrackingValue.parse("36:0").isPresent());
	}
}
