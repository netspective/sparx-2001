/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:19:39 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.form.DialogContext;
import com.xaf.sql.DmlStatement;
import com.xaf.value.ValueContext;
import com.xaf.db.ConnectionContext;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public interface Row
{
    public Column[] getColumns();

    public Object[] getData();
    public List getDataForDmlStatement();
    public Object getActivePrimaryKeyValue();

    public void populateData(ResultSet resultSet) throws SQLException;
    public void populateData(DialogContext dc);

    public void setData(DialogContext dc);

    public DmlStatement createInsertDml(Table table);
    public DmlStatement createUpdateDml(Table table, String whereCond);
    public DmlStatement createDeleteDml(Table table, String whereCond);

    public boolean beforeInsert(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;
    public boolean beforeUpdate(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;
    public boolean beforeDelete(ConnectionContext cc, DmlStatement dml) throws NamingException, SQLException;

    public void afterInsert(ConnectionContext cc) throws NamingException, SQLException;
    public void afterUpdate(ConnectionContext cc) throws NamingException, SQLException;
    public void afterDelete(ConnectionContext cc) throws NamingException, SQLException;
}
