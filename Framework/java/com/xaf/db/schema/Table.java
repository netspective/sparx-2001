/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 1:26:57 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

public interface Table
{
    public void initializeDefn(Schema schema);
    public void finalizeDefn(Schema schema);

    public String getName();
    public String getNameForMapKey();
    public void setName(String value);
    public Row getRow();

    public String getDescription();
    public void setDescription(String value);

    public void registerForeignKey(ForeignKey fKey, boolean isSource);
}
