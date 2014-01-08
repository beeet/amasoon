package org.amasoon.persistence;

import java.io.FileNotFoundException;
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

public abstract class AbstractIntTestSuite {

    protected static EntityManagerFactory emf;
    protected static EntityManager em;
    protected static Connection con;
    protected static IDatabaseConnection dbConnection;
    protected static FlatXmlDataSet dataSet;

    @BeforeClass
    public void setUpDb() throws SQLException, DatabaseUnitException, FileNotFoundException {
        emf = Persistence.createEntityManagerFactory("bookstore");
        em = emf.createEntityManager();

        con = DriverManager.getConnection("jdbc:derby://localhost:1527/bookstore", "app", "app");
        dbConnection = new DatabaseConnection(con);
        InputStream strm = AbstractIntTestSuite.class.getClassLoader().getResourceAsStream("test_data.xml");
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        dataSet = builder.build(strm);
    }

    @AfterClass
    public void tearDownDb() throws SQLException {
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

    @BeforeTest
    public void insertTestData() throws Exception {
        setUpDb();
        em.clear();
        emf.getCache().evictAll();

        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
    }

    @AfterTest
    public void deleteTestData() throws Exception {
        DatabaseOperation.DELETE_ALL.execute(dbConnection, dataSet);
        tearDownDb();
    }
}
