🚀 MissionQA – API & UI Automation Framework (Docker + Jenkins)
📌 Overview

This repository contains a production-grade test automation framework built using modern QA best practices and designed for local execution, CI pipelines, and nightly regression.

🧰 Tech Stack

Java + Maven

Cucumber (BDD)

Selenium WebDriver

REST API testing

Docker & Docker Compose

Jenkins (parameterized + nightly CI)

✅ What This Framework Supports

API-only test execution

UI-only test execution

Combined UI + API execution

Multi-browser UI testing (Chrome & Firefox)

Local Docker execution (no local Java/Selenium setup required)

Fully automated nightly CI regression

🧠 This project was originally provided as an assessment and has since been heavily refactored and extended to demonstrate real-world automation ownership, CI design, and test stability best practices.

🧱 Project Structure
MissionQA
├── artifacts/                     # Generated test reports (HTML + JSON)
│
├── src
│   └── test
│       ├── java
│       │   └── com
│       │       └── missionqa
│       │           ├── api
│       │           │   ├── client        # API clients / request handling
│       │           │   ├── models        # API request/response models
│       │           │   └── steps         # API step definitions
│       │           │
│       │           ├── ui
│       │           │   ├── pages         # Page Object Model (POM)
│       │           │   └── steps         # UI step definitions
│       │           │
│       │           ├── hooks             # Cucumber hooks (setup / teardown)
│       │           ├── runners           # Cucumber runners
│       │           ├── core              # Shared utilities / drivers
│       │           └── config            # Framework configuration
│       │
│       └── resources
│           ├── features
│           │   ├── api
│           │   │   └── API-Test.feature
│           │   └── ui
│           │       └── UI-Test.feature
│           │
│           └── config.properties         # Runtime configuration
│
├── docker-compose.yml              # Selenium + test execution services
├── Dockerfile                      # Test execution image
├── Jenkinsfile                     # CI pipeline (manual + nightly)
├── BUGS_FOUND.md                   # Documented bugs & findings
├── pom.xml                         # Maven dependencies & plugins
├── .gitignore
└── README.md

🧪 Test Coverage
🔹 API Tests

Target: https://reqres.in/

Feature File:

src/test/resources/features/api/API-Test.feature


Validates:

CRUD operations

HTTP response codes

Request/response payload validation

🔹 UI Tests

Target: https://www.saucedemo.com/

Feature File:

src/test/resources/features/ui/UI-Test.feature


Implemented Using:

Page Object Model (POM)

Explicit waits (no flaky sleeps)

Browser-agnostic test design

🐳 Running Tests Locally (Docker)
🔧 Prerequisites

Docker

Docker Compose

❌ Java not required locally (runs inside containers)

▶️ Run ALL tests (UI + API)
docker compose up --build

▶️ Run UI tests only (Chrome)
TAGS="@ui" BROWSER=chromeheadless docker compose up --build

▶️ Run UI tests only (Firefox)
TAGS="@ui" BROWSER=firefoxheadless docker compose up --build

▶️ Run API tests only
TAGS="@api" docker compose up --build

▶️ Run Regression Suites
# UI regression
TAGS="@ui and @regression" docker compose up --build

# API regression
TAGS="@api and @regression" docker compose up --build

📊 Test Reports

After execution, reports are generated under:

artifacts/
├── cucumber.html
└── cucumber.json

In Jenkins:

Reports are aggregated into Cucumber Reports

Separate HTML links are published per browser

UI executions are clearly labeled:

UI Chrome

UI Firefox

🤖 Jenkins CI Pipeline
🔁 Nightly Execution (12:01 AM)

Triggered automatically via cron:

API Regression
@api and @regression


Runs once (no browser dependency)

UI Regression
@ui and @regression


Runs on Chrome

Runs on Firefox

▶️ Manual Jenkins Runs (Build with Parameters)

The pipeline is fully parameterized and supports on-demand execution.

Parameters

RUN_MODE

ALL → @ui or @api

UI_REGRESSION → @ui and @regression

API_REGRESSION → @api and @regression

CUSTOM → Uses TAGS

TAGS

Used only when RUN_MODE=CUSTOM

BROWSERS

chrome

firefox

both (UI only)

🔎 Examples
Goal	RUN_MODE	TAGS	BROWSERS
UI regression	UI_REGRESSION	(blank)	both
API regression	API_REGRESSION	(blank)	chrome
Everything	ALL	@ui or @api	both
Custom UI	CUSTOM	@ui	chrome
🧠 Why Features Appear Twice in Reports

When UI tests run on multiple browsers, the same feature executes once per browser.

Example:

SauceDemo checkout calculations [UI Chrome]
SauceDemo checkout calculations [UI Firefox]

This is intentional and provides:

Browser parity visibility

Clear execution separation

Accurate regression tracking

🐞 Known Issues

All identified bugs, quirks, and improvements are documented in:

BUGS_FOUND.md


Includes:

UI defects

Test stability issues

CI reliability improvements