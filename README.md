# Accurate Shield Disable

Automatically times axe attacks on combat swaps to improve shield-disable consistency in PvP.

## What it does

- Detects when you swap to an axe
- Opens a configurable attack window after swap
- Retries attacks automatically within that window
- Bypasses attack cooldown via mixin for maximum reliability
- Respects ping compensation and input buffering

## Best Settings for Maximum Accuracy

| Setting | Recommended Value | Description |
|---------|------------------|-------------|
| `enabled` | `true` | Master toggle |
| `autoAttackOnSwap` | `true` | Enable auto-attack after swap |
| `attackWindowTicks` | `15-20` | How long to keep retrying (higher = more lenient) |
| `predictionStrength` | `HIGH` | Number of attack attempts per window (1-3) |
| `swapOffset` | `0-2` | Advance/delay window start in ticks |
| `pingCompensationEnabled` | `true` | Adjust timing for your latency |
| `latencyMultiplier` | `100-150` | Higher = more ping compensation |
| `inputBufferEnabled` | `true` | Catch swap+attack inputs |
| `bufferLength` | `3` | Input buffer window in ticks |

### Why these settings
- `attackWindowTicks = 15-20` covers swap animation + cooldown + target acquisition
- `predictionStrength = HIGH` sends 3 attacks to guarantee at least one lands
- `latencyMultiplier = 100-150` compensates for network delay without overshooting

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

## How It Works

1. You swap to an axe (default key: `F`)
2. The mod opens an attack window (`attackWindowTicks`)
3. While the window is open and you're looking at an entity:
   - It bypasses attack cooldown via mixin
   - It sends attacks automatically
   - It retries based on `predictionStrength`
4. If the first attack misses or is blocked, subsequent attempts land during the window

## Configuring

All settings are available via Mod Menu / Cloth Config in-game.

## License

MIT. See `LICENSE` for details.
