#!/bin/bash
set -ex

export CHROME_DRIVER_VERSION=2.42
curl -L -o chromedriver.zip http://chromedriver.storage.googleapis.com/${CHROME_DRIVER_VERSION}/chromedriver_linux64.zip
gunzip -c chromedriver.zip | tar xopf -
chmod +x chromedriver && sudo mv chromedriver /usr/local/bin