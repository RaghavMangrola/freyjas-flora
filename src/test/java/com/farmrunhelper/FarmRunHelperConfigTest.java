package com.farmrunhelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FarmRunHelperConfigTest
{
	@Test
	public void placesSevenCollapsedPatchTypeSectionsAfterPrimarySettings() throws Exception
	{
		String[] fields = {
			"HERB_PATCHES",
			"TREE_PATCHES",
			"FRUIT_TREE_PATCHES",
			"HARDWOOD_TREE_PATCHES",
			"CALQUAT_PATCHES",
			"CORAL_PATCHES",
			"SEAWEED_PATCHES"
		};
		String[] names = {
			"Herbs",
			"Trees",
			"Fruit trees",
			"Hardwood trees",
			"Calquat trees",
			"Coral",
			"Seaweed"
		};

		for (int index = 0; index < fields.length; index++)
		{
			Field field = FarmRunHelperConfig.class.getField(fields[index]);
			ConfigSection section = field.getAnnotation(ConfigSection.class);
			assertNotNull(section);
			assertEquals(names[index], section.name());
			assertEquals(index + 2, section.position());
			assertTrue(section.closedByDefault());
		}
	}

	@Test
	public void keepsRunBehaviorProminentAndSecondarySettingsCollapsed() throws Exception
	{
		ConfigSection runBehavior = FarmRunHelperConfig.class.getField("RUN_PLANNING")
			.getAnnotation(ConfigSection.class);
		ConfigSection highlights = FarmRunHelperConfig.class.getField("COMPOST_REMINDERS")
			.getAnnotation(ConfigSection.class);
		ConfigSection shortestPath = FarmRunHelperConfig.class.getField("NAVIGATION")
			.getAnnotation(ConfigSection.class);

		assertEquals("Run behavior", runBehavior.name());
		assertEquals(0, runBehavior.position());
		assertFalse(runBehavior.closedByDefault());
		assertEquals("Scene highlights", highlights.name());
		assertEquals(1, highlights.position());
		assertTrue(highlights.closedByDefault());
		assertEquals("Shortest Path", shortestPath.name());
		assertEquals(9, shortestPath.position());
		assertTrue(shortestPath.closedByDefault());
	}

	@Test
	public void assignsMasterAndPatchTogglesToTheirPatchTypeSections() throws Exception
	{
		Map<String, String[]> expected = new LinkedHashMap<>();
		expected.put(FarmRunHelperConfig.HERB_PATCHES, new String[]{
			"showHerbs", "enableArdougne", "enableCatherby", "enableCivitas", "enableFalador",
			"enableFarmingGuild", "enableHarmony", "enableHosidius", "enableMorytania",
			"enableTrollStronghold", "enableWeiss"
		});
		expected.put(FarmRunHelperConfig.TREE_PATCHES, new String[]{
			"showTrees", "enableAuburnvaleTree", "enableFaladorTree", "enableFarmingGuildTree",
			"enableGnomeStrongholdTree", "enableLumbridgeTree", "enableTaverleyTree", "enableVarrockTree"
		});
		expected.put(FarmRunHelperConfig.FRUIT_TREE_PATCHES, new String[]{
			"showFruitTrees", "enableBrimhavenFruitTree", "enableCatherbyFruitTree",
			"enableFarmingGuildFruitTree", "enableGnomeStrongholdFruitTree", "enableKastoriFruitTree",
			"enableLletyaFruitTree", "enableGnomeVillageFruitTree"
		});
		expected.put(FarmRunHelperConfig.HARDWOOD_TREE_PATCHES, new String[]{
			"showHardwoodTrees", "enableAnglersRetreatHardwood", "enableAviumSavannahHardwood",
			"enableFossilIslandEastHardwood", "enableFossilIslandMiddleHardwood",
			"enableFossilIslandWestHardwood"
		});
		expected.put(FarmRunHelperConfig.CALQUAT_PATCHES, new String[]{
			"showCalquatTrees", "enableGreatConchCalquat", "enableKastoriCalquat",
			"enableTaiBwoWannaiCalquat"
		});
		expected.put(FarmRunHelperConfig.CORAL_PATCHES, new String[]{
			"showCoral", "enableCoralEast", "enableCoralWest"
		});
		expected.put(FarmRunHelperConfig.SEAWEED_PATCHES, new String[]{
			"showSeaweed", "enableSeaweedNorth", "enableSeaweedSouth"
		});

		for (Map.Entry<String, String[]> entry : expected.entrySet())
		{
			String[] methods = entry.getValue();
			for (int position = 0; position < methods.length; position++)
			{
				Method method = FarmRunHelperConfig.class.getMethod(methods[position]);
				ConfigItem item = method.getAnnotation(ConfigItem.class);
				assertNotNull(item);
				assertEquals(entry.getKey(), item.section());
				assertEquals(position, item.position());
				assertEquals(methods[position], item.keyName());
			}

			ConfigItem master = FarmRunHelperConfig.class.getMethod(methods[0]).getAnnotation(ConfigItem.class);
			assertTrue(master.name().startsWith("Include "));
		}

		for (Method method : FarmRunHelperConfig.class.getDeclaredMethods())
		{
			ConfigItem item = method.getAnnotation(ConfigItem.class);
			if (item != null)
			{
				assertFalse(Arrays.asList("patchTypes", "enabledPatches").contains(item.section()));
			}
		}
	}

	@Test
	public void keepsPatchTypeOrderAsHiddenProfileConfiguration() throws Exception
	{
		ConfigItem item = FarmRunHelperConfig.class
			.getMethod("patchTypeOrder")
			.getAnnotation(ConfigItem.class);

		assertNotNull(item);
		assertEquals(FarmRunHelperConfig.PATCH_TYPE_ORDER_KEY, item.keyName());
		assertTrue(item.hidden());
		assertEquals(PatchTypeOrder.DEFAULT_SERIALIZED, new FarmRunHelperConfig() { }.patchTypeOrder());
	}

	@Test
	public void usesTheSharedDistinctSceneHighlightPalette()
	{
		FarmRunHelperConfig config = new FarmRunHelperConfig() { };
		assertEquals(PatchMasterTheme.COMPOST, config.compostReminderColor());
		assertEquals(PatchMasterTheme.READY, config.readyPatchColor());
		assertEquals(PatchMasterTheme.EMPTY, config.emptyPatchColor());
		assertEquals(PatchMasterTheme.DEAD, config.deadPatchColor());
		assertEquals(PatchMasterTheme.DISEASED, config.diseasedPatchColor());
	}
}
