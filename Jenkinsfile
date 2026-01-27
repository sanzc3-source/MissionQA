pipeline {
    agent any

    triggers {
        cron('1 0 * * *')  // 12:01 AM daily
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        choice(
            name: 'RUN_MODE',
            choices: ['ALL', 'CUSTOM', 'UI_REGRESSION', 'API_REGRESSION'],
            description: 'ALL=@ui or @api. CUSTOM=use TAGS. UI_REGRESSION=@ui and @regression (chrome+firefox). API_REGRESSION=@api and @regression (once).'
        )
        string(
            name: 'TAGS',
            defaultValue: '@ui or @api',
            description: 'Used when RUN_MODE=CUSTOM'
        )
        choice(
            name: 'BROWSERS',
            choices: ['chrome', 'firefox', 'both'],
            description: 'Used for UI runs (CUSTOM/UI_REGRESSION). API ignores this.'
        )
    }

    environment {
        IS_TIMER = "false"
    }

    stages {
        stage('Init') {
            steps {
                script {
                    // Sandbox-safe: Jenkins sets this on cron-triggered builds
                    env.IS_TIMER = (env.BUILD_CAUSE_TIMERTRIGGER == 'true') ? 'true' : 'false'
                    echo "IS_TIMER=${env.IS_TIMER} BUILD_CAUSE_TIMERTRIGGER=${env.BUILD_CAUSE_TIMERTRIGGER}"
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Nightly (cron)') {
            when { expression { env.IS_TIMER == 'true' } }
            steps {
                sh '''
                  set -e

                  run_once() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"

                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true

                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true

                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                  }

                  # 1) API regression once
                  run_once "api" "@api and @regression" "selenium/standalone-chrome:latest" "chromeheadless"

                  # 2) UI regression chrome + firefox
                  run_once "ui-chrome" "@ui and @regression" "selenium/standalone-chrome:latest" "chromeheadless"
                  run_once "ui-firefox" "@ui and @regression" "selenium/standalone-firefox:latest" "firefoxheadless"
                '''
            }
        }

        stage('Manual') {
            when { expression { env.IS_TIMER != 'true' } }
            steps {
                sh '''
                  set -e

                  run_once() {
                    SUFFIX="$1"; TAGS="$2"; SELENIUM_IMAGE="$3"; BROWSER="$4"

                    export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-${SUFFIX}"

                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                    rm -f artifacts/cucumber.html artifacts/cucumber.json || true

                    docker compose -p "$COMPOSE_PROJECT_NAME" build

                    TAGS="$TAGS" BROWSER="$BROWSER" SELENIUM_IMAGE="$SELENIUM_IMAGE" \
                      docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit

                    mkdir -p "artifacts/$SUFFIX"
                    mv artifacts/cucumber.html "artifacts/$SUFFIX/cucumber.html" || true
                    mv artifacts/cucumber.json "artifacts/$SUFFIX/cucumber.json" || true

                    docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                  }

                  MODE="${RUN_MODE}"
                  TAG_EXPR="${TAGS}"

                  if [ "$MODE" = "ALL" ]; then
                    TAG_EXPR='@ui or @api'
                  elif [ "$MODE" = "UI_REGRESSION" ]; then
                    TAG_EXPR='@ui and @regression'
                  elif [ "$MODE" = "API_REGRESSION" ]; then
                    TAG_EXPR='@api and @regression'
                  fi

                  # If API-only expression
                  if echo "$TAG_EXPR" | grep -q '@api' && ! echo "$TAG_EXPR" | grep -q '@ui'; then
                    run_once "manual-api" "$TAG_EXPR" "selenium/standalone-chrome:latest" "chromeheadless"
                    exit 0
                  fi

                  # UI runs
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
            sh 'echo "=== DEBUG artifacts ===" && find artifacts -maxdepth 2 -type f -name "cucumber.*" -print || true'

            // Aggregate all JSON files (nightly + manual)
            cucumber(fileIncludePattern: 'artifacts/**/cucumber.json')

            // Publish separate HTML links (won’t fail if missing)
            publishHTML([reportName: 'API HTML',        reportDir: 'artifacts/api',        reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'UI Chrome HTML',  reportDir: 'artifacts/ui-chrome',  reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'UI Firefox HTML', reportDir: 'artifacts/ui-firefox', reportFiles: 'cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
            publishHTML([reportName: 'Manual HTML',     reportDir: 'artifacts',            reportFiles: '**/cucumber.html', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
        }
    }
}
