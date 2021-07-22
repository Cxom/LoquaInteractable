# LoquaInteractable
Interactable features and backend for Loqua City and the associated GTA-inspired gamemode within Minecraft.

This plugin is the backbone and testing ground of interactable features in Loqua. Particularly, it maintains the tools and APIs useful to specific interactable objects,
as implemented within LoquaInteractable itself, or other plugins dependent on Loqua-Interactable.

It depends on the following other plugins:
* [Cxom/PersistentMetadata](https://github.com/Cxom/PersistentMetadata) - A library/database wrapper for persisting data to particular blocks.
* [Cxom/PunchTree-Util](https://github.com/Cxom/PunchTree-Util) - Common code useful across different Minecraft projects
* [dmulloy2/ProtocolLib](https://github.com/dmulloy2/ProtocolLib/) - Minecraft packet API

So far implemented in LoquaInteractable is:
* In-game metadata viewing and editing 
* A block highlighting API
* Utility commands for working with custom model data, nbt, and ui experiments
* Player-specific wrappers for inputs (input processing currently being worked on)

Upcoming additions include:
* API for building custom items
* Input processing
* API for building custom interaction listeners, so as to respond to certain key combinations or a set of inputs only while a particular item is being held
* Lights and lightswitches
