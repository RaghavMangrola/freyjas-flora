# Third-party notices

PatchMaster reads the storage formats and farming-varbit/birdhouse-varp state mappings used
by RuneLite's built-in Time Tracking plugin. Those mappings are derived from
the RuneLite project, which is distributed under the BSD 2-Clause license.

Relevant upstream files:

- `runelite-client/.../plugins/timetracking/farming/FarmingTracker.java`
- `runelite-client/.../plugins/timetracking/farming/FarmingWorld.java`
- `runelite-client/.../plugins/timetracking/farming/PatchImplementation.java`
- `runelite-client/.../plugins/timetracking/farming/Produce.java`
- `runelite-client/.../plugins/timetracking/hunter/BirdHouse.java`
- `runelite-client/.../plugins/timetracking/hunter/BirdHouseSpace.java`
- `runelite-client/.../plugins/timetracking/hunter/BirdHouseState.java`
- `runelite-client/.../plugins/timetracking/hunter/BirdHouseTracker.java`
- `runelite-client/.../plugins/worldmap/FarmingPatchLocation.java`

RuneLite copyright notices remain with the RuneLite contributors. See
<https://github.com/runelite/runelite/blob/master/LICENSE>.

The Shortest Path integration uses its public `PluginMessage` protocol and does
not copy or bundle Shortest Path code. See
<https://github.com/Skretzo/shortest-path>.

Bank and Seed Vault snapshot handling follows the item-container and
RuneScape-profile persistence approach used by Dude, Where's My Stuff?, which is
distributed under the BSD 2-Clause license. PatchMaster contains its own small,
feature-specific implementation and does not bundle that plugin. See
<https://github.com/Thource/dude-wheres-my-stuff>.
