/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:26:19 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

public interface ForeignKey
{
    public static short FKEYTYPE_NONE = 0;
    public static short FKEYTYPE_SELF = 1;
    public static short FKEYTYPE_PARENT = 2;
    public static short FKEYTYPE_LOOKUP = 3;

    public short getType();

    public Column getSourceColumn();
    public void setSourceColumn(Column value);

    public Column getReferencedColumn();
    public void setReferencedColumn(Column value);
}
