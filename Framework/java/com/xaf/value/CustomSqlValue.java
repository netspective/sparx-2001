package com.xaf.value;

/**
 * Title:        The eXtensible Application Framework
 * Description:  This is a placeholder class used specifically in DmlTask.java
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author       Shahid N. Shah
 * @version 1.0
 */

public class CustomSqlValue extends StaticValue
{
    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "This SVS is used in SQL DML tasks and custom tags when, instead of a java value or expression, you want " +
            "the DML processing to take an actual SQL expression that should be evaluated in the database. For "+
            "example, if <code><u>sysdate</u></code> is the sql-expr, then this SVS would return " +
            "<code><u>sysdate</u></code> without evaluating in Java or treating it as a Java string. If this value "+
            "source is used in any context other than a DML (SQL insert or update or delete) then it returns just the "+
            "sql-expr itself.",
            "sql-expr"
        );
    }
}