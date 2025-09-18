#!/bin/sh
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; else echo 'No gradle installed; workflow will download Gradle on runner.'; fi
