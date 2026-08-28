package com.farmrunhelper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.plugins.timetracking.farming.CompostState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FarmRunHelperPanelTest
{
	@Test
	public void orderEditorMovesResetsAndRestoresThePatchList() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		AtomicReference<List<PatchType>> savedOrder = new AtomicReference<>();
		panel.setListener(new FarmRunHelperPanel.Listener()
		{
			@Override
			public void onStartRun()
			{
			}

			@Override
			public void onNextPatch()
			{
			}

			@Override
			public void onClearRoute()
			{
			}

			@Override
			public void onRouteTo(FarmPatch patch)
			{
			}

			@Override
			public void onDoneToggled(FarmPatch patch)
			{
			}

			@Override
			public void onPatchTypeOrderChanged(List<PatchType> order)
			{
				savedOrder.set(order);
			}
		});

		List<PatchType> initialOrder = Arrays.asList(
			PatchType.HERB,
			PatchType.SEAWEED,
			PatchType.HOPS,
			PatchType.TREE,
			PatchType.FRUIT_TREE,
			PatchType.HARDWOOD_TREE,
			PatchType.CALQUAT,
			PatchType.CORAL,
			PatchType.BIRD_HOUSE);
		panel.update(
			Arrays.asList(
				snapshot(FarmPatch.SEAWEED_NORTH),
				snapshot(FarmPatch.ARDOUGNE)),
			initialOrder,
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		List<String> labels = labels(panel);
		assertTrue(labels.indexOf("Herbs") < labels.indexOf("Seaweed"));

		click(buttonWithText(panel, "Order"));
		assertFalse(buttonWithText(panel, "Start run").isEnabled());
		assertFalse(buttonWithText(panel, "Next patch").isEnabled());

		click(buttonWithTooltip(panel, "Move Seaweed earlier"));
		assertEquals(PatchType.SEAWEED, savedOrder.get().get(0));
		assertEquals(PatchType.HERB, savedOrder.get().get(1));

		click(buttonWithText(panel, "Reset order"));
		assertEquals(PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED), savedOrder.get());

		JLabel herbDragLabel = labelWithTooltip(panel, "Drag Herbs to reorder");
		assertNotNull(herbDragLabel);
		assertEquals(FontManager.getRunescapeBoldFont(), herbDragLabel.getFont());
		assertTrue(herbDragLabel.getMouseMotionListeners().length > 0);
		JLabel herbName = labelWithText(panel, "1. Herbs");
		assertNotNull(herbName);
		JComponent herbRow = (JComponent) herbName.getParent();
		assertEquals(null, herbDragLabel.getTransferHandler());
		assertEquals(0, herbName.getMouseMotionListeners().length);
		assertEquals(0, herbRow.getMouseMotionListeners().length);
		SwingUtilities.invokeAndWait(() -> panel.movePatchTypeTo(PatchType.HERB, initialOrder.size()));
		assertEquals(PatchType.HERB, savedOrder.get().get(savedOrder.get().size() - 1));

		click(buttonWithText(panel, "Done"));
		assertTrue(buttonWithText(panel, "Start run").isEnabled());
		assertTrue(buttonWithText(panel, "Next patch").isEnabled());
		assertNotNull(buttonWithText(panel, "Order"));
	}

	@Test
	public void headerOmitsRedundantStatusNoticeAndPlantingStockBlocks() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		SeedInventory seedStock = SeedInventory.builder()
			.markObserved(SeedInventory.Location.INVENTORY, 100L)
			.markObserved(SeedInventory.Location.BANK, 200L)
			.add(SeedInventory.Location.INVENTORY, PatchType.HERB, true, "Ranarr seed", 3)
			.add(SeedInventory.Location.BANK, PatchType.TREE, true, "Magic sapling", 2)
			.build();

		panel.update(
			Arrays.asList(snapshot(FarmPatch.ARDOUGNE)),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			seedStock);
		flushEdt();

		JLabel title = labelWithText(panel, "Freyja's Flora");
		assertNotNull(title);
		assertEquals(PatchMasterTheme.BRAND, title.getForeground());
		assertEquals(PatchMasterTheme.MOSS, buttonWithText(panel, "Start run").getBackground());
		assertEquals(PatchMasterTheme.CARD, buttonWithText(panel, "Order").getBackground());
		assertEquals(PatchMasterTheme.CARD, buttonWithText(panel, "Next patch").getBackground());
		assertEquals(PatchMasterTheme.TEXT_PRIMARY, buttonWithText(panel, "Clear").getForeground());
		JLabel herbHeader = labelWithText(panel, "Herbs");
		assertNotNull(herbHeader);
		JComponent patchList = (JComponent) herbHeader.getParent().getParent();
		Insets contentInsets = patchList.getBorder().getBorderInsets(patchList);
		assertEquals(PluginPanel.BORDER_OFFSET, contentInsets.left);
		assertEquals(PluginPanel.BORDER_OFFSET, contentInsets.right);
		Insets scrollerInsets = scrollPane(panel).getBorder().getBorderInsets(scrollPane(panel));
		assertEquals(0, scrollerInsets.top);
		assertEquals(0, scrollerInsets.left);
		assertEquals(0, scrollerInsets.bottom);
		assertEquals(0, scrollerInsets.right);
		assertFalse(labels(panel).stream().anyMatch(label ->
			label.contains("Planting stock")
				|| label.contains("Seed stock by location")
				|| label.contains("Profile changed")
				|| label.contains("NEEDS CARE")
				|| label.contains("TO DO")
				|| label.contains("GROWING")
				|| label.contains("UNKNOWN")));
	}

	@Test
	public void patchFamiliesCollapseAndCurrentDestinationExpandsItsFamily() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		List<PatchSnapshot> snapshots = Arrays.asList(
			snapshot(FarmPatch.ARDOUGNE),
			snapshot(FarmPatch.TREE_AUBURNVALE));

		panel.update(
			snapshots,
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		assertTrue(labels(panel).stream().anyMatch(label -> label.contains("Ardougne")));
		assertFalse(labels(panel).stream().anyMatch(label -> label.contains("Auburnvale")));
		click(labelWithTooltip(panel, "Expand Trees"));
		assertTrue(labels(panel).stream().anyMatch(label -> label.contains("Auburnvale")));
		click(labelWithTooltip(panel, "Collapse Trees"));
		assertFalse(labels(panel).stream().anyMatch(label -> label.contains("Auburnvale")));

		panel.update(
			snapshots,
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			FarmPatch.TREE_AUBURNVALE,
			1000L,
			SeedInventory.empty());
		flushEdt();

		assertTrue(labels(panel).stream().anyMatch(label -> label.contains("Auburnvale")));
		assertNotNull(buttonWithTooltip(panel, "Refresh the current Shortest Path destination"));
	}

	@Test
	public void sectionHeadersAndPatchCardsUseDistinctRuneLiteSurfaces() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		panel.update(
			Arrays.asList(snapshot(FarmPatch.ARDOUGNE)),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		JLabel sectionLabel = labelWithText(panel, "Herbs");
		JLabel patchLabel = labelContaining(panel, "Ardougne");
		assertNotNull(sectionLabel);
		assertNotNull(patchLabel);
		assertEquals(PatchMasterTheme.BACKGROUND, sectionLabel.getParent().getBackground());
		assertEquals(
			PatchMasterTheme.CARD,
			patchLabel.getParent().getParent().getParent().getBackground());
	}

	@Test
	public void sectionSummaryUsesStatusTilesAndARealChevronIcon() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		panel.update(
			Arrays.asList(
				snapshot(FarmPatch.ARDOUGNE, PatchState.READY),
				snapshot(FarmPatch.CATHERBY, PatchState.GROWING),
				snapshot(FarmPatch.CIVITAS_ILLA_FORTIS, PatchState.DISEASED),
				snapshot(FarmPatch.FALADOR, PatchState.DEAD),
				snapshot(FarmPatch.HOSIDIUS, PatchState.UNKNOWN)),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		List<JComponent> tiles = componentsWithClientProperty(panel, "patchmaster.statusTile");
		assertEquals(5, tiles.size());
		assertEquals(PatchMasterTheme.READY, tiles.get(0).getBackground());
		assertEquals(PatchMasterTheme.GROWING, tiles.get(1).getBackground());
		assertEquals(PatchMasterTheme.DEAD, tiles.get(2).getBackground());
		assertEquals(PatchMasterTheme.DEAD, tiles.get(3).getBackground());
		assertEquals(PatchMasterTheme.INACTIVE, tiles.get(4).getBackground());

		JLabel chevron = iconLabelWithTooltip(panel, "Collapse Herbs");
		assertNotNull(chevron);
		assertEquals(13, chevron.getIcon().getIconWidth());
		assertEquals(13, chevron.getIcon().getIconHeight());
		assertTrue(chevron.getText() == null || chevron.getText().isEmpty());
	}

	@Test
	public void narrowSidebarUsesWrappedLabelsWithoutHorizontalOverflow() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		panel.update(
			Arrays.asList(snapshot(FarmPatch.HARDWOOD_FOSSIL_ISLAND_MIDDLE)),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.builder()
				.markObserved(SeedInventory.Location.INVENTORY, 1L)
				.markObserved(SeedInventory.Location.BANK, 2L)
				.add(SeedInventory.Location.BANK, PatchType.FRUIT_TREE, false, "Dragonfruit tree seed", 2542)
				.build());
		flushEdt();

		JLabel title = labelWithText(panel, "Freyja's Flora");
		assertNotNull(title);
		assertTrue(title.getPreferredSize().width <= 205);
		assertTrue(buttonWithText(panel, "Start run").getPreferredSize().width <= 205);

		click(buttonWithText(panel, "Order"));
		JLabel orderHelp = labelWithTooltip(panel, "Change the farm run order");
		assertNotNull(orderHelp);
		assertTrue(
			"Order help preferred width was " + orderHelp.getPreferredSize().width,
			orderHelp.getPreferredSize().width <= 225);
		assertEquals(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER, scrollPane(panel).getHorizontalScrollBarPolicy());
	}

	@Test
	public void identicalVisibleStateDoesNotRebuildPatchRows() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		List<PatchSnapshot> snapshots = Arrays.asList(snapshot(FarmPatch.ARDOUGNE));
		List<PatchType> order = PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED);

		panel.update(snapshots, order, null, 1000L, SeedInventory.empty());
		flushEdt();
		JLabel originalSection = labelWithText(panel, "Herbs");
		assertNotNull(originalSection);

		panel.update(snapshots, order, null, 1001L, SeedInventory.empty());
		flushEdt();
		assertSame(originalSection, labelWithText(panel, "Herbs"));
	}

	@Test
	public void readyRowsUseTheGreenStateColorWithoutRepeatingReadyInTheLabel() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		PatchSnapshot snapshot = new PatchSnapshot(
			FarmPatch.ARDOUGNE,
			PatchPrediction.known("Kwuarm", PatchState.READY, 500L, 0L));

		panel.update(
			Arrays.asList(snapshot),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		JLabel cropStatus = labelContaining(panel, "Kwuarm");
		assertNotNull(cropStatus);
		assertFalse(cropStatus.getText().contains("Ready"));
		assertEquals(PatchMasterTheme.SAGE, cropStatus.getForeground());
	}

	@Test
	public void rowStatesUseDistinctAffordancesAndDoneCanBeUncompleted() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		AtomicReference<FarmPatch> uncompleted = new AtomicReference<>();
		panel.setListener(new FarmRunHelperPanel.Listener()
		{
			@Override
			public void onStartRun()
			{
			}

			@Override
			public void onNextPatch()
			{
			}

			@Override
			public void onClearRoute()
			{
			}

			@Override
			public void onRouteTo(FarmPatch patch)
			{
			}

			@Override
			public void onDoneToggled(FarmPatch patch)
			{
				uncompleted.set(patch);
			}

			@Override
			public void onPatchTypeOrderChanged(List<PatchType> order)
			{
			}
		});

		panel.update(
			Arrays.asList(snapshot(FarmPatch.ARDOUGNE), snapshot(FarmPatch.CATHERBY)),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			FarmPatch.ARDOUGNE,
			EnumSet.of(FarmPatch.CATHERBY),
			1000L,
			SeedInventory.empty());
		flushEdt();

		assertTrue(componentsWithClientProperty(panel, "patchmaster.rowState").stream()
			.anyMatch(component -> PatchRowPresentation.State.CURRENT.equals(
				component.getClientProperty("patchmaster.rowState"))));
		assertTrue(componentsWithClientProperty(panel, "patchmaster.rowOpacity").stream()
			.anyMatch(component -> Float.valueOf(0.5f).equals(
				component.getClientProperty("patchmaster.rowOpacity"))));
		assertEquals(PatchMasterTheme.SAGE, labelContaining(panel, "Crop").getForeground());
		assertEquals(Component.LEFT_ALIGNMENT, labelContaining(panel, "Ardougne").getAlignmentX(), 0.0f);
		assertNotNull(buttonWithText(panel, "➜"));
		assertFalse(buttonWithText(panel, "➜").getText().equals("✓"));

		JButton done = buttonWithTooltip(panel, "Mark this patch as not done");
		assertNotNull(done);
		assertEquals("✓", done.getText());
		click(done);
		assertEquals(FarmPatch.CATHERBY, uncompleted.get());
	}

	@Test
	public void emptyRowsUseAStableSingleLineLabel() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		PatchSnapshot snapshot = new PatchSnapshot(
			FarmPatch.ARDOUGNE,
			PatchPrediction.known("Empty", PatchState.EMPTY, 500L, 0L));

		panel.update(
			Arrays.asList(snapshot),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.builder()
				.markObserved(SeedInventory.Location.INVENTORY, 1L)
				.add(SeedInventory.Location.INVENTORY, PatchType.HERB, true, "Kwuarm seed", 1)
				.build());
		flushEdt();

		JLabel emptyStatus = labelContaining(panel, "Empty");
		assertNotNull(emptyStatus);
		assertEquals("<html><body>Empty</body></html>", emptyStatus.getText());
	}

	@Test
	public void cropTooltipIncludesTheRecordedCompostTier() throws Exception
	{
		FarmRunHelperPanel panel = new FarmRunHelperPanel();
		PatchSnapshot snapshot = new PatchSnapshot(
			FarmPatch.ARDOUGNE,
			PatchPrediction.known("Kwuarm", PatchState.GROWING, 500L, 1500L),
			CompostState.ULTRACOMPOST);

		panel.update(
			Arrays.asList(snapshot),
			PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED),
			null,
			1000L,
			SeedInventory.empty());
		flushEdt();

		assertNotNull(labelWithTooltip(panel, "Kwuarm with ultracompost"));
	}

	private static PatchSnapshot snapshot(FarmPatch patch)
	{
		return new PatchSnapshot(patch, PatchPrediction.known("Crop", PatchState.READY, 500L, 0L));
	}

	private static PatchSnapshot snapshot(FarmPatch patch, PatchState state)
	{
		return new PatchSnapshot(patch, PatchPrediction.known("Crop", state, 500L, 0L));
	}

	private static void click(JButton button) throws Exception
	{
		assertNotNull(button);
		SwingUtilities.invokeAndWait(button::doClick);
	}

	private static void click(JLabel label) throws Exception
	{
		SwingUtilities.invokeAndWait(() -> label.dispatchEvent(new MouseEvent(
			label,
			MouseEvent.MOUSE_CLICKED,
			System.currentTimeMillis(),
			0,
			1,
			1,
			1,
			false)));
	}

	private static void flushEdt() throws Exception
	{
		SwingUtilities.invokeAndWait(() -> { });
	}

	private static JButton buttonWithText(Container root, String text)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JButton && text.equals(((JButton) component).getText()))
			{
				return (JButton) component;
			}
		}
		return null;
	}

	private static JButton buttonWithTooltip(Container root, String tooltip)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JButton && tooltip.equals(((JButton) component).getToolTipText()))
			{
				return (JButton) component;
			}
		}
		return null;
	}

	private static JLabel labelWithTooltip(Container root, String tooltip)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JLabel && tooltip.equals(((JLabel) component).getToolTipText()))
			{
				return (JLabel) component;
			}
		}
		return null;
	}

	private static JLabel iconLabelWithTooltip(Container root, String tooltip)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JLabel
				&& ((JLabel) component).getIcon() != null
				&& tooltip.equals(((JLabel) component).getToolTipText()))
			{
				return (JLabel) component;
			}
		}
		return null;
	}

	private static List<JComponent> componentsWithClientProperty(Container root, String property)
	{
		List<JComponent> matches = new ArrayList<>();
		for (Component component : allComponents(root))
		{
			if (component instanceof JComponent
				&& ((JComponent) component).getClientProperty(property) != null)
			{
				matches.add((JComponent) component);
			}
		}
		return matches;
	}

	private static JLabel labelContaining(Container root, String text)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JLabel
				&& ((JLabel) component).getText() != null
				&& ((JLabel) component).getText().contains(text))
			{
				return (JLabel) component;
			}
		}
		return null;
	}

	private static JLabel labelWithText(Container root, String text)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JLabel && text.equals(((JLabel) component).getText()))
			{
				return (JLabel) component;
			}
		}
		return null;
	}

	private static JScrollPane scrollPane(Container root)
	{
		for (Component component : allComponents(root))
		{
			if (component instanceof JScrollPane)
			{
				return (JScrollPane) component;
			}
		}
		return null;
	}

	private static List<String> labels(Container root)
	{
		List<String> labels = new ArrayList<>();
		for (Component component : allComponents(root))
		{
			if (component instanceof JLabel)
			{
				String text = ((JLabel) component).getText();
				if (text != null)
				{
					labels.add(text);
				}
			}
		}
		return labels;
	}

	private static List<Component> allComponents(Container root)
	{
		List<Component> components = new ArrayList<>();
		for (Component component : root.getComponents())
		{
			components.add(component);
			if (component instanceof Container)
			{
				components.addAll(allComponents((Container) component));
			}
		}
		return components;
	}
}
