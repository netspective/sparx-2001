/*
 * Created by IntelliJ IDEA.
 * User: snshah
 * Date: Nov 11, 2001
 * Time: 12:20:35 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.xaf.db.schema;

import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueContext;

import java.util.Map;
import java.util.HashMap;

public abstract class AbstractColumn implements Column
{
    public static long COLUMNFLAG_ISREQUIRED = 1;
    public static long COLUMNFLAG_ISUNIQUE = COLUMNFLAG_ISREQUIRED * 2;
    public static long COLUMNFLAG_ISNATURALPRIMARYKEY = COLUMNFLAG_ISUNIQUE * 2;
    public static long COLUMNFLAG_ISSEQUENCEDPRIMARYKEY = COLUMNFLAG_ISNATURALPRIMARYKEY * 2;
    public static long COLUMNFLAG_HASDEFAULTVALUE = COLUMNFLAG_ISSEQUENCEDPRIMARYKEY * 2;
    public static long COLUMNFLAG_ISDEFAULTVALUESQLEXPR = COLUMNFLAG_HASDEFAULTVALUE * 2;
    public static long COLUMNFLAG_ISDEFAULTVALUESINGLEVALUESRC = COLUMNFLAG_ISDEFAULTVALUESQLEXPR * 2;
    public static long COLUMNFLAG_ISINDEXED = COLUMNFLAG_ISDEFAULTVALUESINGLEVALUESRC * 2;
    public static long COLUMNFLAG_SQLDEFNHASSIZE = COLUMNFLAG_ISINDEXED * 2;
    public static long COLUMNFLAG_CUSTOMSTART = COLUMNFLAG_SQLDEFNHASSIZE * 2;

    public static String SIZE_REPLACEMENT_FMT = "%size%";

    private Table table;
    private String name;
    private int size = -1;
    private int indexInRow;
    private Map sqlDefn = new HashMap();
    private String description;
    private long flags;
    private ForeignKey foreignKey;
    private String foreignKeyRef;
    private short foreignKeyRefType;
    private ColumnData defaultValue;
    private String dataClassName;

    public AbstractColumn(Table table, String name)
    {
        this.table = table;
        this.name = name;
        table.getRow().addColumn(this);
    }

    public Table getTable() { return table; }
    public void setTable(Table value) { table = value; }

    public String getName() { return name; }
    public String getNameForMapKey() { return name.toLowerCase(); }
    public void setName(String value) { name = value; }

    public String getSqlDefn(String dbms)
    {
        String defn = (String) sqlDefn.get(dbms == null ? "ansi" : dbms);
        if(defn != null)
            return flagIsSet(COLUMNFLAG_SQLDEFNHASSIZE) ? replaceValueInStr(defn, SIZE_REPLACEMENT_FMT, Integer.toString(size)) : defn;
        else
            return null;
    }

    public void setSqlDefn(String dbms, String value)
    {
        if(value.indexOf(SIZE_REPLACEMENT_FMT) >= 0)
            setFlag(COLUMNFLAG_SQLDEFNHASSIZE);

        sqlDefn.put(dbms == null ? "ansi" : dbms, value);
    }

    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }

    public int getSize() { return size; }
    public void setSize(int value) { size = value; }

    public String getDataClassName() { return dataClassName; }
    public void setDataClassName(String value) { dataClassName = value; }

    public ForeignKey getForeignKey() { return foreignKey; }
    public void setForeignKey(ForeignKey value) { foreignKey = value; }
    public void setForeignKeyRef(short type, String value) { foreignKeyRefType = type; foreignKeyRef = value; }

    public int getIndexInRow() { return indexInRow; }
    public void setIndexInRow(int value) { indexInRow = value; }

    public boolean hasValue(RowData rowData) { return getObjectValue(rowData) != null; }
    public Object getObjectValue(RowData rowData) { ColumnData data = rowData.getData(this); return data != null ? data.getValue() : null; }
    public void setValueObject(RowData rowData, Object value) { rowData.setData(this, new BasicColumnData(value)); }
    public void setValueSqlExpr(RowData rowData, String expr) { rowData.setData(this, new BasicColumnData(expr)); }

    public boolean hasValue(DataContext dc) { return getObjectValue(dc) != null; }
    public Object getObjectValue(DataContext dc) { ColumnData data = dc.getRowData().getData(this); return data != null ? data.getValue(dc) : null; }

    public ColumnData getDefaultValue() { return defaultValue; }
    public void setDefaultValue(ColumnData value) { defaultValue = value; }

    public boolean isIndexed() { return flagIsSet(COLUMNFLAG_ISINDEXED); }
    public boolean isNaturalPrimaryKey() { return flagIsSet(COLUMNFLAG_ISNATURALPRIMARYKEY); }
    public boolean isPrimaryKey() { return isNaturalPrimaryKey() || isSequencedPrimaryKey(); }
    public boolean isRequired() { return flagIsSet(COLUMNFLAG_ISREQUIRED); }
    public boolean isSequencedPrimaryKey() { return flagIsSet(COLUMNFLAG_ISSEQUENCEDPRIMARYKEY); }
    public boolean isUnique() { return flagIsSet(COLUMNFLAG_ISUNIQUE); }

    public void setIsIndexed(boolean flag) { setOrClearFlag(COLUMNFLAG_ISINDEXED, flag); }
    public void setIsNaturalPrimaryKey(boolean flag) { setOrClearFlag(COLUMNFLAG_ISNATURALPRIMARYKEY, flag); }
    public void setIsRequired(boolean flag) { setOrClearFlag(COLUMNFLAG_ISREQUIRED, flag); }
    public void setIsSequencedPrimaryKey(boolean flag) { setOrClearFlag(COLUMNFLAG_ISNATURALPRIMARYKEY, flag); }
    public void setIsUnique(boolean flag) { setOrClearFlag(COLUMNFLAG_ISUNIQUE, flag); }

    protected long getFlags() { return flags; }
	protected boolean flagIsSet(long flag) { return (flags & flag) == 0 ? false : true; }
	protected void setFlag(long flag) { flags |= flag; }
    protected void setOrClearFlag(long flag, boolean set) { if(set) flags |= flag; else flags &= ~flag; }
	protected void clearFlag(long flag) { flags &= ~flag; }

    public void finalizeDefn(Schema schema, Table table)
    {
        if(foreignKeyRefType != ForeignKey.FKEYTYPE_NONE)
            setForeignKey(schema.getForeignKey(foreignKeyRefType, foreignKeyRef));
    }

    static public String replaceValueInStr(String srcStr, String findStr, String replStr)
    {
		if(srcStr == null || findStr == null || replStr == null)
			return null;

        int findLoc = srcStr.indexOf(findStr);
        if(findLoc >= 0)
        {
            StringBuffer sb = new StringBuffer(srcStr);
            sb.replace(findLoc, findLoc + findStr.length(), replStr);
            return sb.toString();
        }
        else
            return srcStr;
    }

}
