package com.farmrunhelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreeReadyActionTest
{
	@Test
	public void appliesToEveryTreeFamilyAndNotOtherPatchTypes()
	{
		assertTrue(PatchType.TREE.usesTreeReadyAction());
		assertTrue(PatchType.FRUIT_TREE.usesTreeReadyAction());
		assertTrue(PatchType.HARDWOOD_TREE.usesTreeReadyAction());
		assertTrue(PatchType.CALQUAT.usesTreeReadyAction());
		assertFalse(PatchType.HERB.usesTreeReadyAction());
		assertFalse(PatchType.HOPS.usesTreeReadyAction());
		assertFalse(PatchType.CORAL.usesTreeReadyAction());
		assertFalse(PatchType.SEAWEED.usesTreeReadyAction());
		assertFalse(PatchType.BIRD_HOUSE.usesTreeReadyAction());
	}
}
