package com.xaf.form;

import java.util.EventListener;

public interface DialogContextListener extends EventListener
{
    /**
     *  Called before dialog state is calculated (fill in all data necessary).
     */
	public void populateDialogData(DialogContext dc);

    /**
     *  Fired after DialogContext is initialized but the current state (input,
	 *  validate, execute, etc) is <b>not</b> known.
     */
	public void makeDialogContextChanges(DialogContext dc, int stage);

    /**
     *  Fired when the dialog needs to check if data <i>should</i> be validated
     */
	public boolean dialogNeedsValidation(DialogContext dc);

    /**
     *  Fired when the dialog needs to check validity of data, <b>before</b> the dialog
	 *  has performed its own validation.
     */
	public boolean isDialogValid(DialogContext dc, boolean fieldsAlreadyValidated);

    /**
     *  Fired when the execute method is about to be called (after validation is performed).
	 *  If the execute <i>is</i> handled, then be sure to call dc.setExecuteStageHandled(true);
     */
	public String executeDialog(DialogContext dc);
}