# CampusBite

Campus canteen pre-ordering web app — SECJ 3303 Internet Programming group project.

Students browse the menu, add items to a cart, pick a 15-minute pickup slot and place an order.
Kitchen staff work the orders on a board and keep daily stock up to date.
Admins manage the menu, pickup slots and users, and see sales reports.

## Team and modules

| Member  | Modules |
|---------|---------|
| Lawk    | M1 Authentication & Accounts · M2 User & Role Administration · shared set-up |
| Pasha   | M3 Menu Catalogue · M4 Menu & Stock Management |
| Hananeh | M5 Cart & Checkout · M6 Pickup Slots |
| Fatemeh | M7 Kitchen Order Board · M8 Order History & Reports |

Shared files (layout, navigation, CSS, `common` package, configuration, this README) belong to no module.

## Stack

Java 17 · Maven · Spring Boot 2.7.18 (Spring MVC, embedded Tomcat 9) · Thymeleaf ·
JdbcTemplate + MySQL 8 · H2 for tests · Bean Validation · BCrypt

## How to run (Phase 1 — no database needed yet)

1. Install JDK 17 and Maven 3.9+ (`java -version` and `mvn -v` must work).
2. In the project folder run:

   ```bash
   mvn spring-boot:run
   ```

3. Open http://localhost:8080

The app runs with the `memory` profile: all data lives in Java maps and resets on every restart.
MySQL arrives in Week 08.

## How to run the tests

```bash
mvn test
```

## Code layout

```
src/main/java/com/campusbite/
  CampusBiteApplication.java   shared
  common/                      shared (home page, exceptions, error handling, clock)
  auth/  users/                Lawk    (M1, M2)
  menu/  menuadmin/            Pasha   (M3, M4)
  checkout/  slots/            Hananeh (M5, M6)
  kitchen/  reports/           Fatemeh (M7, M8)
src/main/resources/templates/<module>/   each module's pages
src/main/resources/templates/layout/     shared layout + navigation
```
