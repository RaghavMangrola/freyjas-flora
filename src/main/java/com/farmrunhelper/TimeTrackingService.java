package com.farmrunhelper;

import java.util.EnumSet;
import java.util.Optional;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.WorldType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.farming.CompostState;

final class TimeTrackingService
{
	static final int BIRD_HOUSE_DURATION_SECONDS = 50 * 60;

	private final Client client;
	private final ConfigManager configManager;

	@Inject
	TimeTrackingService(Client client, ConfigManager configManager)
	{
		this.client = client;
		this.configManager = configManager;
	}

	PatchPrediction predict(FarmPatch patch)
	{
		String stored = configManager.getRSProfileConfiguration(
			TimeTrackingConfig.CONFIG_GROUP,
			patch.getTimeTrackingKey());
		Optional<TimeTrackingValue> parsed = TimeTrackingValue.parse(stored);
		if (!parsed.isPresent())
		{
			return PatchPrediction.unknown();
		}

		TimeTrackingValue value = parsed.get();
		DecodedPatchState decoded = PatchStateDecoder.decode(patch.getType(), value.getVarbitValue());
		if (decoded == null)
		{
			return PatchPrediction.unknown();
		}

		int tickRate = decoded.getTickRateMinutes();
		if (tickRate > 0 && isLeaguesWorld())
		{
			tickRate = Math.max(1, tickRate / 5);
		}

		Integer offsetPrecision = configManager.getRSProfileConfiguration(
			TimeTrackingConfig.CONFIG_GROUP,
			TimeTrackingConfig.FARM_TICK_OFFSET_PRECISION,
			int.class);
		Integer offset = configManager.getRSProfileConfiguration(
			TimeTrackingConfig.CONFIG_GROUP,
			TimeTrackingConfig.FARM_TICK_OFFSET,
			int.class);

		long readyAt = 0;
		if (decoded.getState() == PatchState.GROWING)
		{
			readyAt = patch.getType() == PatchType.BIRD_HOUSE
				? birdHouseReadyAt(value.getObservedAt())
				: FarmingClock.readyAt(
					tickRate,
					decoded.getStage(),
					decoded.getStages(),
					value.getObservedAt(),
					offsetPrecision,
					offset);
		}

		return PatchPrediction.known(
			decoded.getCropName(),
			decoded.getState(),
			value.getObservedAt(),
			readyAt);
	}

	static long birdHouseReadyAt(long observedAt)
	{
		return observedAt + BIRD_HOUSE_DURATION_SECONDS;
	}

	boolean hasRecordedCompost(FarmPatch patch)
	{
		return getRecordedCompost(patch) != null;
	}

	CompostState getRecordedCompost(FarmPatch patch)
	{
		if (!patch.supportsCompostReminder())
		{
			return null;
		}
		return configManager.getRSProfileConfiguration(
			TimeTrackingConfig.CONFIG_GROUP,
			patch.getTimeTrackingKey() + "." + TimeTrackingConfig.COMPOST,
			CompostState.class);
	}

	private boolean isLeaguesWorld()
	{
		EnumSet<WorldType> worldTypes = client.getWorldType();
		return worldTypes.contains(WorldType.SEASONAL) && !worldTypes.contains(WorldType.DEADMAN);
	}
}
