package com.farmrunhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
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
	private static final WorldPoint FARMING_GUILD_SNAPE_GRASS_TILE = new WorldPoint(1267, 3732, 0);
	private static final WorldPoint FARMING_GUILD_WATERMELON_TILE = new WorldPoint(1267, 3727, 0);

	private final Client client;
	private final FarmRunHelperConfig config;
	private final TimeTrackingService timeTrackingService;
	private final ModelOutlineRenderer modelOutlineRenderer;
	private final ItemManager itemManager;
	private final BufferedImage magicSecateursIcon;
	private final BufferedImage coinsIcon;
	private final BufferedImage spadeIcon;
	private final BufferedImage plantCureIcon;
	private final BufferedImage herbSeedIcon;
	private final BufferedImage hopsSeedIcon;
	private final BufferedImage treeSaplingIcon;
	private final BufferedImage fruitTreeSaplingIcon;
	private final BufferedImage hardwoodSaplingIcon;
	private final BufferedImage calquatSaplingIcon;
	private final BufferedImage seaweedSporeIcon;
	private final BufferedImage ultracompostIcon;
	private final Map<FarmPatch, GameObject> patchObjects = new EnumMap<>(FarmPatch.class);
	private final Map<FarmingGuildContractPatch, GameObject> contractPatchObjects =
		new EnumMap<>(FarmingGuildContractPatch.class);
	private final Map<Integer, BufferedImage> contractPlantIcons = new HashMap<>();

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
		this.itemManager = itemManager;
		this.magicSecateursIcon = itemManager.getImage(ItemID.FAIRY_ENCHANTED_SECATEURS);
		this.coinsIcon = itemManager.getImage(ItemID.COINS);
		this.spadeIcon = itemManager.getImage(ItemID.SPADE);
		this.plantCureIcon = itemManager.getImage(ItemID.PLANT_CURE);
		this.herbSeedIcon = itemManager.getImage(ItemID.GUAM_SEED);
		this.hopsSeedIcon = itemManager.getImage(ItemID.BARLEY_SEED);
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
				break;
			}
		}
		if (!FarmingContractPreplant.isDevelopmentEnabled())
		{
			return;
		}
		for (FarmingGuildContractPatch patch : FarmingGuildContractPatch.values())
		{
			if (patch.matchesSceneObject(definition.getVarbitId(), object.getWorldLocation()))
			{
				contractPatchObjects.put(patch, object);
				return;
			}
		}
	}

	void untrack(GameObject object)
	{
		patchObjects.values().removeIf(trackedObject -> trackedObject == object);
		contractPatchObjects.values().removeIf(trackedObject -> trackedObject == object);
	}

	void rebuild()
	{
		patchObjects.clear();
		contractPatchObjects.clear();
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
		contractPatchObjects.clear();
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

		renderContractPreplantReminders(graphics, playerWorldView);
		return null;
	}

	private void renderContractPreplantReminders(Graphics2D graphics, WorldView playerWorldView)
	{
		if (!FarmingContractPreplant.isDevelopmentEnabled())
		{
			return;
		}
		long now = System.currentTimeMillis() / 1000L;
		for (FarmingContractPreplant.Crop crop : FarmingContractPreplant.crops(config))
		{
			GameObject object = contractPatchObjects.get(crop.getPatch());
			if (object != null && object.getWorldView() == playerWorldView
				&& crop.needsPreplant(timeTrackingService, now))
			{
				renderPreplantReminder(graphics, object, iconFor(crop), crop);
			}
		}
	}

	private BufferedImage iconFor(FarmingContractPreplant.Crop crop)
	{
		return contractPlantIcons.computeIfAbsent(crop.getItemId(), itemManager::getImage);
	}

	private void renderPreplantReminder(
		Graphics2D graphics,
		GameObject object,
		BufferedImage icon,
		FarmingContractPreplant.Crop crop)
	{
		Color color = config.farmingContractPreplantColor();
		modelOutlineRenderer.drawOutline(object, OUTLINE_THICKNESS, color, OUTLINE_FEATHERING);
		if (icon == null)
		{
			return;
		}
		Shape clickbox = object.getClickbox();
		if (clickbox == null)
		{
			return;
		}

		Rectangle2D bounds = clickbox.getBounds2D();
		int iconSize = crop.isAllotment() ? 40 : icon.getWidth();
		Point2D iconCenter = fixedAllotmentIconCenter(crop, object.getWorldView());
		if (iconCenter == null)
		{
			iconCenter = crop.isAllotment()
				? allotmentIconCenter(clickbox, iconSize)
				: new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		}
		int iconX = (int) Math.round(iconCenter.getX() - iconSize / 2.0);
		int iconY = (int) Math.round(iconCenter.getY() - iconSize / 2.0);
		graphics.drawImage(icon, iconX, iconY, iconSize, iconSize, null);
	}

	private Point2D fixedAllotmentIconCenter(FarmingContractPreplant.Crop crop, WorldView worldView)
	{
		WorldPoint tile;
		if (crop.getItemId() == ItemID.SNAPE_GRASS)
		{
			tile = FARMING_GUILD_SNAPE_GRASS_TILE;
		}
		else if (crop.getItemId() == ItemID.WATERMELON)
		{
			tile = FARMING_GUILD_WATERMELON_TILE;
		}
		else
		{
			return null;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(worldView, tile);
		if (localPoint == null)
		{
			return null;
		}

		Polygon canvasTile = Perspective.getCanvasTilePoly(client, localPoint);
		if (canvasTile == null)
		{
			return null;
		}
		Rectangle2D bounds = canvasTile.getBounds2D();
		return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
	}

	static Point2D allotmentIconCenter(Shape shape, int iconSize)
	{
		List<Point2D> points = polygonPoints(shape);
		if (points.size() < 3)
		{
			Rectangle2D bounds = shape.getBounds2D();
			return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		}

		double signedArea = signedArea(points);
		Point2D innerCorner = null;
		double strongestTurn = 0;
		for (int index = 0; index < points.size(); index++)
		{
			Point2D previous = points.get((index + points.size() - 1) % points.size());
			Point2D current = points.get(index);
			Point2D next = points.get((index + 1) % points.size());
			double cross = cross(previous, current, next);
			if (cross * signedArea < 0 && Math.abs(cross) > strongestTurn)
			{
				innerCorner = current;
				strongestTurn = Math.abs(cross);
			}
		}

		if (innerCorner == null)
		{
			Rectangle2D bounds = shape.getBounds2D();
			return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		}

		// The concave vertex is the actual inside corner of an L-shaped allotment.
		// Centering the icon here makes the placement track the patch geometry as the
		// camera moves, rather than choosing a corner of the clickbox's bounding box.
		return innerCorner;
	}

	private static List<Point2D> polygonPoints(Shape shape)
	{
		List<Point2D> points = new ArrayList<>();
		PathIterator iterator = shape.getPathIterator(null, 1.0);
		double[] coordinates = new double[6];
		while (!iterator.isDone())
		{
			int segment = iterator.currentSegment(coordinates);
			if (segment == PathIterator.SEG_MOVETO || segment == PathIterator.SEG_LINETO)
			{
				points.add(new Point2D.Double(coordinates[0], coordinates[1]));
			}
			iterator.next();
		}
		return points;
	}

	private static double signedArea(List<Point2D> points)
	{
		double area = 0;
		for (int index = 0; index < points.size(); index++)
		{
			Point2D current = points.get(index);
			Point2D next = points.get((index + 1) % points.size());
			area += current.getX() * next.getY() - next.getX() * current.getY();
		}
		return area / 2.0;
	}

	private static double cross(Point2D previous, Point2D current, Point2D next)
	{
		return (current.getX() - previous.getX()) * (next.getY() - current.getY())
			- (current.getY() - previous.getY()) * (next.getX() - current.getX());
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
				return patch.getType().usesTreeReadyAction() && config.treeReadyAction() == TreeReadyAction.COINS
					? coinsIcon
					: magicSecateursIcon;
			case DEAD:
				return spadeIcon;
			case EMPTY:
				switch (patch.getType())
				{
					case HOPS: return hopsSeedIcon;
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
