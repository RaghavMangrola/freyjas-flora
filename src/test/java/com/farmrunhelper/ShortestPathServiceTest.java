package com.farmrunhelper;

import java.util.concurrent.atomic.AtomicReference;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortestPathServiceTest
{
	@Test
	public void postsPathMessageUsingShortestPathProtocol()
	{
		EventBus eventBus = new EventBus();
		AtomicReference<PluginMessage> posted = new AtomicReference<>();
		eventBus.register(PluginMessage.class, posted::set, 0);
		ShortestPathService service = new ShortestPathService(eventBus);

		WorldPoint target = new WorldPoint(2663, 3375, 0);
		service.routeTo(target);

		assertEquals("shortestpath", posted.get().getNamespace());
		assertEquals("path", posted.get().getName());
		assertEquals(target, posted.get().getData().get("target"));
	}

	@Test
	public void postsClearMessageUsingShortestPathProtocol()
	{
		EventBus eventBus = new EventBus();
		AtomicReference<PluginMessage> posted = new AtomicReference<>();
		eventBus.register(PluginMessage.class, posted::set, 0);
		ShortestPathService service = new ShortestPathService(eventBus);

		service.clear();

		assertEquals("shortestpath", posted.get().getNamespace());
		assertEquals("clear", posted.get().getName());
	}
}
