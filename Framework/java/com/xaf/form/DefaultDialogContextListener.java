package com.xaf.form;

/**
 * Provide default so that new listeners can extend this one and not have to
 * worry about providing all of the method implementations.
 */

public class DefaultDialogContextListener implements DialogContextListener
{
    public DefaultDialogContextListener()
    {
    }

    /**
     *  Called before dialog state is calculated (fill in all data necessary).
     */
	public void populateDialogData(DialogContext dc)
	{
	}

    /**
     *  Fired after DialogContext is initialized but the current state (input,
	 *  validate, execute, etc) is <b>not</b> known.
     */
	public void makeDialogContextChanges(DialogContext dc, int stage)
	{
	}

    /**
     *  Fired when the dialog needs to check validity of data, <b>before</b> the dialog
	 *  has performed its own validation.
     */
	public boolean isDialogValid(DialogContext dc, boolean fieldsAlreadyValidated)
	{
		return true;
	}

    /**
     *  Fired when the execute method is about to be called (after validation is performed).
     */
	public String executeDialog(DialogContext dc)
	{
		return null;
	}
}