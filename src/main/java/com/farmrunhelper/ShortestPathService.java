package com.farmrunhelper;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;

final class ShortestPathService
{
	private static final String NAMESPACE = "shortestpath";
	private static final String PATH = "path";
	private static final String CLEAR = "clear";
	private static final String TARGET = "target";

	private final EventBus eventBus;

	@Inject
	ShortestPathService(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	void routeTo(WorldPoint target)
	{
		Map<String, Object> data = new HashMap<>();
		data.put(TARGET, target);
		eventBus.post(new PluginMessage(NAMESPACE, PATH, data));
	}

	void clear()
	{
		eventBus.post(new PluginMessage(NAMESPACE, CLEAR));
	}
}
