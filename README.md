<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/Dani-error/lumina/refs/heads/main/.github/assets/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Dani-error/lumina/refs/heads/main/.github/assets/logo-light.svg">
  <img alt="Fallback logo image" src="https://raw.githubusercontent.com/Dani-error/lumina/refs/heads/main/.github/assets/logo-dark.svg">
</picture>

<br/>

## Features

- **Supports Bukkit & forks** (incl. Folia) via **ProtocolLib** or **PacketEvents**
- **Fake skin & ping** support
- **Client support**: 1.7.10+ | **Server**: 1.8+
- **Character limit**: 48 (≤ 1.12)
- **Header/Footer** with multiline support
- **Animations** for scoreboard & tablist
- **No flickering**
- **Kotlin DSL** with full type safety

---

## Usage

<details open>
  <summary><strong>Gradle (Kotlin DSL)</strong></summary>

  ```kotlin
  repositories {
      mavenCentral()
  }

  dependencies {
      implementation("io.github.dani-error.lumina:<module>:1.0.0")
  }
  ```

_Replace `<module>` with one of the modules listed below._
</details>

<details>
  <summary><strong>Maven</strong></summary>

  ```xml
  <dependency>
    <groupId>io.github.dani-error.lumina</groupId>
    <artifactId>MODULE</artifactId>
    <version>1.0.0</version>
  </dependency>
  ```

_Replace `MODULE` with one of the modules listed below._
</details>

<details>
  <summary><strong>Gradle (Groovy DSL)</strong></summary>

  ```groovy
  repositories {
      mavenCentral()
  }

  dependencies {
      implementation 'io.github.dani-error.lumina:<module>:1.0.0'
  }
  ```

_Replace `<module>` with one of the modules listed below._
</details>
<br/>
<details>
  <summary><strong>Additional Repositories</strong></summary>

You may need to add the following if you rely on transitive dependencies:
- `https://repo.papermc.io/repository/maven-public/` (PaperLib)
- `https://repository.derklaro.dev/releases/` (ProtocolLib via Derklaro’s repo; can also use JitPack)
- `https://repo.codemc.io/repository/maven-releases/` (PacketEvents)
- `https://s01.oss.sonatype.org/content/repositories/snapshots/` (for snapshot-only dependencies)
</details>

<details>
  <summary><strong>Shading</strong></summary>

To avoid conflicts when multiple plugins ship the same dependencies, shade/relocate these packages:
- `io.papermc.lib`
- `io.github.retrooper`
- `com.github.retrooper`
- `dev.dani.lumina`
</details>


---

## Modules

| Module Name | Artifact ID | Description                                                   |
|-------------|-------------|---------------------------------------------------------------|
| API         | `api`       | Core Lumina API (no platform-specific code).                  |
| Common      | `common`    | Abstract API implementations for building new platforms.      |
| Bukkit      | `bukkit`    | Full Bukkit (and forks) implementation—includes API & Common. |

All of them published in [maven central](https://central.sonatype.com/search?q=io.github.dani-error.lumina).

---

## Documentation

Full API reference and usage examples are available on the [docs site](https://github.com/Dani-error/lumina/wiki).

## Contributing

Contributions are welcome! To get started:

1. Fork this repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature/<your-feature>
   ```
3. Make your changes in Kotlin (follow existing style).
4. Commit and push to your fork, then open a Pull Request against `main`.

Please include a short description of your changes. For larger features, open an issue first to discuss.

---

## License

This project is MIT-licensed. See [LICENSE](./LICENSE) for details.  
