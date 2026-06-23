#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
exec "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@" 2>/dev/null || \
  java -cp "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"
