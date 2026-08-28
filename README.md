# Freyja's Flora

RuneLite farming and birdhouse run planner. Uses Time Tracking data and sends
destinations to the Shortest Path plugin.

## Features

- 45 farming and birdhouse locations
- Herb, hops, tree, fruit tree, hardwood, calquat, coral, seaweed, and
  birdhouse runs
- Patch states, readiness estimates, scene highlights, and supply snapshots
- Run order, location filters, and per-patch routing

## Use

Enable **Freyja's Flora**, **Time Tracking**, and **Shortest Path**, then open
the Freyja's Flora sidebar.

Time Tracking learns a location after it has been observed. Bank and Seed Vault
counts are saved snapshots; open each storage once to populate them.

## Development

Requires JDK 11 or newer.

```powershell
.\gradlew.bat build
.\gradlew.bat run
```
