/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:39 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import org.w3c.dom.Element;
import org.w3c.dom.DOMException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public interface Rows
{
    /**
     * Given a ResultSet, loop through each row in the ResultSet and create an
     * appropriate Row object that represents a ResultSet row. The assumption is
     * that this method retrieves the columns in each ResultSet row in the order
     * the column was defined in the table (by a 1-based numeric index).
     */
    public void populateDataByIndexes(ResultSet resultSet) throws SQLException;

    /**
     * Given a ResultSet, loop through each row in the ResultSet and create an
     * appropriate Row object that represents a ResultSet row. The ordering of
     * columns is not important because each Row that will be created will search
     * for its columns in the ResultSet by name.
     */
    public void populateDataByNames(ResultSet resultSet) throws SQLException;

    /**
     * Given an XML element that contains row/column data, extract each row and
     * create an appropriate Row instance. The main element must follow this DTD
     * (the element parameter to the method call is assumed to be the "data"
     * element):
     * <pre>
     *      <!ELEMENT data (row)*>
     *          <!ELEMENT row (col)*>
     *              <!ELEMENT col %DATATYPE.TEXT;>
     *                  <!ATTLIST col name CDATA #REQUIRED>
     * </pre>
     */
    public void populateDataByNames(Element element) throws ParseException, DOMException;
}
