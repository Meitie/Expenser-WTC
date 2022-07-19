SHELL := bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DELETE_ON_ERROR:
MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

.PHONY: help start_services grade test run

# help: @ Lists available make tasks
help:
	@egrep -oh '[0-9a-zA-Z_\.\-]+:.*?@ .*' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?@ "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' | sort

# clean: @ Clean the build
clean:
	mvn clean
	docker-compose down

# start_services: @ Starts backing services via Docker Compose
start_services:
	docker-compose up -d

# test: @ Run all tests
test:
	mvn test

clean-all:
	@ echo  "Running Ratings"; cd ratings;  mvn clean; mvn package; cd ..; echo  "Running claims"; cd claims;  mvn clean; mvn package -DskipTests; cd ..; echo  "Running expenses"; cd expenses;  mvn clean; mvn package; cd ..; echo  "Running web"; cd web;  mvn clean; mvn package; cd ..;

run-web:
	@ cd web/target; java -jar web-1.0-jar-with-dependencies.jar

run-ratings:
	@ cd ratings/target; java -jar ratings-1.0-jar-with-dependencies.jar

run-ratings-test:
	@ cd ratings; mvn test

run-claims:
	@ cd claims/target; java -jar claims-1.0-jar-with-dependencies.jar

run-expenses:
	@ cd expenses/target; java -jar expenses-1.0-jar-with-dependencies.jar

#java -jar ratings-1.0-jar-with-dependencies.jar

# run: @ Run the application locally
run: start_services
	mvn exec:java -Dexec.mainClass="za.co.wethinkcode.expenser.ExpenserServer"

# grade: @ Grade a specific scenario (usage: `make grade SCENARIO=Login`)
GRADING_FILES = grading.rsync-filter.txt

grade: STORY?=*
grade: SUBMISSION_DIR?=${PWD}
grade: MODULE=$(shell echo '$(STORY)' | tr  \[A-Z\].\* '\n' | head -n1)
grade: GRADE_CMD='./grade.sh "$(MODULE)"'
grade:
	@echo +++ Copying grading files into student submission dir: $(SUBMISSION_DIR)
	rsync -av --include-from $(GRADING_FILES) . $(SUBMISSION_DIR) --ignore-times

	@echo +++ Running grading tests
	pushd $(SUBMISSION_DIR)
	export GRADE_CMD=$(GRADE_CMD)
	export SUBMISSION_DIR=$(SUBMISSION_DIR)
	docker-compose -f docker-compose.grading.yml run grader
	popd



