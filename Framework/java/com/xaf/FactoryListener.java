package com.xaf;

import java.util.EventListener;

public interface FactoryListener extends EventListener
{
    /**
     *  Called whenever the factory contents have changed. The object that
	 *  caused the change is provided along with the reason for the change.
     */
	public void factoryContentsChanged(FactoryEvent event);
}