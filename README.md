# ChaosSquad MCLIB
This is our library for our minecraft plugins.  
It contains most classes required by multiple plugins that we have created.
### Current Features
- BlockBox: like BoundingBox but only for blocks.
- BlockStructure: Saves blocks and their data in a relative coordinate system for copying and pasting blocks from/to the world.
- CombatTracking: Tracks combat to give the player with most kills the kill, and all other players that helped assists.
- SubcommandCommand: A command multiple other bukkit commands can be added to as subcommands.
- PacketEntityManager: Create, remove and manage packet entities.
- PacketEventHandler: Sends bukkit events for incoming and outgoing packets.
- TaskScheduler: Task scheduler for game modes that cannot use the Bukkit Scheduler because they can be replaced during the plugin runtime.
- ChatUtils: Chat-related utilities
- WorldUtils: World-related utilities
- MiscUtils: Other utilities
### How to use
[coming soon]
### Version Support
This lib only supports one specific version, but most of the non-NMS-features should also work on other versions.
