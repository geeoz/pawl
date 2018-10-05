#!/bin/bash
set -ex

export GECKODRIVER_VERSION=v0.23.0
curl -L -o geckodriver.tar.gz https://github.com/mozilla/geckodriver/releases/download/${GECKODRIVER_VERSION}/geckodriver-${GECKODRIVER_VERSION}-linux64.tar.gz
gunzip -c geckodriver.tar.gz | tar xopf -
chmod +x geckodriver && sudo mv geckodriver /usr/local/bin