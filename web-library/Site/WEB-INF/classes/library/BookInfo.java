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

			if (!bkInfoRow.beforeInsert(cc, dml)) {
				status = false;
				writer.write ("bkInfoRow.beforeInsert() returned false!<br>");
			}

	        boolean successful = bkInfoTbl.executeDml(cc, bkInfoRow, dml, null);
	        bkInfoRow.afterInsert(cc);

       		cc.returnConnection();
        	status = successful;


			writer.write (bkInfoRow.toString() + "<br>");
			writer.write (dml.toString() + "<br>");

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
