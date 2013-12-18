package org.amasoon.persistence;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractIntTestSuite {

    protected static EntityManagerFactory emf;
    protected static EntityManager em;
    protected static Connection con;
    protected static IDatabaseConnection dbConnection;
    protected static FlatXmlDataSet dataSet;

    @BeforeClass
    public  void setUpDb() throws SQLException, DatabaseUnitException {
        emf = Persistence.createEntityManagerFactory("amasoon");
        em = emf.createEntityManager();

        con = DriverManager.getConnection("jdbc:derby://localhost:1527/amasoon", "app", "app");
        dbConnection = new DatabaseConnection(con);

        InputStream strm = Thread.currentThread().getContextClassLoader().getResourceAsStream("amasoon_data.xml");
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        dataSet = builder.build(strm);
    }

    @AfterClass
    public  void tearDownDb() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Before
    public void insertTestData() throws Exception {
        em.clear();
        emf.getCache().evictAll();

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }

    @After
    public void deleteTestData() throws Exception {
        DatabaseOperation.DELETE_ALL.execute(dbConnection, dataSet);
    }
}
