# paste-spring

A simple text-sharing API implemented with Spring Boot 4

## Development

Using DevContainer is recommended for development.

### Testing

Tests are classified by size based on [Google Testing Blog: Test Sizes](https://testing.googleblog.com/2010/12/test-sizes.html) https://testing.googleblog.com/2010/12/test-sizes.html.

Run small tests:

```bash
./gradlew test
```

Run medium tests:

```bash
./gradlew integrationTest
```

Run API tests with Python client:

See [api-tests/README.md](api-tests/README.md).

### Launch Server

```bash
./gradlew bootRun
```

### Observability

Jaeger UI for distributed tracing: http://localhost:16686

## Environment

- Java 21
- Gradle 9.2+
- Spring Boot 4
- OpenAPI Generator
- SQLite
- OpenTelemetry
- Jaeger

## License

[MIT License](LICENSE)

## Author

[toms74209200](https://github.com/toms74209200)
