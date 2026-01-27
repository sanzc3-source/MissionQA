# MissionQA – API & UI Automation Framework

**Docker + Jenkins | Java | Cucumber | Selenium**

---

## Overview

This repository contains a **production-grade test automation framework** built using modern QA best practices and designed for:

- Local execution using Docker
- Parameterized Jenkins pipelines
- Fully automated nightly regression testing

This project was originally provided as an assessment and has since been **significantly refactored and extended** to demonstrate real-world automation ownership, CI design, and test stability practices.

---

## Tech Stack

- Java + Maven
- Cucumber (BDD)
- Selenium WebDriver
- REST API Testing
- Docker & Docker Compose
- Jenkins (parameterized + nightly CI)

---

## What This Framework Supports

- API-only test execution
- UI-only test execution
- Combined UI + API execution
- Multi-browser UI testing (Chrome & Firefox)
- Local Docker execution (no local Java/Selenium required)
- Fully automated nightly CI regression

---

## Project Structure

```text
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
│       │           ├── hooks              # Cucumber hooks (setup / teardown)
│       │           ├── runners            # Cucumber runners
│       │           ├── core               # Shared utilities / drivers
│       │           └── config             # Framework configuration
│       │
│       └── resources
│           ├── features
│           │   ├── api
│           │   │   └── API-Test.feature
│           │   └── ui
│           │       └── UI-Test.feature
│           │
│           └── config.properties          # Runtime configuration
│
├── docker-compose.yml              # Selenium + test execution services
├── Dockerfile                      # Test execution image
├── Jenkinsfile                     # CI pipeline (manual + nightly)
├── BUGS_FOUND.md                   # Documented bugs & findings
├── pom.xml                         # Maven dependencies & plugins
├── .gitignore
└── README.md

Test Coverage
API Tests

Target: https://reqres.in/

Feature File:

src/test/resources/features/api/API-Test.feature


Validates:

CRUD operations

HTTP response codes

Request/response payload validation

UI Tests

Target: https://www.saucedemo.com/

Feature File:

src/test/resources/features/ui/UI-Test.feature


Implemented Using:

Page Object Model (POM)

Explicit waits (no flaky sleeps)

Browser-agnostic design

Running Tests Locally (Docker)
Prerequisites

Docker

Docker Compose

❌ No local Java or Selenium required

Run All Tests (UI + API)
docker compose up --build

Run UI Tests Only

Chrome

TAGS="@ui" BROWSER=chromeheadless docker compose up --build


Firefox

TAGS="@ui" BROWSER=firefoxheadless docker compose up --build

Run API Tests Only
TAGS="@api" docker compose up --build

Run Regression Suites

UI Regression

TAGS="@ui and @regression" docker compose up --build


API Regression

TAGS="@api and @regression" docker compose up --build

Test Reports

After execution, reports are generated under:

artifacts/
├── cucumber.html
└── cucumber.json

Jenkins Reporting

Results are aggregated via Cucumber Reports

Separate HTML reports are published per browser

UI executions are clearly labeled (Chrome vs Firefox)

Jenkins CI Pipeline
Nightly Execution (12:01 AM)

API Regression

@api and @regression (runs once)

UI Regression

@ui and @regression

Runs on Chrome

Runs on Firefox

Manual Execution (Build with Parameters)

Parameters

Parameter	     Description
RUN_MODE	     ALL, UI_REGRESSION, API_REGRESSION, CUSTOM
TAGS	         Used only when RUN_MODE=CUSTOM
BROWSERS	     chrome, firefox, both (UI only)

Examples

Goal	           RUN_MODE	               TAGS	          BROWSERS
UI regression	   UI_REGRESSION	       (blank)	      both
API regression	   API_REGRESSION	       (blank)	      chrome
Everything	       ALL	                   @ui or @api	  both
Custom UI	       CUSTOM	               @ui	          chrome

Why Features Appear Twice in Reports

When UI tests run on multiple browsers, the same feature executes once per browser.

Example:

SauceDemo checkout calculations [UI Chrome]
SauceDemo checkout calculations [UI Firefox]


This is intentional and provides:

Browser parity visibility

Clear execution separation

Accurate regression tracking

Known Issues

All identified bugs and improvements are documented in:

BUGS_FOUND.md
