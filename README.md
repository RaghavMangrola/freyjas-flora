# PatchMaster

PatchMaster is a visual-only RuneLite plugin for herb, regular tree, fruit tree,
hardwood tree, calquat, coral, and giant seaweed runs. It reads the same profile-scoped records maintained by
RuneLite's built-in Time Tracking plugin and sends user-selected destinations
to the Shortest Path plugin.

## Features

- Shows all 36 supported destinations: 10 herb, 7 regular tree, 7 fruit tree,
  5 hardwood tree, 3 calquat, 2 Great Conch coral, and 2 Fossil Island seaweed
  patches.
- Displays ready, growing, diseased, dead, empty, and not-yet-observed states.
- Uses RuneLite's farming-tick offset and current profile when estimating ready
  times.
- Provides **Start run**, **Next patch**, and per-patch route buttons.
- Groups patch controls into compact, collapsible herb, tree, fruit tree,
  hardwood tree, calquat, coral, and seaweed sections with per-group status
  counts.
- Lets users reorder those patch types from the sidebar so the visible panel and
  **Start run**/**Next patch** route sequence match their preferred farm run.
- Automatically routes a started run to the next patch after Time Tracking
  observes the current patch being replanted and, by default, composted; both
  behaviors can be changed in settings.
- Routes to the Great Conch nursery steps or Fossil Island dive point first,
  then updates the visual destination after the player enters the interior.
- Lets users include or exclude empty and unknown patches from a ready run.
- Lets users enable or disable every patch type and individual patch in the
  RuneLite config.
- Shows exact seed and sapling quantities from the live inventory plus the last
  Bank and Seed Vault snapshots saved for the active RuneScape profile. Click
  the seed summary to see which location holds each item; empty patches point
  out when a usable planting item is stored instead of carried.
- Highlights visible ready, empty, dead, and diseased patches with subtle
  farming-themed outlines. Magic secateurs mark ready/dead patches, a herb seed
  marks empty patches, and ultracompost marks growing herb/seaweed patches that
  still need compost.

## Requirements and usage

1. Enable **PatchMaster**. Its declared dependency enables RuneLite's
   built-in **Time Tracking** plugin.
2. Install and enable **Shortest Path** from the Plugin Hub.
3. Open the PatchMaster sidebar panel.
4. Optionally click **Order** and use the arrow buttons to arrange the patch
   types for this profile, then click **Done**.
5. Click **Start run**, **Next patch**, or the arrow beside a specific patch.
6. Follow the route and perform every teleport, movement, and farming action
   yourself.

Patch highlights are visual-only: a thin outline is ripe green for ready crops,
tilled-soil tan for empty patches, muted red for dead crops, harvest-gold for
diseased crops, and water blue when a growing patch still needs compost. Item
reminders use secateurs, planting items, a spade, plant cure, or ultracompost so
the states remain distinguishable without relying on color alone. Every color
can be adjusted under **Scene highlights** in the plugin configuration.

Use the collapsed patch-type sections in the plugin configuration to tailor the
panel, overlays, and run route to your unlocks. Each section has an **Include**
switch followed by its individual destinations. The sidebar **Order** editor
saves a separate patch-type sequence for each RuneLite profile; disabled types
keep their position for later re-enabling. **Skip empty without planting items**
is optional; when enabled, the route planner skips an empty seeded patch if its
corresponding seed, sapling, or spore is not in the inventory.

Time Tracking only knows a patch after the account has visited a region where
RuneLite can observe it. Unknown patches can be included in a run specifically
to populate that data.

PatchMaster learns storage counts when the Bank or Seed Vault item container is
open. Open each storage once per account to create its first snapshot. The last
complete snapshot remains available after the storage closes and is replaced
whenever RuneLite observes a changed container. A `?` beside Bank or Vault
means that storage has not been observed for the current RuneScape profile yet.

## Scope

The Plugin Hub already has broader farming plugins that manage supplies,
teleport preferences, or scene highlights. PatchMaster is deliberately
narrower: it is a Time Tracking view and Shortest Path controller for the
supported farming-run patch families. It does not duplicate inventory loadouts,
interaction highlighting, profit tracking, or custom teleport logic.

## Rule-compliance boundary

The plugin is intentionally limited to information and visual navigation:

- It does not click, move the mouse, press keys, invoke menu actions, swap menu
  entries, select teleports, or interact with game objects or widgets.
- It does not automate farming or movement. Every game action remains a direct
  player action.
- It does not use reflection, JNI, subprocesses, external network services, or
  runtime downloads.
- It reads RuneLite configuration and posts the public `shortestpath/path` or
  `shortestpath/clear` `PluginMessage`; Shortest Path owns all route rendering.
- Saved seed stock stays in RuneLite's profile-scoped configuration; PatchMaster
  does not read another plugin's files or send the counts anywhere.
- It is Java 11 source with no third-party runtime dependencies.

These boundaries follow the [RuneLite Plugin Hub review
criteria](https://github.com/runelite/plugin-hub), RuneLite's [rejected-feature
guidance](https://github.com/runelite/runelite/wiki/Rejected-or-Rolled-Back-Features),
and Jagex's [macro and client feature
rules](https://legal.jagex.com/docs/rules/macro-and-client-features-not-permitted).
Plugin Hub review is still the final authority for distribution.

## Development

Requirements: JDK 11 or newer.

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

`build` produces the standard unbundled plugin JAR. The `run` task uses the
test classpath only to launch a local RuneLite development client.

The `run` task starts a RuneLite development client with Farm Run Helper loaded.

Before submitting to the Plugin Hub, publish this directory as the root of its
own public GitHub repository and follow the Plugin Hub's current submission
instructions.
