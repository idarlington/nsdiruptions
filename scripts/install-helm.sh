helm dependency update helm/charts/kafka && \
helm dependency update helm/charts/base-app && \
helm dependency update helm/ && \
helm install ns-disruptions ./helm --set scraper.apiKey="$SCRAPER_AUTH_KEY"
