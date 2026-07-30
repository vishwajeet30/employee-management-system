#!/usr/bin/env bash

# Stop immediately when a command fails, an undefined variable
# is used, or a pipeline command fails.
set -Eeuo pipefail

# Permanent project location on the Oracle VM.
PROJECT_DIRECTORY="/opt/eems"

echo "Starting EEMS deployment..."

cd "$PROJECT_DIRECTORY"

# Verify that the production environment file exists.
if [[ ! -f ".env" ]]; then
    echo "ERROR: /opt/eems/.env does not exist."
    echo "Create it from .env.example before deploying."
    exit 1
fi

# Download the newest Spring Boot/React image and MySQL image.
docker compose \
    --env-file .env \
    -f docker-compose.prod.yml \
    pull

# Create or update the containers.
docker compose \
    --env-file .env \
    -f docker-compose.prod.yml \
    up \
    -d \
    --remove-orphans

echo "Waiting for the application to become available..."

# Check the application repeatedly.
#
# The deployment fails if the application never becomes
# reachable from the server itself.
for attempt in {1..30}; do

    if curl --fail --silent --show-error \
        http://127.0.0.1/ > /dev/null; then

        echo "EEMS deployment completed successfully."

        docker compose \
            --env-file .env \
            -f docker-compose.prod.yml \
            ps

        # Remove unused older Docker image layers.
        docker image prune -f

        exit 0
    fi

    echo "Application is not ready yet. Check $attempt of 30."
    sleep 5
done

echo "ERROR: Application health check failed."

echo "Recent application logs:"
docker compose \
    --env-file .env \
    -f docker-compose.prod.yml \
    logs \
    --tail=200 \
    app

exit 1