/*
 * Description: library.BookInfo
 * @author ThuA
 * @created Dec 27, 2001 3:22:08 PM
 * @version
 */
package library;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.task.TaskExecuteException;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import com.netspective.sparx.xif.dal.ConnectionContext;
import dal.table.BookInfoTable;
import dal.domain.row.BookInfoRow;
import dal.DataAccessLayer;

import dialog.context.library.BookInfoContext;
import com.netspective.sparx.xaf.sql.DmlStatement;



import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.Date;
import java.net.URL;
import java.net.URLEncoder;
import java.io.Writer;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.List;


public class BookInfo   extends Dialog
{

    /**
     * This is the class that you do your entire dialog validation with
     */
    public boolean isValid(DialogContext dc)
    {
        return super.isValid(dc);
    }

    /**
     *  This is where you perform all your actions. Whatever you return as the function result will be shown
     * in the HTML
     */
    public void execute(Writer writer, DialogContext dc)
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(dc);

        HttpServletRequest request = (HttpServletRequest)dc.getRequest();

		// What to do if the dialog is in add mode ...
        if (dc.addingData())
        {
			boolean status = processAddAction(writer, dc);
			String theStatus = status ? "Success!" : "Failure!";

			try {
				writer.write(theStatus + "<br>");
			} catch (IOException e) {
				e.printStackTrace();
			}

        }

		// What to do if the dialog is in edit mode ...
        if (dc.editingData())
        {
			try {
				writer.write("Data edited<br>");
			} catch (IOException e) {
				e.printStackTrace();
			}
            // processEditAction(dc);
        }

		// What to do if the dialog is in delete mode ...
        if (dc.deletingData())
        {
			try {
				writer.write("Data deleted<br>");
			} catch (IOException e) {
				e.printStackTrace();
			}
            // processDeleteAction(dc);

        }
    }

    /**
     * Process the delete action
     */
    protected void processDeleteAction(DialogContext dc)
    {
    }

    /**
     * Process the update action
     */
	protected boolean processEditAction(DialogContext dc)
	{
		BookInfoContext dcb = (BookInfoContext) dc;
		BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();
		boolean status = false;

		try {
			ConnectionContext cc = dcb.getConnectionContext();

			// Create a new BookInfo record and insert it...
			BookInfoRow bkInfoRow = bkInfoTbl.createBookInfoRow();
			bkInfoRow.setId(dcb.getBookId());
			bkInfoRow.setAuthor(dcb.getBookName());
			bkInfoRow.setName(dcb.getBookAuthor());
			bkInfoRow.setType(dcb.getBookTypeInt());
			bkInfoRow.setIsbn(dcb.getBookISBN());

			status = bkInfoTbl.insert(cc, bkInfoRow);
		} catch (NamingException ne) {
			ne.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return status;
	}

    /**
     * Process the new data
     */
    protected boolean processAddAction(Writer writer, DialogContext dc)
    {
		BookInfoContext dcb = (BookInfoContext) dc;
		BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();
		boolean status = false;

		try {
			ConnectionContext cc = dcb.getConnectionContext();

			// Create a new BookInfo record and insert it...
			BookInfoRow bkInfoRow = bkInfoTbl.createBookInfoRow();
			bkInfoRow.setCrStamp(new Date());
			bkInfoRow.setId(dcb.getBookId());
			bkInfoRow.setAuthor(dcb.getBookName());
			bkInfoRow.setName(dcb.getBookAuthor());
			bkInfoRow.setType(dcb.getBookTypeInt());
			bkInfoRow.setIsbn(dcb.getBookISBN());

			DmlStatement dml = bkInfoRow.createInsertDml(bkInfoTbl);
			bkInfoTbl.validateDmlValues(dml);

			writer.write (bkInfoRow.toString() + "<br>");
			writer.write (dml.toString() + "<br>");

			if (!bkInfoRow.beforeInsert(cc, dml)) {
				status = false;
				writer.write ("bkInfoRow.beforeInsert() returned false!<br>");
			} else {
				writer.write ("bkInfoRow.beforeInsert() returned true!<br>");
			}












			boolean result = false;
			PreparedStatement stmt = null;
			try
			{
				stmt = cc.getConnection().prepareStatement(dml.getSql());

				int columnNum = 1;
				boolean[] bindValues = dml.getBindValues();
				List columnValues = dml.getColumnValues();
				if(bindValues != null)
				{
									// Need to use columnValues.size() since the dml may have removed
									// columns if autoinc columns are not included in the SQL
					for(int c = 0; c < columnValues.size(); c++)
					{
						if(bindValues[c])
						{
							stmt.setObject(columnNum, columnValues.get(c));
							columnNum++;
						}
					}
				}

/*
				Object[] additionalBindParams = null;

				if(additionalBindParams != null)
				{
					for(int i = 0; i < additionalBindParams.length; i++)
					{
						stmt.setObject(columnNum, additionalBindParams[i]);
						columnNum++;
					}
				}
*/
				int numRowsUpdated = stmt.executeUpdate();
				writer.write ("stmt.executeUpdate returned" + numRowsUpdated + "<br>");
				if (numRowsUpdated > 0) result = true;
				writer.write ("stmt.execute() returned " + (result ? "true!" : "false!") + "<br>");
			}
			catch(SQLException e)
			{
				StringBuffer bindParams = new StringBuffer();
				int columnNum = 1;
				boolean[] bindValues = dml.getBindValues();
				List columnValues = dml.getColumnValues();
				if(bindValues != null)
				{
					for(int c = 0; c < columnValues.size(); c++)
					{
						if(bindValues[c])
						{
							Object value = columnValues.get(c);
							if(columnNum > 1)
								bindParams.append(", ");
							bindParams.append(columnNum);
							bindParams.append(": ");
							bindParams.append(value == null ? "NULL" : (value.toString() + " {" + value.getClass().getName() + "}"));
							columnNum++;
						}
					}
				}
/*
				if(additionalBindParams != null)
				{
					for(int i = 0; i < additionalBindParams.length; i++)
					{
						Object value = additionalBindParams[i];
						if(columnNum > 1)
							bindParams.append(", ");
						bindParams.append(columnNum);
						bindParams.append(": ");
						bindParams.append(value == null ? "NULL" : (value.toString() + " {" + value.getClass().getName() + "}"));
						columnNum++;
					}
				}
*/
				throw new SQLException(e.toString() + " [" + dml.getSql() + "\n(bind " + (bindParams.length() > 0 ? bindParams.toString() : "none") + ")\n" + bkInfoRow.toString() + "]");
			}
			finally
			{
				if(stmt != null) stmt.close();
			}
















			boolean successful = result;
//	        boolean successful = bkInfoTbl.executeDml(cc, bkInfoRow, dml, null);
	        writer.write ("executeDml " + (successful ? "succeeded" : "failed") + "!<br>");

	        bkInfoRow.afterInsert(cc);

       		cc.returnConnection();
        	status = successful;

//			status = bkInfoTbl.insert(cc, bkInfoRow);
			cc.commitTransaction();
		} catch (NamingException ne) {
			ne.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return status;
    }
}
