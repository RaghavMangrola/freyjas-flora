package com.farmrunhelper;

import java.awt.Color;
import java.util.Set;

/** Maps farm-run progress and observed patch status to one row presentation. */
final class PatchRowPresentation
{
	enum State
	{
		UPCOMING,
		CURRENT,
		DONE
	}

	private final State state;
	private final Color backgroundColor;
	private final Color accentColor;
	private final float opacity;
	private final String actionGlyph;
	private final String actionTooltip;

	private PatchRowPresentation(
		State state,
		Color backgroundColor,
		Color accentColor,
		float opacity,
		String actionGlyph,
		String actionTooltip)
	{
		this.state = state;
		this.backgroundColor = backgroundColor;
		this.accentColor = accentColor;
		this.opacity = opacity;
		this.actionGlyph = actionGlyph;
		this.actionTooltip = actionTooltip;
	}

	static PatchRowPresentation forPatch(
		FarmPatch patch,
		FarmPatch currentPatch,
		Set<FarmPatch> donePatches,
		PatchState patchState)
	{
		if (patch.equals(currentPatch))
		{
			return new PatchRowPresentation(
				State.CURRENT,
				PatchMasterTheme.CURRENT_ROW,
				PatchMasterTheme.ROUTING,
				1.0f,
				"➜",
				"Refresh the current Shortest Path destination");
		}
		if (donePatches.contains(patch))
		{
			return new PatchRowPresentation(
				State.DONE,
				PatchMasterTheme.CARD,
				statusAccent(patchState),
				0.5f,
				"✓",
				"Mark this patch as not done");
		}
		return new PatchRowPresentation(
			State.UPCOMING,
			PatchMasterTheme.CARD,
			statusAccent(patchState),
			1.0f,
			"→",
			"Show this destination with Shortest Path");
	}

	private static Color statusAccent(PatchState state)
	{
		switch (state)
		{
			case READY:
			case GROWING:
				return PatchMasterTheme.READY;
			case DISEASED:
			case DEAD:
				return PatchMasterTheme.DEAD;
			default:
				return PatchMasterTheme.INACTIVE;
		}
	}

	State getState()
	{
		return state;
	}

	Color getBackgroundColor()
	{
		return backgroundColor;
	}

	Color getAccentColor()
	{
		return accentColor;
	}

	float getOpacity()
	{
		return opacity;
	}

	String getActionGlyph()
	{
		return actionGlyph;
	}

	String getActionTooltip()
	{
		return actionTooltip;
	}

	boolean isDone()
	{
		return state == State.DONE;
	}
}
