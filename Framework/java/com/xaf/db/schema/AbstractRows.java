/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:20:47 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import org.w3c.dom.Element;
import org.w3c.dom.DOMException;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public abstract class AbstractRows extends ArrayList implements Rows
{
    public void populateDataByIndexes(ResultSet resultSet) throws SQLException
    {
        clear();
    }

    public void populateDataByNames(ResultSet resultSet) throws SQLException
    {
        clear();
    }

    public void populateDataByNames(Element element) throws ParseException, DOMException
    {
        clear();
    }
}
