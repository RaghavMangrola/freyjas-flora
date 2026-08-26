package com.farmrunhelper;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

final class FarmRunIcon
{
	private FarmRunIcon()
	{
	}

	static BufferedImage fallback()
	{
		return new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	}

	@SuppressWarnings("deprecation")
	static void loadFarmersShirt(ItemManager itemManager, int size, Consumer<BufferedImage> onLoaded)
	{
		if (itemManager == null)
		{
			return;
		}

		AsyncBufferedImage source = itemManager.getImage(ItemID.FARMERS_SHIRT);
		if (source == null)
		{
			return;
		}

		source.onLoaded(() -> onLoaded.accept(fitOpaqueContent(source, size)));
	}

	static BufferedImage fitOpaqueContent(BufferedImage source, int size)
	{
		int minimumX = source.getWidth();
		int minimumY = source.getHeight();
		int maximumX = -1;
		int maximumY = -1;
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				if ((source.getRGB(x, y) >>> 24) == 0)
				{
					continue;
				}
				minimumX = Math.min(minimumX, x);
				minimumY = Math.min(minimumY, y);
				maximumX = Math.max(maximumX, x);
				maximumY = Math.max(maximumY, y);
			}
		}

		if (maximumX < minimumX || maximumY < minimumY)
		{
			return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		}

		BufferedImage trimmed = source.getSubimage(
			minimumX,
			minimumY,
			maximumX - minimumX + 1,
			maximumY - minimumY + 1);
		double scale = Math.min((double) size / trimmed.getWidth(), (double) size / trimmed.getHeight());
		int width = Math.max(1, (int) Math.round(trimmed.getWidth() * scale));
		int height = Math.max(1, (int) Math.round(trimmed.getHeight() * scale));
		BufferedImage scaled = ImageUtil.resizeImage(trimmed, width, height);
		BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D graphics = result.createGraphics();
		try
		{
			graphics.drawImage(scaled, (size - width) / 2, (size - height) / 2, null);
		}
		finally
		{
			graphics.dispose();
		}
		return result;
	}
}
