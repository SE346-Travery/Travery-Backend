# Travery Project - Development Guidelines

## Architecture & Design
- **Layered REST API:** Spring Boot 4 + Java 25.
- **DTOs:** Modular and flat. Extract nested static classes into standalone files in `dtos` package.
- **Exceptions:** Use `BaseAppException` with `WebErrorCode` for standard responses.
- **Validation:** Strict hierarchy and ownership validation (SOLID principles).
- **Mappers:** Descriptive naming for role-specific mappers (MapStruct).

## API Standards
- **Prefix:** All controllers must use `/api/v1/`.
- **Refresh:** Endpoints modifying state (`PATCH`, `POST`, `PUT`) must return the fully updated entity.
- **Security:** Use `@PreAuthorize` and enforce ownership at the service layer.

## Conventions
- **Imports:** Use explicit imports over fully qualified names.
- **Formatting:** Run `./mvnw spotless:apply` before committing.
- **Testing:** Comprehensive unit (Service) and MockMvc (Controller) tests required. Pass `./mvnw test-compile` and `./mvnw test`.

## Sources of Truth
- **Wiki:** Refer to `/home/tuan/Documents/UIT/MyWiki/travery-wiki/`.
