package com.farmrunhelper;

import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FarmRunIconTest
{
	@Test
	public void fitOpaqueContentRemovesTransparentPaddingBeforeItScales()
	{
		BufferedImage source = new BufferedImage(12, 16, BufferedImage.TYPE_INT_ARGB);
		for (int y = 3; y <= 12; y++)
		{
			for (int x = 4; x <= 7; x++)
			{
				source.setRGB(x, y, 0xffe0b94d);
			}
		}

		BufferedImage icon = FarmRunIcon.fitOpaqueContent(source, 16);

		assertEquals(16, icon.getHeight());
		assertEquals(16, opaqueBounds(icon)[3] - opaqueBounds(icon)[1] + 1);
	}

	@Test
	public void fitOpaqueContentLeavesAnEmptyImageTransparent()
	{
		BufferedImage icon = FarmRunIcon.fitOpaqueContent(
			new BufferedImage(12, 16, BufferedImage.TYPE_INT_ARGB),
			16);

		assertEquals(-1, opaqueBounds(icon)[2]);
	}

	private static int[] opaqueBounds(BufferedImage image)
	{
		int minimumX = image.getWidth();
		int minimumY = image.getHeight();
		int maximumX = -1;
		int maximumY = -1;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if ((image.getRGB(x, y) >>> 24) == 0)
				{
					continue;
				}
				minimumX = Math.min(minimumX, x);
				minimumY = Math.min(minimumY, y);
				maximumX = Math.max(maximumX, x);
				maximumY = Math.max(maximumY, y);
			}
		}
		return new int[] {minimumX, minimumY, maximumX, maximumY};
	}
}
