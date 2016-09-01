#!/bin/bash
set -ev

if [ "${TRAVIS_BRANCH}" = "develop" ] || [ "${TRAVIS_BRANCH}" = "master" ]; then
    sbt publish
fi