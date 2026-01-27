pipeline {
    agent any

    triggers {
        cron('1 0 * * *')   // 12:01 AM daily
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        choice(
            name: 'RUN_MODE',
            choices: ['ALL', 'CUSTOM', 'UI_REGRESSION', 'API_REGRESSION'],
            description: 'ALL = @ui or @api. CUSTOM = run TAGS on selected browser(s). UI_REGRESSION = @ui and @regression. API_REGRESSION = @api and @regression.'
        )
        string(
            name: 'TAGS',
            defaultValue: '@ui or @api',
            description: 'Only used when RUN_MODE=CUSTOM (or override). Example: @ui and @regression'
        )
        choice(
            name: 'BROWSERS',
            choices: ['chrome', 'firefox', 'both'],
            description: 'Only used for UI runs (CUSTOM / UI_REGRESSION). API ignores this.'
        )
    }

    environment {
        IS_TIMER = "false"
    }

    stage('Init') {
        steps {
            script {
                env.IS_TIMER = (env.BUILD_CAUSE_TIMERTRIGGER == 'true') ? 'true' : 'false'
                echo "IS_TIMER = ${env.IS_TIMER} (BUILD_CAUSE_TIMERTRIGGER=${env.BUILD_CAUSE_TIMERTRIGGER})"
            }
        }
    }

        stage('Checkout') {
            steps { checkout scm }
        }

        // -------------------------
        // NIGHTLY (cron) RUNS
        // -------------------------

        stage('NIGHTLY - API (@api and @regression)') {
            when { expression { env.IS_TIMER == "true" } }
            steps {
                sh '''
                  set -e
                  run() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"
                    cleanup() { docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true; }
                    trap cleanup EXIT

                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true
                  }

                  run "api" "@api and @regression" "selenium/standalone-chrome:latest" "chromeheadless"
                '''
            }
        }

        stage('NIGHTLY - UI Chrome (@ui and @regression)') {
            when { expression { env.IS_TIMER == "true" } }
            steps {
                sh '''
                  set -e
                  run() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"
                    cleanup() { docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true; }
                    trap cleanup EXIT

                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true
                  }

                  run "ui-chrome" "@ui and @regression" "selenium/standalone-chrome:latest" "chromeheadless"
                '''
            }
        }

        stage('NIGHTLY - UI Firefox (@ui and @regression)') {
            when { expression { env.IS_TIMER == "true" } }
            steps {
                sh '''
                  set -e
                  run() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"
                    cleanup() { docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true; }
                    trap cleanup EXIT

                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true
                  }

                  run "ui-firefox" "@ui and @regression" "selenium/standalone-firefox:latest" "firefoxheadless"
                '''
            }
        }

        // -------------------------
        // MANUAL RUNS (UI clicks)
        // -------------------------

        stage('MANUAL - Run Selected Suite') {
            when { expression { env.IS_TIMER != "true" } }
            steps {
                sh '''
                  set -e

                  run_once() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"
                    cleanup() { docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true; }
                    trap cleanup EXIT

                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true
                  }

                  # Resolve suite -> TAG expression
                  MODE="${RUN_MODE}"
                  TAG_EXPR="${TAGS}"

                  if [ "$MODE" = "ALL" ]; then
                    TAG_EXPR='@ui or @api'
                  elif [ "$MODE" = "UI_REGRESSION" ]; then
                    TAG_EXPR='@ui and @regression'
                  elif [ "$MODE" = "API_REGRESSION" ]; then
                    TAG_EXPR='@api and @regression'
                  fi

                  # API run (browser irrelevant)
                  if echo "$TAG_EXPR" | grep -q '@api'; then
                    run_once "manual-api" "$TAG_EXPR" "selenium/standalone-chrome:latest" "chromeheadless"
                    exit 0
                  fi

                  # UI run: choose browser(s)
                  if [ "${BROWSERS}" = "both" ]; then
                    run_once "manual-ui-chrome" "$TAG_EXPR" "selenium/standalone-chrome:latest" "chromeheadless"
                    run_once "manual-ui-firefox" "$TAG_EXPR" "selenium/standalone-firefox:latest" "firefoxheadless"
                  elif [ "${BROWSERS}" = "firefox" ]; then
                    run_once "manual-ui-firefox" "$TAG_EXPR" "selenium/standalone-firefox:latest" "firefoxheadless"
                  else
                    run_once "manual-ui-chrome" "$TAG_EXPR" "selenium/standalone-chrome:latest" "chromeheadless"
                  fi
                '''
            }
        }
    }

    post {
        always {
            sh 'echo "=== DEBUG: artifacts tree ===" && find artifacts -maxdepth 2 -type f -name "cucumber.*" -print || true'

            // Aggregate ALL json files from all stages
            cucumber(fileIncludePattern: 'artifacts/**/cucumber.json')

            // Publish HTML reports (separate links)
            publishHTML([reportName: 'API HTML',       reportDir: 'artifacts/api',        reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'UI Chrome HTML', reportDir: 'artifacts/ui-chrome',  reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'UI Firefox HTML',reportDir: 'artifacts/ui-firefox', reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'Manual HTML',    reportDir: 'artifacts',           reportFiles: '**/cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
        }
    }
}