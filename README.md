# Spring Framework 7 Workshop

Building Resilient Applications with the new Spring Framework 7 resiliency features

This repository contains code to accompany the Java Brains workshop.

---

## Environment Setup

- The project uses **SDKMAN** for managing Java and Maven versions.
- Initialize your development environment using SDKMAN CLI and sdkman env file [`sdkmanrc`](.sdkmanrc)

```shell
sdk env install
sdk env
```

### Note: To install SDKMAN refer: [sdkman.io](https://sdkman.io/install)

## Project Requirements

- [ ] Java 25 +
- [ ] Maven 3.9 +
- [ ] SDKMAN for development environment setup

## Core Dependencies

- [ ] Spring Boot 4.0.0

---

To build and run the project, go to root directory: (e.g., `spring-7-workshop-1`, `spring-7-workshop-2`, etc.,) and run the below commands.

### Build project

- Using spring-boot maven plugin

```shell
sdk env
mvn clean package
```

### Run project

- Using spring-boot maven plugin

```shell
sdk env
mvn spring-boot:run
```

---
