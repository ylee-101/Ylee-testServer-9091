# Offline build

Run `./build-offline.sh` from a fresh clone to build without network access.
The script uses the tracked `offline-maven/` repository, Gradle 9.7.1
distribution, and bundled JDK 21 instead of Gradle's normal caches.

The two files exceeding GitHub's 100 MiB regular-Git limit are stored as
tracked 95 MiB-or-smaller `*.part-*` files. `build-offline.sh` reassembles them
locally on its first run; the reconstructed files are ignored by Git.

The bundled JDK is Eclipse Temurin 21.0.7 for macOS ARM64 only. On another
operating system or CPU architecture, install a compatible JDK 21, then run
the same script; it automatically falls back to that system JDK.

```bash
./build-offline.sh
```

`offlineMavenOnly` disables remote plugin and dependency repositories. The
regular `./gradlew build` command remains available for deliberate online
dependency refreshes. After a refresh, run `./gradlew prepareOfflineMaven` and
then re-run `./build-offline.sh` before committing the updated bundle.
