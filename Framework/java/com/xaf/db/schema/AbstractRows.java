/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:20:47 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractRows extends ArrayList implements Rows
{
    public void populateData(ResultSet resultSet) throws SQLException
    {
        clear();
    }
}
