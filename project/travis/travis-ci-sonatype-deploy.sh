#!/bin/bash
set -ev

if [ "${TRAVIS_BRANCH}" = "develop" ]; then
    sbt publish
elif [ "${TRAVIS_BRANCH}" = "master" ]; then
    sbt publishSigned
fi