# CityRescue Lite+ (Starter Repo)

This repository is your starting point for the CityRescue Lite+ coursework.

## Quick start (local)

Requirements:
- Java 17+
- Maven 3.8+

Run the **public tests**:
```bash
mvn -q -Dtest=Public*Test test
```

Run **all tests** that exist in this repository (still only public tests in the starter):
```bash
mvn test
```

## What you must implement
- `src/main/java/cityrescue/CityRescueImpl.java` (main implementation)
- Supporting model classes (you may add more classes/packages as needed)
- Keep behaviour deterministic (tie-break rules in the spec)

## Notes
- This repo contains **public tests only** (used for visible marks).
- Additional hidden tests will be used for the remaining marks.
