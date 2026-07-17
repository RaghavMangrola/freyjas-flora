package com.farmrunhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

final class CompostReminderOverlay extends Overlay
{
	private static final int OUTLINE_THICKNESS = 3;
	private static final int OUTLINE_FEATHERING = 2;
	private static final BasicStroke EMPTY_PATCH_STROKE = new BasicStroke(2.0f);

	private final Client client;
	private final FarmRunHelperConfig config;
	private final TimeTrackingService timeTrackingService;
	private final ModelOutlineRenderer modelOutlineRenderer;
	private final BufferedImage magicSecateursIcon;
	private final BufferedImage spadeIcon;
	private final BufferedImage plantCureIcon;
	private final BufferedImage herbSeedIcon;
	private final BufferedImage treeSaplingIcon;
	private final BufferedImage fruitTreeSaplingIcon;
	private final BufferedImage hardwoodSaplingIcon;
	private final BufferedImage calquatSaplingIcon;
	private final BufferedImage seaweedSporeIcon;
	private final BufferedImage ultracompostIcon;
	private final Map<FarmPatch, GameObject> patchObjects = new EnumMap<>(FarmPatch.class);

	@Inject
	CompostReminderOverlay(
		Client client,
		FarmRunHelperConfig config,
		TimeTrackingService timeTrackingService,
		ModelOutlineRenderer modelOutlineRenderer,
		ItemManager itemManager)
	{
		this.client = client;
		this.config = config;
		this.timeTrackingService = timeTrackingService;
		this.modelOutlineRenderer = modelOutlineRenderer;
		this.magicSecateursIcon = itemManager.getImage(ItemID.FAIRY_ENCHANTED_SECATEURS);
		this.spadeIcon = itemManager.getImage(ItemID.SPADE);
		this.plantCureIcon = itemManager.getImage(ItemID.PLANT_CURE);
		this.herbSeedIcon = itemManager.getImage(ItemID.GUAM_SEED);
		this.treeSaplingIcon = itemManager.getImage(ItemID.PLANTPOT_OAK_SAPLING);
		this.fruitTreeSaplingIcon = itemManager.getImage(ItemID.PLANTPOT_APPLE_SAPLING);
		this.hardwoodSaplingIcon = itemManager.getImage(ItemID.PLANTPOT_TEAK_SAPLING);
		this.calquatSaplingIcon = itemManager.getImage(ItemID.PLANTPOT_CALQUAT_SAPLING);
		this.seaweedSporeIcon = itemManager.getImage(ItemID.SEAWEED_SEED);
		this.ultracompostIcon = itemManager.getImage(ItemID.BUCKET_ULTRACOMPOST);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	void track(GameObject object)
	{
		if (object == null)
		{
			return;
		}

		ObjectComposition definition = client.getObjectDefinition(object.getId());
		if (definition == null)
		{
			return;
		}

		for (FarmPatch patch : FarmPatch.values())
		{
			if (patch.matchesSceneObject(definition.getVarbitId(), object.getWorldLocation()))
			{
				patchObjects.put(patch, object);
				return;
			}
		}
	}

	void untrack(GameObject object)
	{
		patchObjects.values().removeIf(trackedObject -> trackedObject == object);
	}

	void rebuild()
	{
		patchObjects.clear();
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return;
		}

		Tile[][][] tiles = worldView.getScene().getTiles();
		if (tiles == null)
		{
			return;
		}

		for (Tile[][] planeTiles : tiles)
		{
			if (planeTiles == null)
			{
				continue;
			}
			for (Tile[] row : planeTiles)
			{
				if (row == null)
				{
					continue;
				}
				for (Tile tile : row)
				{
					if (tile == null)
					{
						continue;
					}
					for (GameObject object : tile.getGameObjects())
					{
						track(object);
					}
				}
			}
		}
	}

	void clear()
	{
		patchObjects.clear();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showCompostReminders())
		{
			return null;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null;
		}

		WorldView playerWorldView = player.getWorldView();
		long now = System.currentTimeMillis() / 1000L;
		for (Map.Entry<FarmPatch, GameObject> entry : patchObjects.entrySet())
		{
			FarmPatch patch = entry.getKey();
			GameObject object = entry.getValue();
			if (!PatchSelection.isEnabled(config, patch) || object.getWorldView() != playerWorldView)
			{
				continue;
			}

			PatchState state = timeTrackingService.predict(patch).getEffectiveState(now);
			PatchHighlight highlight = PatchHighlight.forPatch(
				patch, state, timeTrackingService.hasRecordedCompost(patch));
			if (highlight == null)
			{
				continue;
			}

			renderReminder(graphics, object, iconFor(patch, highlight), colorFor(highlight),
				patch.getType() == PatchType.SEAWEED && highlight == PatchHighlight.EMPTY);
		}
		return null;
	}

	private Color colorFor(PatchHighlight highlight)
	{
		switch (highlight)
		{
			case READY:
				return config.readyPatchColor();
			case EMPTY:
				return config.emptyPatchColor();
			case DEAD:
				return config.deadPatchColor();
			case DISEASED:
				return config.diseasedPatchColor();
			case COMPOST:
			default:
				return config.compostReminderColor();
		}
	}

	private BufferedImage iconFor(FarmPatch patch, PatchHighlight highlight)
	{
		switch (highlight)
		{
			case READY:
				return magicSecateursIcon;
			case DEAD:
				return spadeIcon;
			case EMPTY:
				switch (patch.getType())
				{
					case TREE: return treeSaplingIcon;
					case FRUIT_TREE: return fruitTreeSaplingIcon;
					case HARDWOOD_TREE: return hardwoodSaplingIcon;
					case CALQUAT: return calquatSaplingIcon;
					case SEAWEED: return seaweedSporeIcon;
					case HERB: return herbSeedIcon;
					default: return null;
				}
			case COMPOST:
				return ultracompostIcon;
			case DISEASED:
				return plantCureIcon;
			default:
				return null;
		}
	}

	private void renderReminder(Graphics2D graphics, GameObject object, BufferedImage icon, Color color,
		boolean renderFootprint)
	{
		modelOutlineRenderer.drawOutline(object, OUTLINE_THICKNESS, color, OUTLINE_FEATHERING);
		if (renderFootprint)
		{
			Polygon footprint = object.getCanvasTilePoly();
			if (footprint != null)
			{
				OverlayUtil.renderPolygon(graphics, footprint, color, new Color(0, 0, 0, 0), EMPTY_PATCH_STROKE);
			}
		}

		if (icon != null)
		{
			java.awt.Shape clickbox = object.getClickbox();
			if (clickbox == null)
			{
				return;
			}

			Rectangle2D bounds = clickbox.getBounds2D();
			int iconX = (int) bounds.getCenterX() - icon.getWidth() / 2;
			int iconY = (int) bounds.getCenterY() - icon.getHeight() / 2;
			graphics.drawImage(icon, iconX, iconY, null);
		}
	}
}
