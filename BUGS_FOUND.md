# Bugs & Issues Found – MissionQA Assessment

This document tracks issues discovered during setup, execution, and refactoring,
including root cause analysis and verification steps.

## Pre-flight Observations (Before Test Execution)

These items were identified during initial project inspection and environment setup.
They are documented here prior to running the test suite and will be confirmed or
dismissed during execution.

### Observation 1 – Build Output Directory Present (`target/`)
**Type:** Build Hygiene  
**Risk:** Low

The `target/` directory exists locally as a result of Maven compilation.
This directory should not be committed to source control and should be ignored via `.gitignore`.

**Status:** Not yet validated during execution.

---

### Observation 2 – Package Mismatch Between Source and Compiled Tests
**Type:** Test Configuration / Cucumber  
**Risk:** High

Test classes located under: src/test/java/mission are being compiled into:  target/test-classes/qumu

This suggests a package declaration mismatch in one or more test classes.
This may result in Cucumber glue or runner discovery issues.

**Status:** Not yet validated during execution.

