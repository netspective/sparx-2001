package app.value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.naming.NamingException;

import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSource;
import com.netspective.sparx.util.value.ListSource;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import java.sql.SQLException;

public class GiftImageFileValue extends ListSource
{
    public GiftImageFileValue()
    {
    }

    public void initializeSource(String srcParams)
    {
        System.out.println("srcParams: " + srcParams);
    }

    public String getValue(ValueContext vc)
    {
        return "Hola";

	}

    public SelectChoicesList getSelectChoices(ValueContext vc)
	{
        String dataCmd = vc.getRequest().getParameter("data_cmd");
        String id = vc.getRequest().getParameter("id");

        SelectChoicesList choices = new SelectChoicesList();
        choices.add(new SelectChoice("No Picture", "no_picture"));


        if (id != null && id.length() > 0 && "edit".equals(dataCmd)) {
            String category = null;
            String name = null;

            StatementManager sm = vc.getStatementManager();
            DatabaseContext dbContext = DatabaseContextFactory.getContext(vc.getRequest(), vc.getServletContext());
            Object[][] result = null;
            Object[] parameters = {id};
            //System.out.println("id: " + id);
            try {
                result = sm.executeStmtGetValuesMatrix(dbContext, vc, null , "Gift.getGiftPicture", parameters);
            } catch (StatementNotFoundException e) { return choices;
            } catch (NamingException e) { return choices;
            } catch (SQLException e) { return choices;
            }
            System.out.println("result.size: " + result.length);
            if (result != null && result.length >=1){
                if (result[0] != null && result[0].length >=2){
                    name = (String) result[0][0];
                    category = (String) result[0][1];
                    //System.out.println("result[0][0]: " + result[0][0]);
                    //System.out.println("result[0][1]: " + result[0][1]);
                }
            }
            if ( category != null && category.length() > 0 && name != null && name.length() > 0){
                //System.out.println("category: " + category);
                //System.out.println("name: " + name);
                choices.add(new SelectChoice("Picture", category + "/" + name));
            }
        }

        return choices;
	}
}
