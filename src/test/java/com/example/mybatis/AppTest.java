package com.example.mybatis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AppTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static public void testApp() {
        // Create a custom configuration with Testcontainers PostgreSQL
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(postgres.getDriverClassName());
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        Environment environment = new Environment("development", new JdbcTransactionFactory(), dataSource);
        Configuration config = new Configuration(environment);
        config.setUseGeneratedKeys(true);
        config.addMapper(TransactionTokenMapper.class);

        App.factory = new SqlSessionFactoryBuilder().build(config);

        assertNotNull(App.factory);

        SqlSession s = App.factory.openSession();

        TransactionTokenMapper mapper = s.getMapper(TransactionTokenMapper.class);
        mapper.schema();

        s.commit();
        s.close();
    }

    private TransactionTokenMapper mapper = null;
    private SqlSession session = null;

    @BeforeEach
    public void setupSession() {
        session = App.factory.openSession();  // This obtains a database connection!
        mapper = session.getMapper(TransactionTokenMapper.class);
    }

    @AfterEach
    public void closeSession() {
        session.commit();  // This commits the data to the database. Required even if auto-commit=true
        session.close();   // This releases the connection
    }

    private TransactionToken tokenFactory(String tokenPrefix, String transactionPrefix)
    {
        TransactionToken t = new TransactionToken();
        t.setToken(tokenPrefix + System.currentTimeMillis());
        t.setTransaction(transactionPrefix + System.currentTimeMillis());
        return t;
    }

    @Test
    public void testInsert() {
        TransactionToken t = tokenFactory("alpha", "beta");
        mapper.insert(t);
        assertTrue(t.getId() > -1);

        long count = mapper.count();

        TransactionToken t2 = tokenFactory("cappa", "delta");
        mapper.insert(t2);
        assertTrue(t2.getId() > -1);

        assertEquals(count + 1, mapper.count());
    }

    @Test
    public void testUpdate() {
        TransactionToken t = tokenFactory("faraday", "gamma");
        mapper.insert(t);

        TransactionToken t2 = mapper.getById(t.getId());
        assertEquals(t.getToken(), t2.getToken());
        assertEquals(t.getTransaction(), t2.getTransaction());

        t2.setToken("bingo" + System.currentTimeMillis());
        t2.setTransaction("funky" + System.currentTimeMillis());
        mapper.update(t2);

        TransactionToken t3 = mapper.getById(t.getId());
        assertEquals(t2.getToken(), t3.getToken());
        assertEquals(t2.getTransaction(), t3.getTransaction());
    }

    @Test
    public void testDeleteById() {
        long count = mapper.count();

        TransactionToken t = tokenFactory("indigo", "jakarta");
        mapper.insert(t);
        assertEquals(count + 1, mapper.count());

        mapper.deleteById(t);
        assertEquals(count, mapper.count());
    }

    @Test
    public void testDeleteByTransaction() {
        long count = mapper.count();

        TransactionToken t2 = tokenFactory("kava", "lambda");
        mapper.insert(t2);
        assertEquals(count + 1, mapper.count());

        mapper.deleteByTransaction(t2);
        assertEquals(count, mapper.count());
    }

    @Test
    public void testFindByTransaction() {
        TransactionToken t = tokenFactory("manual", "nova");
        mapper.insert(t);
        assertTrue(t.getId() >= 0);

        TransactionToken t2 = mapper.selectByTransaction(t.getTransaction());
        assertEquals(t.getToken(), t2.getToken());
        assertEquals(t.getTransaction(), t2.getTransaction());
    }

    @Test
    public void testRollback() {
        long count = mapper.count();

        TransactionToken t = tokenFactory("omega", "passport");
        mapper.insert(t);
        assertEquals(count + 1, mapper.count());

        session.rollback();
        assertEquals(count, mapper.count());

        TransactionToken t3 = tokenFactory("quark", "star");
        mapper.insert(t3);
        assertEquals(count + 1, mapper.count());
    }
}
