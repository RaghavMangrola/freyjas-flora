package com.farmrunhelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;

final class SeedInventoryService
{
	private static final String BANK_SNAPSHOT_KEY = "seedStock.bank";
	private static final String SEED_VAULT_SNAPSHOT_KEY = "seedStock.seedVault";

	private static final Set<String> HERB_SEED_NAMES = new HashSet<>(Arrays.asList(
		"Guam seed", "Marrentill seed", "Tarromin seed", "Harralander seed", "Ranarr seed",
		"Toadflax seed", "Irit seed", "Avantoe seed", "Huasca seed", "Kwuarm seed",
		"Snapdragon seed", "Cadantine seed", "Lantadyme seed", "Dwarf weed seed", "Torstol seed"));
	private static final Set<String> TREE_SEED_NAMES = new HashSet<>(Arrays.asList(
		"Acorn", "Willow seed", "Maple seed", "Yew seed", "Magic seed"));
	private static final Set<String> FRUIT_TREE_SEED_NAMES = new HashSet<>(Arrays.asList(
		"Apple tree seed", "Banana tree seed", "Orange tree seed", "Curry tree seed",
		"Pineapple seed", "Papaya tree seed", "Palm tree seed", "Dragonfruit tree seed"));
	private static final Set<String> HARDWOOD_SEED_NAMES = new HashSet<>(Arrays.asList(
		"Teak seed", "Mahogany seed", "Camphor seed", "Ironwood seed", "Rosewood seed"));
	private static final Set<Integer> TREE_SAPLING_IDS = new HashSet<>(Arrays.asList(
		ItemID.PLANTPOT_OAK_SAPLING,
		ItemID.PLANTPOT_WILLOW_SAPLING,
		ItemID.PLANTPOT_MAPLE_SAPLING,
		ItemID.PLANTPOT_YEW_SAPLING,
		ItemID.PLANTPOT_MAGIC_TREE_SAPLING));
	private static final Set<Integer> FRUIT_TREE_SAPLING_IDS = new HashSet<>(Arrays.asList(
		ItemID.PLANTPOT_APPLE_SAPLING,
		ItemID.PLANTPOT_BANANA_SAPLING,
		ItemID.PLANTPOT_ORANGE_SAPLING,
		ItemID.PLANTPOT_CURRY_SAPLING,
		ItemID.PLANTPOT_PINEAPPLE_SAPLING,
		ItemID.PLANTPOT_PAPAYA_SAPLING,
		ItemID.PLANTPOT_PALM_SAPLING,
		ItemID.PLANTPOT_DRAGONFRUIT_SAPLING));
	private static final Set<Integer> HARDWOOD_SAPLING_IDS = new HashSet<>(Arrays.asList(
		ItemID.PLANTPOT_TEAK_SAPLING,
		ItemID.PLANTPOT_MAHOGANY_SAPLING,
		ItemID.PLANTPOT_CAMPHOR_SAPLING,
		ItemID.PLANTPOT_IRONWOOD_SAPLING,
		ItemID.PLANTPOT_ROSEWOOD_SAPLING));

	private final Client client;
	private final ConfigManager configManager;
	private String loadedProfileKey;
	private boolean profileLoaded;
	private SeedStorageSnapshot bankSnapshot = SeedStorageSnapshot.unknown();
	private SeedStorageSnapshot seedVaultSnapshot = SeedStorageSnapshot.unknown();

	@Inject
	SeedInventoryService(Client client, ConfigManager configManager)
	{
		this.client = client;
		this.configManager = configManager;
	}

	void resetProfile()
	{
		loadedProfileKey = null;
		profileLoaded = false;
		bankSnapshot = SeedStorageSnapshot.unknown();
		seedVaultSnapshot = SeedStorageSnapshot.unknown();
		ensureProfileLoaded();
	}

	void updateOpenStorages()
	{
		observeContainer(InventoryID.BANK, client.getItemContainer(InventoryID.BANK));
		observeContainer(InventoryID.SEED_VAULT, client.getItemContainer(InventoryID.SEED_VAULT));
	}

	boolean onItemContainerChanged(ItemContainerChanged event)
	{
		return observeContainer(event.getContainerId(), event.getItemContainer());
	}

