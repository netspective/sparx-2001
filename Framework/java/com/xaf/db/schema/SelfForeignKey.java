/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:34:48 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

public class SelfForeignKey extends BasicForeignKey
{
    public SelfForeignKey(Column src, Column ref)
    {
        super(src, ref);
    }

    public short getType() { return ForeignKey.FKEYTYPE_SELF; }
}
