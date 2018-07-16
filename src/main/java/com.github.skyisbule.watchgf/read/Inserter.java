package com.github.skyisbule.watchgf.read;

import com.github.skyisbule.watchgf.Config;

import java.sql.*;

public class Inserter {

    public void doInsert(){
        Connection connection = null;
        try {
            String sql = "insert into meta values('sky','{0,0}')";
            connection = DriverManager.getConnection("jdbc:sqlite:"+ Config.HISTORY_PATH);
            PreparedStatement ptmt = connection.prepareStatement(sql);
            ptmt.execute();
        }
        catch(SQLException e) { System.err.println(e.getMessage()); } finally { try { if(connection != null) connection.close(); } catch(SQLException e) { System.err.println(e); } }
    }

}
