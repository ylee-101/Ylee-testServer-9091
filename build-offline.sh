#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "$0")" && pwd)"
bundled_jdk="$project_dir/offline-tools/jdk-21.0.7+6"
gradle_zip="$project_dir/offline-tools/gradle-9.7.1-bin.zip"
gradle_user_home="$project_dir/.offline-gradle-home"
distribution_dir="$gradle_user_home/wrapper/dists/gradle-9.7.1-bin/1w1c7tv4s851m17nbqdsro2tv"

assemble_split_file() {
  local destination="$1"
  local parts=("${destination}.part-"*)

  if [[ ${#parts[@]} -eq 0 || ! -e "${parts[0]}" ]]; then
    echo "Missing split bundle for: $destination" >&2
    exit 1
  fi

  if [[ ! -f "$destination" || "${parts[0]}" -nt "$destination" ]]; then
    cat "${parts[@]}" > "$destination"
  fi
}

assemble_split_file "$gradle_zip"
assemble_split_file "$bundled_jdk/lib/modules"

if [[ -x "$bundled_jdk/bin/java" ]]; then
  export JAVA_HOME="$bundled_jdk"
elif [[ "$(java -version 2>&1 | head -n 1)" != *'"21.'* ]]; then
  echo "JDK 21 is required. Install it or restore offline-tools/jdk-21.0.7+6." >&2
  exit 1
fi

if [[ ! -f "$gradle_zip" ]]; then
  echo "Missing bundled Gradle distribution: $gradle_zip" >&2
  exit 1
fi

mkdir -p "$distribution_dir"
cp "$gradle_zip" "$distribution_dir/gradle-9.7.1-bin.zip"

export GRADLE_USER_HOME="$gradle_user_home"
exec "$project_dir/gradlew" --offline -PofflineMavenOnly=true --rerun-tasks build "$@"
