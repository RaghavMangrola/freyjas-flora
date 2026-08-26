package com.farmrunhelper;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Freyja’s Flora",
	description = "Tracks farming patches and birdhouses and sends visual destinations to Shortest Path",
	tags = {"farming", "herb", "hops", "tree", "fruit", "hardwood", "calquat", "coral", "seaweed", "birdhouse", "hunter", "timer", "route", "path"},
	enabledByDefault = false
)
@PluginDependency(TimeTrackingPlugin.class)
public class FarmRunHelperPlugin extends Plugin implements FarmRunHelperPanel.Listener
{
	private static final int PANEL_REFRESH_INTERVAL_MS = 30_000;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private FarmRunHelperConfig config;

	@Inject
	private FarmRunHelperPanel panel;

	@Inject
	private TimeTrackingService timeTrackingService;

	@Inject
	private ShortestPathService shortestPathService;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CompostReminderOverlay compostReminderOverlay;

	@Inject
	private SeedInventoryService seedInventoryService;

	private final FarmRunProgress runProgress = new FarmRunProgress();
	private NavigationButton navigationButton;
	private FarmPatch currentPatch;
	private PatchState currentPatchState;
	private boolean currentPatchReplanted;
	private WorldPoint lastNavigationTarget;
	private boolean runActive;
	private Timer panelRefreshTimer;

