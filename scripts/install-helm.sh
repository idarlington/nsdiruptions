#!/bin/bash

sh ./scripts/update-helm-dep.sh
helm install ns-disruptions ./helm --set scraper.apiKey="$SCRAPER_AUTH_KEY"
