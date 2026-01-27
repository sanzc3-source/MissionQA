
**Impact:**
- WireMock failed to initialize
- API tests failed before execution

**Root Cause:**  
Manual pinning of Apache HttpClient5 versions conflicted with
WireMock’s expected transitive dependencies.

**Fix:**
- Removed explicit HttpClient5 and HttpCore5 version pinning
- Allowed WireMock to resolve compatible HTTP dependencies
- Stabilized UI independently via Selenium Manager

**Verification:**
- WireMock starts successfully
- `@api` and `@ui` test suites run cleanly
- `mvn clean test` is fully GREEN

---

## Final State Summary

- UI and API tests pass consistently
- External dependencies eliminated
- Dependency graph stabilized
- Framework is CI-ready and deterministic