# Unreleased

# 2.1.0

- Added support for Minecraft 26.2
- Added faststats metrics
- Added an option to disable the version checker (`enable-version-check` in `config.yml`)
- Stacked item displays now fall back to the item's vanilla `item_name` component when the item has no custom display name (Minecraft 1.20.5 and above)
- Server version detection now resolves unknown or future version strings — such as Minecraft 26.x, which drops the `1.` prefix — to the most recent supported behaviour instead of the closest numeric match
- Fixed the jar reporting a version different from the one it was built from
