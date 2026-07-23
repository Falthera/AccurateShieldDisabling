# Accurate Shield Disable

Makes Minecraft shield disabling easier and more consistent in PvP by automatically timing axe attacks when you swap to an axe.

## What it does

- Detects combat-ready weapon swaps to an axe
- Uses ping-aware prediction to time the first hit right as the target's shield goes up
- Lets you confirm reliable shield breaks instead of manually guessing the timing across distance and latency

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for **Minecraft 1.21.11**
2. Download the latest release JAR from the [Releases page](https://github.com/Falthera/AccurateShieldDisabling/releases)
3. Drop the file into your `mods/` folder

## Building from source

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

## Requirements

- Minecraft `1.21.11`
- Fabric Loader `0.18.2+`
- Fabric API
- Java 21

## License

MIT. See `LICENSE` for details.