	@Provides
	FarmRunHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FarmRunHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		seedInventoryService.resetProfile();
		normalizePatchTypeOrderConfiguration();
		panel.setListener(this);
		addNavigation(FarmRunIcon.fallback());
		FarmRunIcon.loadFarmersShirt(itemManager, 16, icon -> SwingUtilities.invokeLater(() ->
		{
			if (navigationButton != null)
			{
				clientToolbar.removeNavigation(navigationButton);
				addNavigation(icon);
				clientToolbar.addNavigation(navigationButton);
			}
		}));
		clientToolbar.addNavigation(navigationButton);
		overlayManager.add(compostReminderOverlay);
		panelRefreshTimer = new Timer(PANEL_REFRESH_INTERVAL_MS, event ->
			clientThread.invokeLater(() ->
			{
				if (panelRefreshTimer != null)
				{
					refreshPanel();
				}
			}));
		panelRefreshTimer.setCoalesce(true);
		panelRefreshTimer.start();
		clientThread.invokeLater(() ->
		{
			compostReminderOverlay.rebuild();
			refreshPanel();
		});
	}

	private void addNavigation(java.awt.image.BufferedImage icon)
	{
		navigationButton = NavigationButton.builder()
			.tooltip("Freyja’s Flora")
			.icon(icon)
			.panel(panel)
			.priority(5)
			.build();
	}

	@Override
	protected void shutDown()
	{
		if (panelRefreshTimer != null)
		{
			panelRefreshTimer.stop();
			panelRefreshTimer = null;
		}
		if (config.clearPathOnShutdown() && lastNavigationTarget != null)
		{
			shortestPathService.clear();
		}
		currentPatch = null;
		currentPatchState = null;
		currentPatchReplanted = false;
		lastNavigationTarget = null;
		runProgress.clear();
		runActive = false;
		overlayManager.remove(compostReminderOverlay);
		compostReminderOverlay.clear();
		panel.setListener(null);
		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
			navigationButton = null;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		seedInventoryService.updateOpenStorages();
		updateInteriorRoute();
		checkForCompletedPatch();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (FarmRunHelperConfig.GROUP.equals(event.getGroup())
			&& FarmRunHelperConfig.PATCH_TYPE_ORDER_KEY.equals(event.getKey()))
		{
			normalizePatchTypeOrderConfiguration();
		}

		if (FarmRunHelperConfig.GROUP.equals(event.getGroup())
			|| TimeTrackingConfig.CONFIG_GROUP.equals(event.getGroup()))
		{
			clientThread.invoke(this::refreshPanel);
		}
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		seedInventoryService.resetProfile();
		currentPatch = null;
		currentPatchState = null;
		currentPatchReplanted = false;
		lastNavigationTarget = null;
		runProgress.clear();
		runActive = false;
		normalizePatchTypeOrderConfiguration();
		refreshPanel();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			compostReminderOverlay.clear();
		}
		if (event.getGameState() == GameState.LOGGED_IN || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			if (event.getGameState() == GameState.LOGGED_IN)
			{
				compostReminderOverlay.rebuild();
			}
			refreshPanel();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		compostReminderOverlay.track(event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		compostReminderOverlay.untrack(event.getGameObject());
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int containerId = event.getContainerId() > 0x8000
			? event.getContainerId() - 0x8000
			: event.getContainerId();
		if (containerId == InventoryID.BANK || containerId == InventoryID.SEED_VAULT)
		{
			seedInventoryService.onItemContainerChanged(event);
		}
		if (containerId == InventoryID.INV
			|| containerId == InventoryID.BANK
			|| containerId == InventoryID.SEED_VAULT)
		{
			clientThread.invokeLater(this::refreshPanel);
		}
	}

	@Override
	public void onStartRun()
	{
		clientThread.invokeLater(() ->
		{
			runActive = true;
			runProgress.start(getVisibleSnapshots());
			routeCurrentPatch();
		});
	}

	@Override
	public void onNextPatch()
	{
		clientThread.invokeLater(() ->
		{
			runActive = true;
			runProgress.advance(getVisibleSnapshots());
			routeCurrentPatch();
		});
	}

	@Override
	public void onClearRoute()
	{
		clientThread.invokeLater(() ->
		{
			shortestPathService.clear();
			currentPatch = null;
			currentPatchState = null;
			currentPatchReplanted = false;
			lastNavigationTarget = null;
			runProgress.clear();
			runActive = false;
			refreshPanel();
		});
	}

	@Override
	public void onDoneToggled(FarmPatch patch)
	{
		clientThread.invokeLater(() ->
		{
			if (!runProgress.toggleDone(patch, getVisibleSnapshots()))
			{
				return;
			}
			runActive = true;
			if (currentPatch == null)
			{
				routeCurrentPatch();
			}
			else
			{
				refreshPanel();
			}
		});
	}

	@Override
	public void onRouteTo(FarmPatch patch)
	{
		clientThread.invokeLater(() ->
		{
			runActive = false;
			routeToPatch(patch);
		});
	}

	@Override
	public void onPatchTypeOrderChanged(List<PatchType> order)
	{
		configManager.setConfiguration(
			FarmRunHelperConfig.GROUP,
			FarmRunHelperConfig.PATCH_TYPE_ORDER_KEY,
			PatchTypeOrder.serialize(order));
	}

	private void routeNextPatch()
	{
		runProgress.advance(getVisibleSnapshots());
		routeCurrentPatch();
	}

	private void routeCurrentPatch()
	{
		FarmPatch nextPatch = runProgress.getCurrentPatch();
		if (nextPatch == null)
		{
			currentPatch = null;
			currentPatchState = null;
			currentPatchReplanted = false;
			refreshPanel();
			return;
		}
		routeToPatch(nextPatch);
	}

	private void routeToPatch(FarmPatch patch)
	{
		runProgress.select(patch);
		currentPatch = patch;
		Player localPlayer = client.getLocalPlayer();
		if (client.getGameState() != GameState.LOGGED_IN || localPlayer == null)
		{
			refreshPanel();
			return;
		}

		currentPatchState = timeTrackingService.predict(patch).getEffectiveState(Instant.now().getEpochSecond());
		currentPatchReplanted = false;
		WorldPoint playerLocation = localPlayer.getWorldLocation();
		lastNavigationTarget = patch.getNavigationTarget(playerLocation);
		shortestPathService.routeTo(lastNavigationTarget);
		refreshPanel();
	}

	private void updateInteriorRoute()
	{
		if (currentPatch == null || client.getLocalPlayer() == null)
		{
			return;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		WorldPoint desiredTarget = currentPatch.getNavigationTarget(playerLocation);
		if (!desiredTarget.equals(lastNavigationTarget))
		{
			lastNavigationTarget = desiredTarget;
			shortestPathService.routeTo(desiredTarget);
		}
	}

	private void checkForCompletedPatch()
	{
		if (!runActive || !config.autoAdvance() || currentPatch == null)
		{
			return;
		}

		PatchState observedState = timeTrackingService.predict(currentPatch)
			.getEffectiveState(Instant.now().getEpochSecond());
		if (currentPatchState != null && PatchCompletion.wasReplanted(currentPatchState, observedState))
		{
			currentPatchReplanted = true;
		}

		if (currentPatchReplanted && canAdvanceFromCurrentPatch())
		{
			routeNextPatch();
			return;
		}

		currentPatchState = observedState;
	}

	private boolean canAdvanceFromCurrentPatch()
	{
		return !config.waitForCompostBeforeAutoAdvance()
			|| !currentPatch.supportsCompostReminder()
			|| timeTrackingService.hasRecordedCompost(currentPatch);
	}

	private void refreshPanel()
	{
		panel.update(
			getVisibleSnapshots(),
			PatchTypeOrder.parse(config.patchTypeOrder()),
			currentPatch,
			runProgress.getDonePatches(),
			Instant.now().getEpochSecond(),
			seedInventoryService.getInventory());
	}

	private List<PatchSnapshot> getVisibleSnapshots()
	{
		List<PatchSnapshot> snapshots = new ArrayList<>();
		for (FarmPatch patch : PatchTypeOrder.orderedEnabledPatches(config.patchTypeOrder(), config))
		{
			snapshots.add(new PatchSnapshot(
				patch,
				timeTrackingService.predict(patch),
				timeTrackingService.getRecordedCompost(patch)));
		}
		return snapshots;
	}

	private void normalizePatchTypeOrderConfiguration()
	{
		String currentOrder = config.patchTypeOrder();
		String normalizedOrder = PatchTypeOrder.serialize(PatchTypeOrder.parse(currentOrder));
		if (!normalizedOrder.equals(currentOrder))
		{
			configManager.setConfiguration(
				FarmRunHelperConfig.GROUP,
				FarmRunHelperConfig.PATCH_TYPE_ORDER_KEY,
				normalizedOrder);
		}
	}

}
