package app.value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;
import javax.naming.NamingException;

import com.netspective.sparx.util.value.*;
import com.netspective.sparx.xaf.form.field.SelectChoicesList;
import com.netspective.sparx.xaf.form.field.SelectChoice;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.form.Dialog;
import com.netspective.sparx.xaf.sql.StatementManager;
import com.netspective.sparx.xaf.sql.StatementNotFoundException;
import com.netspective.sparx.xif.db.DatabaseContext;
import com.netspective.sparx.xif.db.DatabaseContextFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

public class FlowControlValue extends ValueSource
{
    private String forwardUrl = "";
    private HashMap retainedParams = new HashMap();
    private List retainedParamsNames = new ArrayList();
    public FlowControlValue()
    {
    }

   public String getValue(ValueContext context) {

      DialogContext dc = null;
      if (context instanceof DialogContext)
      {
         dc = (DialogContext) context;
      }
      else
      {
          ServletRequest request = context.getRequest();
          dc = (DialogContext) request.getAttribute(DialogContext.DIALOG_CONTEXT_ATTR_NAME);
      }

      String url = null;
      if ( dc.getDataCommand() ==  DialogContext.DATA_CMD_EDIT ) {
         String params = "";
         for (int i = 0; i < retainedParamsNames.size(); i++) {
           String paramName = (String) retainedParamsNames.get(i);
            String paramValue = "";
           if (paramName != null && paramName.length() > 0) {
                paramValue = context.getRequest().getParameter(paramName);
                if (paramValue == null)
                    paramValue = "";
           }
           params = params + Dialog.PARAMNAME_CONTROLPREFIX + paramName + "=" + paramValue + "&";
         }
         params =  params + "_d_exec=yes&_dc.output.destination=1";
         url = "/" + forwardUrl + "?" + params;

         ServletContextUriValue uriValue = new ServletContextUriValue();
         uriValue.initializeSource(url);
         return uriValue.getValue(context);

      }
      else {
         return dc.getOriginalReferer();
      }


   }

   public void initializeSource(String srcParams) {
      super.initializeSource(srcParams);

      String retainParams = null;      

      int delimPos = srcParams.indexOf('?');
      if(delimPos >= 0)
      {
         forwardUrl = srcParams.substring(0, delimPos);
         retainParams = srcParams.substring(delimPos + 1);

         if(retainParams.length() > 0)
         {
             retainedParamsNames = new ArrayList();
             StringTokenizer st = new StringTokenizer(retainParams, ",");
             while(st.hasMoreTokens())
                 retainedParamsNames.add(st.nextToken());       
         }
      }     
   }
}
