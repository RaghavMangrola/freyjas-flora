package com.farmrunhelper;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.farming.CompostState;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

final class FarmRunHelperPanel extends PluginPanel
{
	private static final int PANEL_TEXT_WIDTH = 190;
	private static final int PATCH_TEXT_WIDTH = 116;
	private static final int CROP_ICON_WIDTH = Constants.ITEM_SPRITE_WIDTH;
	private static final int CROP_ICON_HEIGHT = Constants.ITEM_SPRITE_HEIGHT;
	private static final int COMPOST_ICON_WIDTH = CROP_ICON_WIDTH * 2 / 3;
	private static final int COMPOST_ICON_HEIGHT = CROP_ICON_HEIGHT * 2 / 3;
	private static final int STATUS_TILE_SIZE = 5;
	private static final int STATUS_TILE_GAP = 2;
	private static final String ORDER_TYPE_PROPERTY = "patchmaster.orderType";
	private static final String STATUS_TILE_PROPERTY = "patchmaster.statusTile";
	private static final DataFlavor PATCH_TYPE_FLAVOR = new DataFlavor(PatchType.class, "Patch type");
	private static final Icon CHEVRON_RIGHT = new SectionChevronIcon(false);
	private static final Icon CHEVRON_DOWN = new SectionChevronIcon(true);

	interface Listener
	{
		void onStartRun();

		void onNextPatch();

		void onClearRoute();

		void onRouteTo(FarmPatch patch);

		void onDoneToggled(FarmPatch patch);

		void onPatchTypeOrderChanged(List<PatchType> order);
	}

	private final JPanel patchList = new JPanel();
	private final JPanel header = new JPanel();
	private final JButton startButton = new JButton("Start run");
	private final JButton nextButton = new JButton("Next patch");
	private final JButton orderButton = new JButton("Order");
	private final List<PatchType> patchTypeOrder = new ArrayList<>();
	private final EnumSet<PatchType> collapsedPatchTypes = EnumSet.allOf(PatchType.class);
	private final Map<Integer, ImageIcon> cropIcons = new HashMap<>();
	private final Map<Integer, ImageIcon> compostIcons = new HashMap<>();
	private final ItemManager itemManager;
	private List<PatchSnapshot> latestSnapshots = Collections.emptyList();
	private FarmPatch latestCurrentPatch;
	private Set<FarmPatch> latestDonePatches = Collections.emptySet();
	private long latestNow;
	private SeedInventory latestSeedInventory = SeedInventory.empty();
	private boolean editingOrder;
	private boolean patchSectionsInitialized;
	private String lastRenderedPatchListKey;
	private Listener listener;

	FarmRunHelperPanel()
	{
		this(null);
	}

