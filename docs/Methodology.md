Writing Minecraft plugins (and programs in general) is all about dealing with entrypoints.
Entry points tell you all the ways that logic may start executing in your program.
Some entry point types are redundant with others, but remember, we're concerned first
and foremost with how logic is organized, so they are not redundant in that sense.

A Paper Minecraft plugin has the following notable logic starting points:
1. Commands
2. Events
3. Packets
4. External requests like web or cron
5. Plugin start
6. Plugin stop

It is easy to get overwhelmed with plugin behavior if entrypoints are TOO decoupled. Particularly, there
is a danger with listening to the same events or packets in multiple places. This leads to
a combinatorial explosion of possible execution paths, and makes it very difficult to
reason about the behavior of the program. Therefore this plugin utilizes the following structure:

All events are listened to ONCE, via a listener in the `listeners` package. This listener
delegates to other systems as necessary. This ensures that the order of event processing
is well-defined and easy to understand.