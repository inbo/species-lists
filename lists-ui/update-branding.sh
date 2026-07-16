#!/usr/bin/env bash
set -e -o pipefail

export AWS_PROFILE=inbo-dev

bun run build:production

aws s3 sync \
  ./dist \
  s3://inbo-vbp-dev-branding/poc/species-lists
