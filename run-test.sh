#!/bin/bash

# Navigate to the helloworld directory
cd "$(dirname "$0")"

# Run your integration tests
mvn clean install
mvn test