	@Inject
	FarmRunHelperPanel(ItemManager itemManager)
	{
		super(false);
		this.itemManager = itemManager;
		setLayout(new BorderLayout());
		setBackground(PatchMasterTheme.BACKGROUND);

		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setBackground(PatchMasterTheme.BACKGROUND);
		header.setBorder(new EmptyBorder(
			PluginPanel.BORDER_OFFSET + 2,
			PluginPanel.BORDER_OFFSET,
			9,
			PluginPanel.BORDER_OFFSET));

		JPanel brand = new JPanel(new BorderLayout(5, 0));
		brand.setBackground(PatchMasterTheme.BACKGROUND);
		brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel title = new JLabel("Freyja’s Flora", new ImageIcon(FarmRunIcon.fallback()), SwingConstants.LEFT);
		title.setFont(FontManager.getRunescapeBoldFont());
		title.setForeground(PatchMasterTheme.BRAND);
		title.setIconTextGap(6);
		FarmRunIcon.loadFarmersShirt(itemManager, 16, icon -> SwingUtilities.invokeLater(() ->
		{
			title.setIcon(new ImageIcon(icon));
			title.revalidate();
			title.repaint();
		}));
		brand.add(title, BorderLayout.CENTER);
		orderButton.setMargin(new Insets(2, 7, 2, 7));
		styleButton(orderButton, PatchMasterTheme.CARD, PatchMasterTheme.TEXT_PRIMARY);
		orderButton.setToolTipText("Change the patch-type order used by the panel and farm run");
		orderButton.addActionListener(event -> setEditingOrder(!editingOrder));
		brand.add(orderButton, BorderLayout.EAST);
		header.add(brand);

		JPanel primaryAction = new JPanel(new BorderLayout());
		primaryAction.setBackground(PatchMasterTheme.BACKGROUND);
		primaryAction.setBorder(new EmptyBorder(6, 0, 4, 0));
		startButton.setToolTipText("Route to the first ready, empty, or unknown patch");
		styleButton(startButton, PatchMasterTheme.MOSS, PatchMasterTheme.TEXT_PRIMARY);
		startButton.addActionListener(event ->
		{
			if (listener != null)
			{
				listener.onStartRun();
			}
		});
		primaryAction.add(startButton, BorderLayout.CENTER);
		header.add(primaryAction);

		JPanel secondaryActions = new JPanel(new GridLayout(1, 2, 4, 0));
		secondaryActions.setBackground(PatchMasterTheme.BACKGROUND);
		nextButton.setToolTipText("Route to the next patch included in this run");
		nextButton.addActionListener(event ->
		{
			if (listener != null)
			{
				listener.onNextPatch();
			}
		});
		JButton clear = new JButton("Clear");
		clear.setToolTipText("Clear the Shortest Path route");
		clear.addActionListener(event ->
		{
			if (listener != null)
			{
				listener.onClearRoute();
			}
		});
		Insets actionMargin = new Insets(2, 6, 2, 6);
		nextButton.setMargin(actionMargin);
		clear.setMargin(actionMargin);
		styleButton(nextButton, PatchMasterTheme.CARD, PatchMasterTheme.TEXT_PRIMARY);
		styleButton(clear, PatchMasterTheme.CARD, PatchMasterTheme.TEXT_PRIMARY);
		secondaryActions.add(nextButton);
		secondaryActions.add(clear);
		secondaryActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, secondaryActions.getPreferredSize().height));
		header.add(secondaryActions);

		patchList.setLayout(new BoxLayout(patchList, BoxLayout.Y_AXIS));
		patchList.setBackground(PatchMasterTheme.BACKGROUND);
		patchList.setBorder(new EmptyBorder(
			0,
			PluginPanel.BORDER_OFFSET,
			PluginPanel.BORDER_OFFSET,
			PluginPanel.BORDER_OFFSET));
		patchList.setTransferHandler(new PatchTypeTransferHandler(null));
		JPanel patchListWrapper = new JPanel(new BorderLayout());
		patchListWrapper.setBackground(PatchMasterTheme.BACKGROUND);
		patchListWrapper.add(patchList, BorderLayout.NORTH);

		JScrollPane patchScroller = new JScrollPane(patchListWrapper);
		patchScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		patchScroller.setBorder(BorderFactory.createEmptyBorder());
		patchScroller.setViewportBorder(null);
		patchScroller.getVerticalScrollBar().setUI(new RuneLiteStyleScrollBarUI());
		patchScroller.getVerticalScrollBar().setUnitIncrement(16);
		patchScroller.setBackground(PatchMasterTheme.BACKGROUND);

		add(header, BorderLayout.NORTH);
		add(patchScroller, BorderLayout.CENTER);
	}

	void setListener(Listener listener)
	{
		this.listener = listener;
	}

	private static void styleButton(JButton button, Color background, Color foreground)
	{
		button.setBackground(background);
		button.setForeground(foreground);
		button.setBorder(BorderFactory.createLineBorder(PatchMasterTheme.BUTTON_BORDER));
		button.setFocusPainted(false);
	}

	void update(
		List<PatchSnapshot> snapshots,
		List<PatchType> orderedTypes,
		FarmPatch currentPatch,
		long now,
		SeedInventory seedInventory)
	{
		update(snapshots, orderedTypes, currentPatch, Collections.emptySet(), now, seedInventory);
	}

	void update(
		List<PatchSnapshot> snapshots,
		List<PatchType> orderedTypes,
		FarmPatch currentPatch,
		Set<FarmPatch> donePatches,
		long now,
		SeedInventory seedInventory)
	{
		SwingUtilities.invokeLater(() -> updateOnEdt(
			snapshots,
			orderedTypes,
			currentPatch,
			donePatches,
			now,
			seedInventory));
	}

	private void updateOnEdt(
		List<PatchSnapshot> snapshots,
		List<PatchType> orderedTypes,
		FarmPatch currentPatch,
		Set<FarmPatch> donePatches,
		long now,
		SeedInventory seedInventory)
	{
		latestSnapshots = snapshots;
		latestCurrentPatch = currentPatch;
		latestDonePatches = donePatches.isEmpty()
			? Collections.emptySet()
			: java.util.EnumSet.copyOf(donePatches);
		latestNow = now;
		latestSeedInventory = seedInventory;
		patchTypeOrder.clear();
		patchTypeOrder.addAll(orderedTypes);
		if (!patchSectionsInitialized)
		{
			for (PatchType type : patchTypeOrder)
			{
				if (snapshots.stream().anyMatch(snapshot -> snapshot.getPatch().getType() == type))
				{
					collapsedPatchTypes.remove(type);
					patchSectionsInitialized = true;
					break;
				}
			}
		}
		if (currentPatch != null)
		{
			collapsedPatchTypes.remove(currentPatch.getType());
		}

		String patchListKey = buildPatchListPresentationKey();
		if (!patchListKey.equals(lastRenderedPatchListKey))
		{
			renderPatchList();
			lastRenderedPatchListKey = patchListKey;
			refreshPatchListLayout();
		}
	}

	private void renderPatchList()
	{
		patchList.removeAll();
		if (editingOrder)
		{
			renderOrderEditor();
			return;
		}
		for (PatchType type : patchTypeOrder)
		{
			List<PatchSnapshot> group = new ArrayList<>();
			for (PatchSnapshot snapshot : latestSnapshots)
			{
				if (snapshot.getPatch().getType() == type)
				{
					group.add(snapshot);
				}
			}
			if (group.isEmpty())
			{
				continue;
			}

			patchList.add(patchSectionHeader(type, group));
			if (!collapsedPatchTypes.contains(type))
			{
				for (PatchSnapshot snapshot : group)
				{
					patchList.add(patchRow(
						snapshot,
						latestNow,
						latestSeedInventory));
				}
			}
		}
	}

	private void setEditingOrder(boolean editingOrder)
	{
		this.editingOrder = editingOrder;
		startButton.setEnabled(!editingOrder);
		nextButton.setEnabled(!editingOrder);
		orderButton.setText(editingOrder ? "Done" : "Order");
		orderButton.setToolTipText(editingOrder
			? "Return to the patch list"
			: "Change the patch-type order used by the panel and farm run");

		if (editingOrder && patchTypeOrder.isEmpty())
		{
			patchTypeOrder.addAll(PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED));
		}
		renderPatchListAndRefresh();
	}

	private void renderOrderEditor()
	{
		patchList.add(sectionHeader("Farm run order"));

		JPanel helpContainer = new JPanel(new BorderLayout());
		helpContainer.setBackground(PatchMasterTheme.BACKGROUND);
		helpContainer.setBorder(new EmptyBorder(4, 7, 7, 7));
		JLabel help = new JLabel(wrap(
			"Drag patch types into the order you want, or use the arrow buttons.",
			PANEL_TEXT_WIDTH));
		help.setForeground(PatchMasterTheme.TEXT_SECONDARY);
		help.setFont(FontManager.getRunescapeSmallFont());
		help.setToolTipText("Change the farm run order");
		helpContainer.add(help, BorderLayout.CENTER);
		patchList.add(helpContainer);

		for (int index = 0; index < patchTypeOrder.size(); index++)
		{
			patchList.add(orderRow(patchTypeOrder.get(index), index));
		}

		JPanel resetContainer = new JPanel(new BorderLayout());
		resetContainer.setBackground(PatchMasterTheme.BACKGROUND);
		resetContainer.setBorder(new EmptyBorder(6, 0, 0, 0));
		JButton reset = new JButton("Reset order");
		reset.setToolTipText("Restore the default patch-type order");
		styleButton(reset, PatchMasterTheme.CARD, PatchMasterTheme.TEXT_PRIMARY);
		reset.addActionListener(event -> resetPatchTypeOrder());
		resetContainer.add(reset, BorderLayout.CENTER);
		patchList.add(resetContainer);
	}

	private JPanel orderRow(PatchType type, int index)
	{
		JPanel row = new JPanel(new BorderLayout(5, 0));
		row.setBackground(PatchMasterTheme.CARD);
		row.putClientProperty(ORDER_TYPE_PROPERTY, type);
		row.setBorder(BorderFactory.createCompoundBorder(
			new EmptyBorder(0, 0, 3, 0),
			new EmptyBorder(5, 7, 5, 5)));

		JLabel handle = new JLabel("≡");
		handle.setForeground(PatchMasterTheme.TEXT_SECONDARY);
		handle.setFont(FontManager.getRunescapeBoldFont());
		handle.setBorder(new EmptyBorder(0, 0, 0, 3));
		installOrderDragSource(handle, type);
		installOrderDragSource(row, type);
		row.add(handle, BorderLayout.WEST);

		JLabel name = new JLabel((index + 1) + ". " + type.getDisplayName());
		name.setFont(FontManager.getRunescapeBoldFont());
		name.setForeground(PatchMasterTheme.TEXT_PRIMARY);
		installOrderDragSource(name, type);
		row.add(name, BorderLayout.CENTER);

		JPanel controls = new JPanel(new GridLayout(1, 2, 3, 0));
		controls.setOpaque(false);
		JButton up = new JButton("↑");
		up.setPreferredSize(new Dimension(30, 26));
		up.setMargin(new Insets(1, 4, 1, 4));
		styleButton(up, PatchMasterTheme.CARD_HOVER, PatchMasterTheme.TEXT_PRIMARY);
		up.setToolTipText("Move " + type.getDisplayName() + " earlier");
		up.setEnabled(index > 0);
		up.addActionListener(event -> movePatchType(type, -1));
		JButton down = new JButton("↓");
		down.setPreferredSize(new Dimension(30, 26));
		down.setMargin(new Insets(1, 4, 1, 4));
		styleButton(down, PatchMasterTheme.CARD_HOVER, PatchMasterTheme.TEXT_PRIMARY);
		down.setToolTipText("Move " + type.getDisplayName() + " later");
		down.setEnabled(index < patchTypeOrder.size() - 1);
		down.addActionListener(event -> movePatchType(type, 1));
		controls.add(up);
		controls.add(down);
		row.add(controls, BorderLayout.EAST);
		installOrderDropTarget(row);
		return row;
	}

	private void movePatchType(PatchType type, int direction)
	{
		int currentIndex = patchTypeOrder.indexOf(type);
		int nextIndex = currentIndex + direction;
		if (currentIndex < 0 || nextIndex < 0 || nextIndex >= patchTypeOrder.size())
		{
			return;
		}

		Collections.swap(patchTypeOrder, currentIndex, nextIndex);
		notifyPatchTypeOrderChanged();
		renderPatchListAndRefresh();
	}

	boolean movePatchTypeTo(PatchType type, int insertionIndex)
	{
		int currentIndex = patchTypeOrder.indexOf(type);
		if (currentIndex < 0)
		{
			return false;
		}

		int targetIndex = Math.max(0, Math.min(insertionIndex, patchTypeOrder.size()));
		if (currentIndex < targetIndex)
		{
			targetIndex--;
		}
		if (targetIndex == currentIndex)
		{
			return false;
		}

		patchTypeOrder.remove(currentIndex);
		patchTypeOrder.add(targetIndex, type);
		notifyPatchTypeOrderChanged();
		renderPatchListAndRefresh();
		return true;
	}

	private void resetPatchTypeOrder()
	{
		patchTypeOrder.clear();
		patchTypeOrder.addAll(PatchTypeOrder.parse(PatchTypeOrder.DEFAULT_SERIALIZED));
		notifyPatchTypeOrderChanged();
		renderPatchListAndRefresh();
	}

	private void notifyPatchTypeOrderChanged()
	{
		if (listener != null)
		{
			listener.onPatchTypeOrderChanged(new ArrayList<>(patchTypeOrder));
		}
	}

	private void refreshPatchListLayout()
	{
		patchList.revalidate();
		patchList.repaint();
	}

	private void renderPatchListAndRefresh()
	{
		renderPatchList();
		lastRenderedPatchListKey = buildPatchListPresentationKey();
		refreshPatchListLayout();
	}

	private String buildPatchListPresentationKey()
	{
		StringBuilder key = new StringBuilder(editingOrder ? "order|" : "patches|");
		if (editingOrder)
		{
			return key.append(patchTypeOrder).toString();
		}

		key.append("collapsed=").append(collapsedPatchTypes).append('|');
		for (PatchSnapshot snapshot : latestSnapshots)
		{
			PatchPrediction prediction = snapshot.getPrediction();
			PatchState state = prediction.getEffectiveState(latestNow);
			key.append(snapshot.getPatch())
				.append(':').append(PatchRowPresentation.forPatch(
					snapshot.getPatch(),
					latestCurrentPatch,
					latestDonePatches,
					state).getState())
				.append(':').append(state)
				.append(':').append(snapshot.getCompostState())
				.append(':').append(statusText(
					snapshot.getPatch(),
					prediction,
					state,
					latestNow,
					latestSeedInventory))
				.append('|');
		}
		return key.toString();
	}

	private void installOrderDragSource(JComponent component, PatchType type)
	{
		component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		component.setToolTipText("Drag " + type.getDisplayName() + " to reorder");
		component.setTransferHandler(new PatchTypeTransferHandler(type));
		component.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent event)
			{
				JComponent source = (JComponent) event.getSource();
				source.getTransferHandler().exportAsDrag(source, event, TransferHandler.MOVE);
			}
		});
	}

	private void installOrderDropTarget(JComponent component)
	{
		if (component.getTransferHandler() == null)
		{
			component.setTransferHandler(new PatchTypeTransferHandler(null));
		}
		for (Component child : component.getComponents())
		{
			if (child instanceof JComponent)
			{
				installOrderDropTarget((JComponent) child);
			}
		}
	}

	private int orderInsertionIndexAt(Point point)
	{
		for (Component component : patchList.getComponents())
		{
			if (!(component instanceof JComponent))
			{
				continue;
			}
			Object value = ((JComponent) component).getClientProperty(ORDER_TYPE_PROPERTY);
			if (!(value instanceof PatchType))
			{
				continue;
			}
			int index = patchTypeOrder.indexOf(value);
			if (index >= 0 && point.y < component.getY() + component.getHeight() / 2)
			{
				return index;
			}
		}
		return patchTypeOrder.size();
	}

	private final class PatchTypeTransferHandler extends TransferHandler
	{
		private final PatchType sourceType;

		private PatchTypeTransferHandler(PatchType sourceType)
		{
			this.sourceType = sourceType;
		}

		@Override
		public int getSourceActions(JComponent component)
		{
			return sourceType == null ? NONE : MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent component)
		{
			return sourceType == null ? null : new PatchTypeTransferable(sourceType);
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			if (!editingOrder || !support.isDrop() || !support.isDataFlavorSupported(PATCH_TYPE_FLAVOR))
			{
				return false;
			}
			support.setDropAction(MOVE);
			return true;
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			if (!canImport(support))
			{
				return false;
			}
			try
			{
				PatchType type = (PatchType) support.getTransferable().getTransferData(PATCH_TYPE_FLAVOR);
				Point point = support.getDropLocation().getDropPoint();
				point = SwingUtilities.convertPoint(support.getComponent(), point, patchList);
				return movePatchTypeTo(type, orderInsertionIndexAt(point));
			}
			catch (UnsupportedFlavorException | IOException exception)
			{
				return false;
			}
		}
	}

	private static final class PatchTypeTransferable implements Transferable
	{
		private final PatchType type;

		private PatchTypeTransferable(PatchType type)
		{
			this.type = type;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return new DataFlavor[]{PATCH_TYPE_FLAVOR};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return PATCH_TYPE_FLAVOR.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
		{
			if (!isDataFlavorSupported(flavor))
			{
				throw new UnsupportedFlavorException(flavor);
			}
			return type;
		}
	}

	private JPanel patchSectionHeader(PatchType type, List<PatchSnapshot> snapshots)
	{
		boolean collapsed = collapsedPatchTypes.contains(type);
		boolean routing = latestCurrentPatch != null && latestCurrentPatch.getType() == type;
		JPanel panel = new JPanel(new BorderLayout(6, 0));
		panel.setBackground(routing ? PatchMasterTheme.CARD_HOVER : PatchMasterTheme.BACKGROUND);
		panel.setBorder(BorderFactory.createCompoundBorder(
			new EmptyBorder(10, 0, 3, 0),
			new EmptyBorder(7, 7, 7, 7)));

		JLabel label = new JLabel(type.getDisplayName());
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setForeground(PatchMasterTheme.TEXT_PRIMARY);
		panel.add(label, BorderLayout.CENTER);

		JPanel summaryPanel = new JPanel(new BorderLayout(7, 0));
		summaryPanel.setOpaque(false);
		JPanel statusGrid = patchStatusGrid(snapshots);
		JPanel statusGridWrapper = new JPanel(new GridBagLayout());
		statusGridWrapper.setOpaque(false);
		statusGridWrapper.add(statusGrid);
		summaryPanel.add(statusGridWrapper, BorderLayout.CENTER);

		JLabel chevron = new JLabel(collapsed ? CHEVRON_RIGHT : CHEVRON_DOWN);
		summaryPanel.add(chevron, BorderLayout.EAST);
		panel.add(summaryPanel, BorderLayout.EAST);

		MouseAdapter toggle = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if (!collapsedPatchTypes.remove(type))
				{
					collapsedPatchTypes.add(type);
				}
				renderPatchListAndRefresh();
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
				panel.setBackground(PatchMasterTheme.CARD_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				panel.setBackground(routing
					? PatchMasterTheme.CARD_HOVER
					: PatchMasterTheme.BACKGROUND);
			}
		};
		installSectionToggle(
			panel,
			toggle,
			(collapsed ? "Expand " : "Collapse ") + type.getDisplayName());
		return panel;
	}

	private JPanel patchStatusGrid(List<PatchSnapshot> snapshots)
	{
		JPanel grid = new JPanel(new GridLayout(0, 3, STATUS_TILE_GAP, STATUS_TILE_GAP));
		grid.setOpaque(false);
		for (PatchSnapshot snapshot : snapshots)
		{
			PatchState state = snapshot.getPrediction().getEffectiveState(latestNow);
			JPanel tile = new JPanel();
			tile.setBackground(statusTileColor(state));
			tile.setPreferredSize(new Dimension(STATUS_TILE_SIZE, STATUS_TILE_SIZE));
			tile.putClientProperty(STATUS_TILE_PROPERTY, state);
			tile.setToolTipText(snapshot.getPatch().getDisplayName() + " · " + stateLabel(state));
			grid.add(tile);
		}
		return grid;
	}

	private static Color statusTileColor(PatchState state)
	{
		switch (state)
		{
			case READY:
				return PatchMasterTheme.READY;
			case GROWING:
				return PatchMasterTheme.GROWING;
			case DISEASED:
			case DEAD:
				return PatchMasterTheme.DEAD;
			default:
				return PatchMasterTheme.INACTIVE;
		}
	}

	private static void installSectionToggle(JComponent component, MouseAdapter toggle, String tooltip)
	{
		component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (component.getToolTipText() == null)
		{
			component.setToolTipText(tooltip);
		}
		component.addMouseListener(toggle);
		for (Component child : component.getComponents())
		{
			if (child instanceof JComponent)
			{
				installSectionToggle((JComponent) child, toggle, tooltip);
			}
		}
	}

	private static final class SectionChevronIcon implements Icon
	{
		private final boolean down;

		private SectionChevronIcon(boolean down)
		{
			this.down = down;
		}

		@Override
		public void paintIcon(Component component, Graphics graphics, int x, int y)
		{
			Graphics2D graphics2D = (Graphics2D) graphics.create();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(PatchMasterTheme.TEXT_SECONDARY);
			graphics2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			if (down)
			{
				graphics2D.drawLine(x + 3, y + 5, x + 6, y + 8);
				graphics2D.drawLine(x + 6, y + 8, x + 9, y + 5);
			}
			else
			{
				graphics2D.drawLine(x + 5, y + 3, x + 8, y + 6);
				graphics2D.drawLine(x + 8, y + 6, x + 5, y + 9);
			}
			graphics2D.dispose();
		}

		@Override
		public int getIconWidth()
		{
			return 13;
		}

		@Override
		public int getIconHeight()
		{
			return 13;
		}
	}

	private JPanel sectionHeader(String text)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(PatchMasterTheme.BACKGROUND);
		panel.setBorder(new EmptyBorder(10, 2, 3, 2));
		JLabel label = new JLabel(text);
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setForeground(PatchMasterTheme.TEXT_PRIMARY);
		panel.add(label, BorderLayout.CENTER);
		return panel;
	}

	private JPanel patchRow(PatchSnapshot snapshot, long now, SeedInventory seedInventory)
	{
		PatchPrediction prediction = snapshot.getPrediction();
		PatchState state = prediction.getEffectiveState(now);
		PatchRowPresentation presentation = PatchRowPresentation.forPatch(
			snapshot.getPatch(),
			latestCurrentPatch,
			latestDonePatches,
			state);

		PatchRowPanel row = new PatchRowPanel(presentation.getOpacity());
		row.setLayout(new BorderLayout(5, 0));
		row.setBackground(presentation.getBackgroundColor());
		row.putClientProperty("patchmaster.rowState", presentation.getState());
		row.putClientProperty("patchmaster.rowOpacity", presentation.getOpacity());
		row.setBorder(BorderFactory.createCompoundBorder(
			new EmptyBorder(0, 0, 3, 0),
			new EmptyBorder(6, 0, 6, 5)));
		JPanel accent = new JPanel();
		accent.setBackground(presentation.getAccentColor());
		accent.putClientProperty("patchmaster.rowAccent", presentation.getState());
		accent.setPreferredSize(new Dimension(4, 1));
		accent.setToolTipText(presentation.getState() == PatchRowPresentation.State.CURRENT
			? "Current destination"
			: presentation.getState() == PatchRowPresentation.State.DONE ? "Done" : "Upcoming");
		row.add(accent, BorderLayout.WEST);

		JPanel content = new JPanel(new BorderLayout(4, 0));
		content.setOpaque(false);
		content.add(patchIcon(snapshot), BorderLayout.WEST);

		JPanel labels = new JPanel();
		labels.setOpaque(false);
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		JLabel name = new JLabel(wrap(snapshot.getPatch().getDisplayName(), PATCH_TEXT_WIDTH));
		name.setFont(FontManager.getRunescapeBoldFont());
		name.setForeground(PatchMasterTheme.TEXT_PRIMARY);
		name.setAlignmentX(Component.LEFT_ALIGNMENT);
		String statusText = statusText(snapshot.getPatch(), prediction, state, now, seedInventory);
		labels.add(name);
		labels.add(cropSubtitle(statusText, state));
		content.add(labels, BorderLayout.CENTER);
		row.add(content, BorderLayout.CENTER);

		JPanel routeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		routeContainer.setOpaque(false);
		JButton route = new JButton(presentation.getActionGlyph());
		route.setPreferredSize(new Dimension(34, 31));
		route.setToolTipText(presentation.getActionTooltip());
		route.putClientProperty("patchmaster.rowAction", presentation.getState());
		if (presentation.getState() == PatchRowPresentation.State.CURRENT)
		{
			route.setForeground(PatchMasterTheme.ROUTING);
		}
		else
		{
			route.setForeground(PatchMasterTheme.TEXT_SECONDARY);
		}
		route.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent event)
			{
				if (!presentation.isDone())
				{
					route.setForeground(PatchMasterTheme.GOLD);
				}
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
				route.setForeground(presentation.getState() == PatchRowPresentation.State.CURRENT
					? PatchMasterTheme.GOLD
					: PatchMasterTheme.TEXT_SECONDARY);
			}
		});
		route.addActionListener(event ->
		{
			if (listener != null)
			{
				if (presentation.isDone())
				{
					listener.onDoneToggled(snapshot.getPatch());
				}
				else
				{
					listener.onRouteTo(snapshot.getPatch());
				}
			}
		});
		routeContainer.add(route);
		row.add(routeContainer, BorderLayout.EAST);
		return row;
	}

	private static JLabel cropSubtitle(String cropText, PatchState state)
	{
		JLabel subtitle = new JLabel(wrap(cropText, PATCH_TEXT_WIDTH));
		subtitle.setFont(FontManager.getRunescapeSmallFont());
		subtitle.setForeground(state == PatchState.DEAD || state == PatchState.DISEASED
			? PatchMasterTheme.DANGER
			: PatchMasterTheme.SAGE);
		subtitle.setToolTipText(cropText);
		subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		return subtitle;
	}

	private static final class PatchRowPanel extends JPanel
	{
		private final float contentOpacity;

		private PatchRowPanel(float contentOpacity)
		{
			this.contentOpacity = contentOpacity;
		}

		@Override
		public void paint(Graphics graphics)
		{
			if (contentOpacity >= 1.0f || getWidth() <= 0 || getHeight() <= 0)
			{
				super.paint(graphics);
				return;
			}

			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphics = image.createGraphics();
			super.paint(imageGraphics);
			imageGraphics.dispose();

			Graphics2D graphics2D = (Graphics2D) graphics.create();
			graphics2D.setComposite(AlphaComposite.SrcOver.derive(contentOpacity));
			graphics2D.drawImage(image, 0, 0, null);
			graphics2D.dispose();
		}
	}

	private JLayeredPane patchIcon(PatchSnapshot snapshot)
	{
		JLayeredPane layeredIcon = new JLayeredPane();
		layeredIcon.setPreferredSize(new Dimension(CROP_ICON_WIDTH, CROP_ICON_HEIGHT));
		String tooltip = cropIconTooltip(snapshot);
		layeredIcon.setToolTipText(tooltip);

		JLabel cropIcon = new JLabel("", SwingConstants.CENTER);
		cropIcon.setBounds(0, 0, CROP_ICON_WIDTH, CROP_ICON_HEIGHT);
		cropIcon.setToolTipText(tooltip);
		int cropItemId = PatchCropIcon.itemIdFor(
			snapshot.getPatch().getType(),
			snapshot.getPrediction().getCropName());
		bindItemIcon(
			cropIcon,
			cropItemId,
			CROP_ICON_WIDTH,
			CROP_ICON_HEIGHT,
			cropIcons);
		layeredIcon.add(cropIcon, (Object) 0);

		CompostState compostState = snapshot.getCompostState();
		if (compostState != null)
		{
			JLabel compostIcon = new JLabel("", SwingConstants.CENTER);
			compostIcon.setBounds(
				CROP_ICON_WIDTH - COMPOST_ICON_WIDTH,
				CROP_ICON_HEIGHT - COMPOST_ICON_HEIGHT,
				COMPOST_ICON_WIDTH,
				COMPOST_ICON_HEIGHT);
			compostIcon.setToolTipText(tooltip);
			bindItemIcon(
				compostIcon,
				compostState.getItemId(),
				COMPOST_ICON_WIDTH,
				COMPOST_ICON_HEIGHT,
				compostIcons);
			layeredIcon.add(compostIcon, (Object) 1);
		}

		return layeredIcon;
	}

	private void bindItemIcon(
		JLabel label,
		int itemId,
		int maximumWidth,
		int maximumHeight,
		Map<Integer, ImageIcon> cache)
	{
		if (itemManager == null)
		{
			return;
		}

		ImageIcon cached = cache.get(itemId);
		if (cached != null)
		{
			label.setIcon(cached);
			return;
		}

		AsyncBufferedImage source = itemManager.getImage(itemId);
		if (source == null)
		{
			return;
		}

		source.onLoaded(() ->
		{
			ImageIcon icon = scaledItemIcon(source, maximumWidth, maximumHeight);
			SwingUtilities.invokeLater(() ->
			{
				ImageIcon resolved = cache.computeIfAbsent(itemId, ignored -> icon);
				label.setIcon(resolved);
				label.revalidate();
				label.repaint();
			});
		});
	}

	private static ImageIcon scaledItemIcon(BufferedImage source, int maximumWidth, int maximumHeight)
	{
		double scale = Math.min(
			(double) maximumWidth / source.getWidth(),
			(double) maximumHeight / source.getHeight());
		int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
		int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
		return new ImageIcon(ImageUtil.resizeImage(source, width, height));
	}

	private static String cropIconTooltip(PatchSnapshot snapshot)
	{
		String cropName = snapshot.getPrediction().getCropName();
		String cropDescription;
		if ("Empty".equals(cropName) || "Unknown".equals(cropName) || "Herb".equals(cropName))
		{
			cropDescription = snapshot.getPatch().getType().getDisplayName();
		}
		else
		{
			cropDescription = cropName;
		}

		CompostState compostState = snapshot.getCompostState();
		return compostState == null
			? cropDescription
			: cropDescription + " with " + compostName(compostState);
	}

	private static String compostName(CompostState compostState)
	{
		switch (compostState)
		{
			case ULTRACOMPOST:
				return "ultracompost";
			case SUPERCOMPOST:
				return "supercompost";
			default:
				return "compost";
		}
	}

	private static String statusText(
		FarmPatch patch,
		PatchPrediction prediction,
		PatchState state,
		long now,
		SeedInventory seedInventory)
	{
		switch (state)
		{
			case READY:
				return prediction.getCropName();
			case GROWING:
				return prediction.getCropName() + " · " + formatRemaining(prediction.getReadyAt() - now);
			case DISEASED:
				return prediction.getCropName() + " · Diseased";
			case DEAD:
				return prediction.getCropName() + " · Dead";
			case EMPTY:
				return patch.getType() == PatchType.BIRD_HOUSE && !"Empty".equals(prediction.getCropName())
					? prediction.getCropName() + " · Needs seed"
					: "Empty";
			default:
				return "Visit once to sync";
		}
	}

	private static String formatRemaining(long seconds)
	{
		if (seconds <= 0)
		{
			return "Ready";
		}
		Duration duration = Duration.ofSeconds(seconds);
		long hours = duration.toHours();
		long minutes = duration.minusHours(hours).toMinutes();
		if (hours > 0)
		{
			return hours + "h " + minutes + "m";
		}
		return Math.max(1, minutes) + "m";
	}

	private static String stateLabel(PatchState state)
	{
		switch (state)
		{
			case READY:
				return "Ready";
			case GROWING:
				return "Growing";
			case DISEASED:
				return "Diseased";
			case DEAD:
				return "Dead";
			case EMPTY:
				return "Empty";
			default:
				return "Unknown";
		}
	}

	private static Color statusColor(PatchState state)
	{
		switch (state)
		{
			case READY:
				return PatchMasterTheme.READY;
			case GROWING:
				return PatchMasterTheme.GROWING;
			case DISEASED:
				return PatchMasterTheme.DISEASED;
			case DEAD:
				return PatchMasterTheme.DEAD;
			case EMPTY:
				return PatchMasterTheme.EMPTY;
			default:
				return PatchMasterTheme.UNKNOWN;
		}
	}

	private static String wrap(String text)
	{
		return wrap(text, 195);
	}

	private static String wrap(String text, int width)
	{
		int maxCharactersPerLine = Math.max(12, width / 7);
		StringBuilder wrapped = new StringBuilder();
		int lineLength = 0;
		for (String word : text.split("\\s+"))
		{
			if (lineLength > 0 && lineLength + 1 + word.length() > maxCharactersPerLine)
			{
				wrapped.append("<br>");
				lineLength = 0;
			}
			else if (lineLength > 0)
			{
				wrapped.append(' ');
				lineLength++;
			}
			wrapped.append(escapeHtml(word));
			lineLength += word.length();
		}
		return "<html><body>" + wrapped + "</body></html>";
	}

	private static String escapeHtml(String text)
	{
		return text
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;");
	}
}
