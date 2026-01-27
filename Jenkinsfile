pipeline {
    agent any

    triggers {
        cron('1 0 * * *')   // 12:01 AM daily
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('API Regression (@api and @regression)') {
            steps {
                sh '''
                  set -e
                  export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-api"
                  trap 'docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true' EXIT

                  docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                  docker compose -p "$COMPOSE_PROJECT_NAME" build

                  TAGS='@api and @regression' \
                  BROWSER=chromeheadless \
                  SELENIUM_IMAGE=selenium/standalone-chrome:latest \
                  docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit
                '''
            }
        }

        stage('UI Regression - Chrome (@ui and @regression)') {
            steps {
                sh '''
                  set -e
                  export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-ui-chrome"
                  trap 'docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true' EXIT

                  docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                  docker compose -p "$COMPOSE_PROJECT_NAME" build

                  TAGS='@ui and @regression' \
                  BROWSER=chromeheadless \
                  SELENIUM_IMAGE=selenium/standalone-chrome:latest \
                  docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit
                '''
            }
        }

        stage('UI Regression - Firefox (@ui and @regression)') {
            steps {
                sh '''
                  set -e
                  export COMPOSE_PROJECT_NAME="missionqa-${BUILD_NUMBER}-ui-firefox"
                  trap 'docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true' EXIT

                  docker compose -p "$COMPOSE_PROJECT_NAME" down -v --remove-orphans || true
                  docker compose -p "$COMPOSE_PROJECT_NAME" build

                  TAGS='@ui and @regression' \
                  BROWSER=firefoxheadless \
                  SELENIUM_IMAGE=selenium/standalone-firefox:latest \
                  docker compose -p "$COMPOSE_PROJECT_NAME" up --abort-on-container-exit
                '''
            }
        }
    }

    post {
        always {
            // Debug: show what Jenkins has in workspace artifacts
            sh 'echo "=== DEBUG: artifacts directory ===" && ls -lah artifacts || true'

            // Cucumber "Test Result" section
            cucumber(fileIncludePattern: 'artifacts/cucumber.json')

            // HTML report link (single file)
            publishHTML([
                reportName: 'Cucumber HTML Report',
                reportDir: 'artifacts',
                reportFiles: 'cucumber.html',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
    }
}
