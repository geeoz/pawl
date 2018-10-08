#!/bin/bash
set -ev

LOG_FOLDER="${TRAVIS_BUILD_NUMBER}/${TRAVIS_JOB_NUMBER}"
WEBDAV="https://webdav.yandex.ru"
WEBDAV_USER="${YANDEX_USER}:${YANDEX_KEY}"
WEBDAV_FOLDER="/Travis-CI/PAWL/${LOG_FOLDER}"
SLACK="true"

# Internal variable
_slack_notification="false"

create_webdav_folders () {
  echo "Check Webdav folders ${WEBDAV}${dest}"
  dest=$1

  OLD_IFS="$IFS"
  IFS='/'
  folders=( $dest )
  IFS="$OLD_IFS"
  for folder in "${folders[@]}"
  do
    if [ "${folder}" == "" ]; then
      continue
    fi

    path="${path}/${folder}"
    status=`curl --user $WEBDAV_USER "${WEBDAV}${path}" -sw "%{http_code}" -o /dev/null -I`

    if [ "${status}" == "404" ]; then
      curl --user $WEBDAV_USER -X MKCOL "${WEBDAV}${path}"
    fi
  done
}

upload_folder_to_webdav () {
  source=$1
  dest=$2
  subtree=${3:-true}
  path=""
  notify="false"

  if [ -d $source ]; then
    create_webdav_folders $dest

    echo "Upload files from ${source} to ${WEBDAV}${dest}"
    for file in ${source}/*
    do
      if [ -d $file ] && [ "${subtree}" == "true" ]; then
        dir=${file##*/}
        upload_folder_to_webdav $file "${dest}/${dir}"
      else
        curl --user $WEBDAV_USER -T $file "${WEBDAV}${dest}/${file##*/}"
      fi
    done
    _slack_notification="true"
  fi
}

notify_slack () {
  if [ "${SLACK}" == "true" ] && [ "${_slack_notification}" == "true" ]; then
    GITHUB_COMMIT=${TRAVIS_COMMIT:0:7}
    curl -X POST -H "Content-type: application/json" --data "{\"attachments\": [{\"color\": \"#4484C2\", \"text\":\"Build logs <${SLACK_YANDEX_FOLDER}/${LOG_FOLDER}|#${TRAVIS_JOB_NUMBER}> (<https://github.com/geeoz/pawl/compare/${TRAVIS_COMMIT_RANGE}|${GITHUB_COMMIT}>) of ${TRAVIS_REPO_SLUG}@${TRAVIS_BRANCH}\"}]}" $SLACK_WEBHOOK
  fi
}

upload_folder_to_webdav "/tmp/pawl-screenshots" "${WEBDAV_FOLDER}/pawl-screenshots"

notify_slack