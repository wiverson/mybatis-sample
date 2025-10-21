Simple example of how to use MyBatis with annotations.

This example uses PostgreSQL with Testcontainers for automated testing. Tests automatically spin up a PostgreSQL
container, run the tests, and clean up - no manual database setup required!

## Quick Start

Simply run the tests via Maven. Testcontainers will automatically handle the PostgreSQL setup:

```bash
mvn clean verify
```

**Note:** Docker must be running for tests to work, as Testcontainers manages PostgreSQL containers automatically.

## Manual Local Development (Optional)

If you want to run PostgreSQL manually for development, use the included docker-compose.yml:

```bash
# Start PostgreSQL
docker-compose up -d

# Stop PostgreSQL
docker-compose down

# Stop and remove data
docker-compose down -v
```

The PostgreSQL instance will be available at `localhost:5432` with:
- Database: `testdb`
- Username: `test`
- Password: `test`

## Requirements

- [Maven 3.9.0+](http://maven.apache.org/)
- Java 21+ (LTS)
- Docker (for Testcontainers and docker-compose)

## Dependencies

This project uses the following major dependencies:
- MyBatis 3.5.19
- JUnit 5 (Jupiter) 5.11.4
- Log4j 2.24.0
- PostgreSQL JDBC Driver 42.7.7
- Testcontainers 1.20.1

## Additional Resources

A MySQL Workbench file is included, along with a sample MyBatis generator file. You can use these
to explore more complex mappings - for example, to use the generator with an existing schema.
