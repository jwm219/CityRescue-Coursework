#!/usr/bin/env sh
#
# Apache Maven Wrapper startup script (Unix)
#
# This repository intentionally ships only the scripts + properties.
# The wrapper JAR and Maven distribution are downloaded on first run.

set -eu

BASEDIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
PROPS_FILE="$WRAPPER_DIR/maven-wrapper.properties"
JAR_FILE="$WRAPPER_DIR/maven-wrapper.jar"

# Read wrapperUrl from properties (fallback to Maven Central)
WRAPPER_URL=$(grep -E '^wrapperUrl=' "$PROPS_FILE" | cut -d= -f2- || true)
if [ -z "${WRAPPER_URL:-}" ]; then
  WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
fi

if [ ! -f "$JAR_FILE" ]; then
  mkdir -p "$WRAPPER_DIR"
  echo "Downloading Maven Wrapper JAR..." >&2
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$WRAPPER_URL" -o "$JAR_FILE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$WRAPPER_URL" -O "$JAR_FILE"
  else
    echo "ERROR: Neither curl nor wget is available to download Maven Wrapper." >&2
    exit 1
  fi
fi

if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
else
  JAVA_CMD=$(command -v java || true)
fi

if [ -z "${JAVA_CMD:-}" ]; then
  echo "ERROR: Java not found. Please install Java 17+ or set JAVA_HOME." >&2
  exit 1
fi

exec "$JAVA_CMD" ${MAVEN_OPTS:-} -classpath "$JAR_FILE" \
  -Dmaven.multiModuleProjectDirectory="$BASEDIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
