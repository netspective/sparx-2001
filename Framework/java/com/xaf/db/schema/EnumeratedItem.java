/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 22, 2001
 * Time: 10:46:22 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

public interface EnumeratedItem
{
    public int getId();
    public Integer getIdAsInteger();
    public String getCaption();
    public String getAbbrev();
    public String getAbbrevOrCaption();
}
