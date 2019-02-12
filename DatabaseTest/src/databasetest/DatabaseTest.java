package databasetest;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.sql.*;

public class DatabaseTest {

    public static void main(String[] args) {
        
    }
        public static JSONArray getJSONData() throws ClassNotFoundException, InstantiationException{
        
        JSONArray results = null;
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;

        String query, value;
        String[] headers;

        JSONArray records = new JSONArray();

        boolean hasResults;
        int finalCount, columnCount;

        try {

            /* Identify the Server */
            String server = ("jdbc:mysql://localhost/db_test");
            String username = "root";
            String password = "CS310";
            System.out.println("Connecting to " + server + "...");

            /* Load the MySQL JDBC Driver */
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            /* Open Connection */
            conn = DriverManager.getConnection(server, username, password);

            /* Test Connection */
            if (conn.isValid(0)) {

                /* Prepare Select Query */
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);

                /* Execute Select Query */
                System.out.println("Submitting Query ...");

                hasResults = pstSelect.execute();

                /* Get Results */
                System.out.println("Getting Results ...");

                while (hasResults || pstSelect.getUpdateCount() != -1) {

                    if (hasResults) {

                        /* Get ResultSet Metadata */
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        headers = new String[columnCount - 1];

                        /* Get Column Names; Print as Table Header */
                        for (int i = 1; i <= columnCount; i++) {

                            headers[i] = metadata.getColumnLabel(i + 2);

                        }

                        LinkedHashMap data = new LinkedHashMap();

                        /* Get Data; Print as Table Rows */
                        while (resultset.next()) {

                            /* Begin Next ResultSet Row */
                            data = new LinkedHashMap();

                            /* Loop Through ResultSet Columns; Print Values */
                            for (int i = 0; i <= headers.length; i++) {

                                value = resultset.getString(i + 2);

                                if (resultset.wasNull()) {
                                    data.put(headers[i], "");
                                } else {
                                    data.put(headers[i], value);
                                }

                            }
                            records.add(data);

                        }

                    } else {

                        finalCount = pstSelect.getUpdateCount();

                        if (finalCount == -1) {
                            break;
                        }

                    }

                    /* Check for More Data */
                    hasResults = pstSelect.getMoreResults();

                }

                results = records;

            }

            System.out.println();

            /* Close Database Connection */
            conn.close();

        } catch (Exception e) {
            System.err.println(e.toString());
        } /* Close Other Database Objects */ finally {

            if (resultset != null) {
                try {
                    resultset.close();
                    resultset = null;
                } catch (Exception e) {
                }
            }

            if (pstSelect != null) {
                try {
                    pstSelect.close();
                    pstSelect = null;
                } catch (Exception e) {
                }
            }

            if (pstUpdate != null) {
                try {
                    pstUpdate.close();
                    pstUpdate = null;
                } catch (Exception e) {
                }
            }

        }
        }

