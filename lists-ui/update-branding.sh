#!/usr/bin/env bash
set -e -o pipefail

# export AWS_PROFILE=inbo-dev
export AWS_PROFILE=inbo-prod

bun run build:production

aws s3 sync \
  ./dist \
  s3://inbo-vbp-prod-branding/lists
# s3://inbo-vbp-dev-branding/lists
