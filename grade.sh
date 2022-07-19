#!/usr/bin/env bash
# This script will prepare and grade the submission.
# Runs inside the grader container specified in docker-compose.grading.yml
# The LMS will run it exactly the same way.
set -ex

modules=("claims" "expenses" "ratings" "web")
version=1.0

clean() {
  mvn clean
}

test_module() {
  pushd $MODULE
  mvn package
  popd
}

preflight_e2e() {
  for m in $modules; do
    jar_file="./$m/target/$m-$version.jar"
    [[ ! -e $jar_file ]] &&
      echo "!!! [grading#preflight] !!! Module not built successfully: $jar_file" && \
      exit 1
  done
}

main() {
  export MODULE=$1

  clean
  test_module
  preflight_e2e
}

main $@
