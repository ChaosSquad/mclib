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
- DynamicWorldLoadingSystem: Dynamically loads and unloads worlds from a template to prevent the template world from being modified.
- ChatUtils: Chat-related utilities
- WorldUtils: World-related utilities
- MiscUtils: Other utilities
### JavaDocs
You can visit the javadocs [here](https://chaossquad.github.io/mclib).
### How to use
Add Repository:
```xml
<repository>
  <id>ChaosSquad-Repository-snapshots</id>
  <name>ChaosSquad Repository</name>
  <url>https://chaossquad.net:8443/snapshots</url>
</repository>
```

Add Dependency:
```xml
<dependency>
  <groupId>net.chaossquad</groupId>
  <artifactId>mclib</artifactId>
  <version>master-029f9e7d272da14044530a9d5699ff14a1917715</version>
</dependency>
```
### Version Support
This lib only supports one specific version, but most of the non-NMS-features should also work on other versions.
