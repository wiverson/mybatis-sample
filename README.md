Simple example of how to use MyBatis with annotations.

This example uses an embedded H2 database by default.  This allows you to check the project out and immediately
run a clean build via Maven:

```bash
mvn clean verify
```

In addition, a MySQL Workbench file is included, along with a sample MyBatis generator file.  You can use these
to explore more complex mappings - for example, to use the generator with an existing schema.

## Requirements

- [Maven 3.9.0+](http://maven.apache.org/)
- Java 21+ (LTS)

## Dependencies

This project uses the following major dependencies:
- MyBatis 3.5.19
- JUnit 5 (Jupiter) 5.11.4
- Log4j 2.24.0
- H2 Database 2.3.232
