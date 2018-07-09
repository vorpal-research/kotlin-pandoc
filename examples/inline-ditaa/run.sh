#!/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java -jar $DIR/target/inline-ditaa-filter-0.0.1-fat.jar
