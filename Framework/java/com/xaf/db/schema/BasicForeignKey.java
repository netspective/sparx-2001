/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:34:48 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

public class BasicForeignKey implements ForeignKey
{
    private Column source;
    private Column referenced;

    public BasicForeignKey(Column src, Column ref)
    {
        source = src;
        referenced = ref;

        source.getTable().registerForeignKey(this, true);
        referenced.getTable().registerForeignKey(this, false);
    }

    public short getType() { return ForeignKey.FKEYTYPE_LOOKUP; }

    public Column getSourceColumn() { return source; }
    public void setSourceColumn(Column value) { value = source; }

    public Column getReferencedColumn() { return referenced; }
    public void setReferencedColumn(Column value) { referenced = value; }
}
