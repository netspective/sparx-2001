/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:39 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Rows
{
    public void populateData(ResultSet resultSet) throws SQLException;
}
