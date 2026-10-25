## 1.1.0

- Rewritten from scratch, now available for Fabric as well as NeoForge
- Now available for 26.1.2, 26.2 and 26.3
- Existing worlds and placed items carry over
- Now requires BaguetteLib and YetAnotherConfigLib
- Config moved to `config/stickit.json` and can be edited in-game from the mod list; the old `serverconfig/stickit-server.toml` is not carried over
- Removed the `/cstickit dumprt` debug command
- Fixed the `stickit:unplaceable` item tag not working
- Fixed placing items out of reach or inside spawn protection
- Fixed ghost blocks and items when a placement was rejected
- Fixed rotating a placed tile not being saved or shown to other players
- Fixed `/setblock` and `/fill` dropping the contents of placed items
- Fixed placed items in air being waterlogged by default
- Fixed briefly colliding with placed items after a chunk loaded
- Hoppers pulling from placed items no longer keep them ticking every tick
