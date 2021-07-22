# LoquaInteractable
Interactable features and backend for Loqua and GTA

This plugin is the backbone and testing ground of interactable features in Loqua. Particularly, it maintains the tools and APIs useful to specific interactable objects,
as implemented within LoquaInteractable itself, or other plugins dependent on Loqua-Interactable.

It depends on the following other plugins:
* [Cxom/PersistentMetadata](https://github.com/Cxom/PersistentMetadata) - A library/database wrapper for persisting data to particular blocks.
* [Cxom/PunchTree-Util](https://github.com/Cxom/PunchTree-Util) - Common code useful across different Minecraft projects
* [dmulloy2/ProtocolLib](https://github.com/dmulloy2/ProtocolLib/) - Minecraft packet api

So far implemented in LoquaInteractable is:
* In-game metadata viewing and editing 
* A block highlighting api
* Utility commands for working with custom model data, nbt, and ui experiments
* Player-specific wrappers for inputs (input processing currently being worked on)
