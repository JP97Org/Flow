#!/usr/bin/env sh
set -eu

if [ -z "${PWD:-}" ]; then
  echo "PWD is not set" >&2
  exit 1
fi

docker run --rm -it \
  -v "$PWD":/workspace \
  -w /workspace \
  -p 8080:8080 \
  maven:3.9.16-amazoncorretto-21 \
  mvn quarkus:dev -Dquarkus.http.host=0.0.0.0 "$@"
