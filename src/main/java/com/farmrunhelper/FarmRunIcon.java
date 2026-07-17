package com.farmrunhelper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

final class FarmRunIcon
{
	private FarmRunIcon()
	{
	}

	static BufferedImage create()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Path2D leaf = new Path2D.Double();
			leaf.moveTo(2.5, 13);
			leaf.curveTo(1.8, 6, 7, 1.2, 14.2, 1.8);
			leaf.curveTo(14, 9.2, 9.5, 14.2, 2.5, 13);
			leaf.closePath();
			graphics.setColor(new Color(18, 55, 28));
			graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			graphics.draw(leaf);
			graphics.setColor(new Color(76, 164, 75));
			graphics.fill(leaf);

			graphics.setColor(new Color(196, 229, 133));
			graphics.setStroke(new BasicStroke(1.4f));
			graphics.drawLine(2, 14, 12, 4);

			Path2D droplet = new Path2D.Double();
			droplet.moveTo(3.2, 0.8);
			droplet.curveTo(1.9, 2.5, 1.1, 3.6, 1.1, 4.7);
			droplet.curveTo(1.1, 6.1, 2, 7, 3.2, 7);
			droplet.curveTo(4.5, 7, 5.4, 6.1, 5.4, 4.7);
			droplet.curveTo(5.4, 3.6, 4.5, 2.5, 3.2, 0.8);
			droplet.closePath();
			graphics.setColor(new Color(22, 61, 85));
			graphics.setStroke(new BasicStroke(1.3f));
			graphics.draw(droplet);
			graphics.setColor(PatchMasterTheme.COMPOST);
			graphics.fill(droplet);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}
}