	SeedInventory getInventory()
	{
		ensureProfileLoaded();
		SeedInventory.Builder inventory = SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, System.currentTimeMillis());
		addContainer(inventory, SeedInventory.Location.INVENTORY, client.getItemContainer(InventoryID.INV));
		addSnapshot(inventory, SeedInventory.Location.BANK, bankSnapshot);
		addSnapshot(inventory, SeedInventory.Location.SEED_VAULT, seedVaultSnapshot);
		return inventory.build();
	}

	private boolean observeContainer(int rawContainerId, ItemContainer itemContainer)
	{
		int containerId = normalizeContainerId(rawContainerId);
		if ((containerId != InventoryID.BANK && containerId != InventoryID.SEED_VAULT)
			|| itemContainer == null)
		{
			return false;
		}

		ensureProfileLoaded();
		if (loadedProfileKey == null)
		{
			return false;
		}

		Map<Integer, Integer> items = collectTrackedItems(itemContainer);
		SeedStorageSnapshot previous = containerId == InventoryID.BANK ? bankSnapshot : seedVaultSnapshot;
		if (previous.isObserved() && previous.hasSameItems(items))
		{
			return false;
		}

		SeedStorageSnapshot snapshot = SeedStorageSnapshot.observed(System.currentTimeMillis(), items);
		if (containerId == InventoryID.BANK)
		{
			bankSnapshot = snapshot;
			configManager.setRSProfileConfiguration(
				FarmRunHelperConfig.GROUP,
				BANK_SNAPSHOT_KEY,
				snapshot.serialize());
		}
		else
		{
			seedVaultSnapshot = snapshot;
			configManager.setRSProfileConfiguration(
				FarmRunHelperConfig.GROUP,
				SEED_VAULT_SNAPSHOT_KEY,
				snapshot.serialize());
		}
		return true;
	}

	private void ensureProfileLoaded()
	{
		String currentProfileKey = configManager.getRSProfileKey();
		if (profileLoaded && Objects.equals(loadedProfileKey, currentProfileKey))
		{
			return;
		}

		loadedProfileKey = currentProfileKey;
		profileLoaded = true;
		bankSnapshot = SeedStorageSnapshot.unknown();
		seedVaultSnapshot = SeedStorageSnapshot.unknown();
		if (currentProfileKey != null)
		{
			bankSnapshot = SeedStorageSnapshot.parse(
				configManager.getRSProfileConfiguration(FarmRunHelperConfig.GROUP, BANK_SNAPSHOT_KEY));
			seedVaultSnapshot = SeedStorageSnapshot.parse(
				configManager.getRSProfileConfiguration(FarmRunHelperConfig.GROUP, SEED_VAULT_SNAPSHOT_KEY));
		}
	}

	private Map<Integer, Integer> collectTrackedItems(ItemContainer itemContainer)
	{
		Map<Integer, Integer> items = new TreeMap<>();
		for (Item item : itemContainer.getItems())
		{
			TrackedItem tracked = getTrackedItem(item.getId());
			if (tracked != null && item.getQuantity() > 0)
			{
				items.merge(item.getId(), item.getQuantity(), SeedInventoryService::saturatedAdd);
			}
		}
		return items;
	}

	private void addContainer(
		SeedInventory.Builder inventory,
		SeedInventory.Location location,
		ItemContainer itemContainer)
	{
		if (itemContainer == null)
		{
			return;
		}
		for (Item item : itemContainer.getItems())
		{
			addItem(inventory, location, item.getId(), item.getQuantity());
		}
	}

	private void addSnapshot(
		SeedInventory.Builder inventory,
		SeedInventory.Location location,
		SeedStorageSnapshot snapshot)
	{
		if (!snapshot.isObserved())
		{
			return;
		}
		inventory.markObserved(location, snapshot.getUpdatedAtMs());
		for (Map.Entry<Integer, Integer> item : snapshot.getItemQuantities().entrySet())
		{
			addItem(inventory, location, item.getKey(), item.getValue());
		}
	}

	private void addItem(
		SeedInventory.Builder inventory,
		SeedInventory.Location location,
		int itemId,
		int quantity)
	{
		if (quantity <= 0)
		{
			return;
		}
		TrackedItem tracked = getTrackedItem(itemId);
		if (tracked != null)
		{
			inventory.add(location, tracked.patchType, tracked.directlyPlantable, tracked.name, quantity);
		}
	}

	private TrackedItem getTrackedItem(int itemId)
	{
		if (itemId < 0)
		{
			return null;
		}

		ItemComposition definition = client.getItemDefinition(itemId);
		if (definition == null || definition.getPlaceholderTemplateId() != -1)
		{
			return null;
		}

		String name = definition.getName();
		PatchType patchType = null;
		boolean directlyPlantable = false;
		if (HERB_SEED_NAMES.contains(name))
		{
			patchType = PatchType.HERB;
			directlyPlantable = true;
		}
		else if (TREE_SAPLING_IDS.contains(itemId))
		{
			patchType = PatchType.TREE;
			directlyPlantable = true;
		}
		else if (FRUIT_TREE_SAPLING_IDS.contains(itemId))
		{
			patchType = PatchType.FRUIT_TREE;
			directlyPlantable = true;
		}
		else if (HARDWOOD_SAPLING_IDS.contains(itemId))
		{
			patchType = PatchType.HARDWOOD_TREE;
			directlyPlantable = true;
		}
		else if (itemId == ItemID.PLANTPOT_CALQUAT_SAPLING)
		{
			patchType = PatchType.CALQUAT;
			directlyPlantable = true;
		}
		else if ("Seaweed spore".equals(name))
		{
			patchType = PatchType.SEAWEED;
			directlyPlantable = true;
		}
		else if (TREE_SEED_NAMES.contains(name))
		{
			patchType = PatchType.TREE;
		}
		else if (FRUIT_TREE_SEED_NAMES.contains(name))
		{
			patchType = PatchType.FRUIT_TREE;
		}
		else if (HARDWOOD_SEED_NAMES.contains(name))
		{
			patchType = PatchType.HARDWOOD_TREE;
		}
		else if ("Calquat tree seed".equals(name))
		{
			patchType = PatchType.CALQUAT;
		}

		String lowerName = name.toLowerCase(Locale.ROOT);
		boolean isSeedOrSapling = lowerName.endsWith(" seed")
			|| lowerName.endsWith(" sapling")
			|| lowerName.endsWith(" spore")
			|| lowerName.endsWith("acorn");
		return isSeedOrSapling ? new TrackedItem(name, patchType, directlyPlantable) : null;
	}

	private static int normalizeContainerId(int containerId)
	{
		return containerId > 0x8000 ? containerId - 0x8000 : containerId;
	}

	private static int saturatedAdd(int left, int right)
	{
		return left > Integer.MAX_VALUE - right ? Integer.MAX_VALUE : left + right;
	}

	private static final class TrackedItem
	{
		private final String name;
		private final PatchType patchType;
		private final boolean directlyPlantable;

		private TrackedItem(String name, PatchType patchType, boolean directlyPlantable)
		{
			this.name = name;
			this.patchType = patchType;
			this.directlyPlantable = directlyPlantable;
		}
	}
}
