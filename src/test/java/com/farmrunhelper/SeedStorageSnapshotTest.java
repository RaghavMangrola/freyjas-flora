package com.farmrunhelper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SeedStorageSnapshotTest
{
	@Test
	public void roundTripsAStableSortedSnapshot()
	{
		Map<Integer, Integer> items = new LinkedHashMap<>();
		items.put(5300, 12);
		items.put(5295, 75);

		SeedStorageSnapshot snapshot = SeedStorageSnapshot.observed(123456L, items);
		assertEquals("1;123456;5295:75,5300:12", snapshot.serialize());

		SeedStorageSnapshot restored = SeedStorageSnapshot.parse(snapshot.serialize());
		assertTrue(restored.isObserved());
		assertEquals(123456L, restored.getUpdatedAtMs());
		assertEquals(items, restored.getItemQuantities());
	}

	@Test
	public void preservesKnownEmptyAndRejectsUnknownVersions()
	{
		SeedStorageSnapshot empty = SeedStorageSnapshot.parse("1;9876;");
		assertTrue(empty.isObserved());
		assertTrue(empty.getItemQuantities().isEmpty());

		assertFalse(SeedStorageSnapshot.parse(null).isObserved());
		assertFalse(SeedStorageSnapshot.parse("2;9876;5295:1").isObserved());
		assertFalse(SeedStorageSnapshot.parse("1;bad;5295:1").isObserved());
	}

	@Test
	public void keepsValidEntriesFromAPartiallyDamagedItemList()
	{
		SeedStorageSnapshot snapshot = SeedStorageSnapshot.parse(
			"1;123;5295:4,broken,5300:nope,5304:7,-1:9,5291:0");

		assertTrue(snapshot.isObserved());
		assertEquals(2, snapshot.getItemQuantities().size());
		assertEquals(Integer.valueOf(4), snapshot.getItemQuantities().get(5295));
		assertEquals(Integer.valueOf(7), snapshot.getItemQuantities().get(5304));
	}
}
