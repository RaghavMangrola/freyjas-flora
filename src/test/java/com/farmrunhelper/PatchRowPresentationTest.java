package com.farmrunhelper;

import java.util.Collections;
import java.util.EnumSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PatchRowPresentationTest
{
	@Test
	public void mapsUpcomingCurrentAndDoneRowsInOnePlace()
	{
		PatchRowPresentation upcoming = PatchRowPresentation.forPatch(
			FarmPatch.ARDOUGNE,
			null,
			Collections.emptySet(),
			PatchState.READY);
		assertEquals(PatchRowPresentation.State.UPCOMING, upcoming.getState());
		assertEquals(PatchMasterTheme.READY, upcoming.getAccentColor());
		assertEquals("→", upcoming.getActionGlyph());
		assertEquals(1.0f, upcoming.getOpacity(), 0.0f);

		PatchRowPresentation current = PatchRowPresentation.forPatch(
			FarmPatch.ARDOUGNE,
			FarmPatch.ARDOUGNE,
			Collections.emptySet(),
			PatchState.DEAD);
		assertEquals(PatchRowPresentation.State.CURRENT, current.getState());
		assertEquals(PatchMasterTheme.ROUTING, current.getAccentColor());
		assertEquals(PatchMasterTheme.CURRENT_ROW, current.getBackgroundColor());
		assertEquals("➜", current.getActionGlyph());
		assertFalse("Current must not look completed", "✓".equals(current.getActionGlyph()));

		PatchRowPresentation done = PatchRowPresentation.forPatch(
			FarmPatch.ARDOUGNE,
			null,
			EnumSet.of(FarmPatch.ARDOUGNE),
			PatchState.DEAD);
		assertEquals(PatchRowPresentation.State.DONE, done.getState());
		assertEquals(PatchMasterTheme.DEAD, done.getAccentColor());
		assertEquals(0.5f, done.getOpacity(), 0.0f);
		assertEquals("✓", done.getActionGlyph());
	}

	@Test
	public void usesNeutralAccentForUnknownUpcomingPatches()
	{
		PatchRowPresentation unknown = PatchRowPresentation.forPatch(
			FarmPatch.ARDOUGNE,
			null,
			Collections.emptySet(),
			PatchState.UNKNOWN);

		assertEquals(PatchMasterTheme.INACTIVE, unknown.getAccentColor());
	}
}
