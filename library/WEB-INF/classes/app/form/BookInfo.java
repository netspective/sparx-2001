package app.form;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.dal.DataAccessLayer;
import app.dal.domain.row.BookInfoRow;
import app.dal.table.BookInfoTable;
import app.form.context.library.BookInfoContext;

import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xif.dal.ConnectionContext;


public class BookInfo extends Dialog
{
    /**
     * This is the class that you do your entire dialog validation with
     */
    public boolean isValid(DialogContext dc)
    {
        return super.isValid(dc);
    }


    public void populateValues(DialogContext dc, int i)
    {
        // make sure to call the parent method to ensure default behavior
        super.populateValues(dc, i);

        // you should almost always call dc.isInitialEntry() to ensure that you're not
        // populating data unless the user is seeing the data for the first time
        if (!dc.isInitialEntry())
            return;

        // now do the populating using DialogContext methods
        if (dc.editingData() || dc.deletingData())
        {
            BookInfoContext dcb = (BookInfoContext) dc;
            String bookId = dc.getRequest().getParameter("bookid");

            BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();

            try
            {
                ConnectionContext cc = dcb.getConnectionContext();

                // Grab the information from the BookInfo table into a new BookInfoRow ...
                BookInfoRow bkInfoRow = bkInfoTbl.getBookInfoById(cc, bookId);

                dcb.setBookId(bkInfoRow.getId());
                dcb.setBookAuthor(bkInfoRow.getAuthor());
                dcb.setBookName(bkInfoRow.getName());
                dcb.setBookType(bkInfoRow.getTypeInt());
                dcb.setBookISBN(bkInfoRow.getIsbn());
            }
            catch (NamingException ne)
            {
                ne.printStackTrace();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
        }
    }


    /**
     *  This is where you perform all your actions. Whatever you return as the function result will be shown
     * in the HTML
     */
    public void execute(Writer writer, DialogContext dc) throws IOException
    {
        // if you call super.execute(dc) then you would execute the <execute-tasks> in the XML; leave it out
        // to override
        // super.execute(writer, dc);

        HttpServletRequest request = (HttpServletRequest) dc.getRequest();
        String redirectURL = request.getContextPath() + "/index.jsp";
        String executeStatus;

        // What to do if the dialog is in add mode ...
        if (dc.addingData())
        {
            boolean status = processAddAction(writer, dc);
        }

        // What to do if the dialog is in edit mode ...
        if (dc.editingData())
        {
            boolean status = processEditAction(writer, dc);
        }

        // What to do if the dialog is in delete mode ...
        if (dc.deletingData())
        {
            boolean status = processDeleteAction(writer, dc);
        }

        ((HttpServletResponse) dc.getResponse()).sendRedirect(redirectURL);
    }

    /**
     * Process the new data
     */
    protected boolean processAddAction(Writer writer, DialogContext dc)
    {
        BookInfoContext dcb = (BookInfoContext) dc;
        BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();
        boolean status = false;

        try
        {
            ConnectionContext cc = dcb.getConnectionContext();

            // Create a new BookInfo record and insert it...
            BookInfoRow bkInfoRow = bkInfoTbl.createBookInfoRow();
            bkInfoRow.setCrStamp(null);
            bkInfoRow.setId(dcb.getBookId());
            bkInfoRow.setAuthor(dcb.getBookAuthor());
            bkInfoRow.setName(dcb.getBookName());
            bkInfoRow.setType(dcb.getBookTypeInt());
            bkInfoRow.setIsbn(dcb.getBookISBN());

            status = bkInfoTbl.insert(cc, bkInfoRow);
            cc.commitTransaction();
        }
        catch (NamingException ne)
        {
            ne.printStackTrace();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        return status;
    }

    /**
     * Process the update action
     */
    protected boolean processEditAction(Writer writer, DialogContext dc)
    {
        BookInfoContext dcb = (BookInfoContext) dc;
        BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();
        boolean status = false;
        String bookId = dc.getRequest().getParameter("bookid");

        try
        {
            ConnectionContext cc = dcb.getConnectionContext();

            BookInfoRow bkInfoRow = bkInfoTbl.getBookInfoById(cc, bookId);
            bkInfoRow.setId(dcb.getBookId());
            bkInfoRow.setAuthor(dcb.getBookAuthor());
            bkInfoRow.setName(dcb.getBookName());
            bkInfoRow.setType(dcb.getBookTypeInt());
            bkInfoRow.setIsbn(dcb.getBookISBN());

            status = bkInfoTbl.update(cc, bkInfoRow);
            cc.commitTransaction();
        }
        catch (NamingException ne)
        {
            ne.printStackTrace();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        return status;
    }

    /**
     * Process the delete action
     */
    protected boolean processDeleteAction(Writer writer, DialogContext dc)
    {
        BookInfoContext dcb = (BookInfoContext) dc;
        BookInfoTable bkInfoTbl = DataAccessLayer.instance.getBookInfoTable();
        boolean status = false;
        String bookId = dc.getRequest().getParameter("bookid");

        try
        {
            ConnectionContext cc = dcb.getConnectionContext();

            BookInfoRow bkInfoRow = bkInfoTbl.getBookInfoById(cc, bookId);

            status = bkInfoTbl.delete(cc, bkInfoRow);
            cc.commitTransaction();
        }
        catch (NamingException ne)
        {
            ne.printStackTrace();
        }
        catch (SQLException se)
        {
            se.printStackTrace();
        }

        return status;
    }
}
