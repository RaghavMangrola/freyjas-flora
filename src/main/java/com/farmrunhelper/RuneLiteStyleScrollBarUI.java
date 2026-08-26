package com.farmrunhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Mirrors RuneLite's FlatLaf scrollbar defaults when the standalone dev runner
 * has not installed the RuneLite look and feel.
 */
final class RuneLiteStyleScrollBarUI extends BasicScrollBarUI
{
	private static final int WIDTH = 7;

	@Override
	protected void configureScrollBarColors()
	{
		trackColor = PatchMasterTheme.BACKGROUND;
		thumbColor = PatchMasterTheme.INACTIVE;
	}

	@Override
	public Dimension getPreferredSize(JComponent component)
	{
		return new Dimension(WIDTH, WIDTH);
	}

	@Override
	protected Dimension getMinimumThumbSize()
	{
		return new Dimension(WIDTH, WIDTH);
	}

	@Override
	protected JButton createDecreaseButton(int orientation)
	{
		return createHiddenButton();
	}

	@Override
	protected JButton createIncreaseButton(int orientation)
	{
		return createHiddenButton();
	}

	@Override
	protected void paintTrack(Graphics graphics, JComponent component, Rectangle bounds)
	{
		graphics.setColor(PatchMasterTheme.BACKGROUND);
		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	protected void paintThumb(Graphics graphics, JComponent component, Rectangle bounds)
	{
		if (!scrollbar.isEnabled() || bounds.isEmpty())
		{
			return;
		}

		graphics.setColor(PatchMasterTheme.INACTIVE);
		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private static JButton createHiddenButton()
	{
		JButton button = new JButton();
		Dimension size = new Dimension(0, 0);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		return button;
	}
}
