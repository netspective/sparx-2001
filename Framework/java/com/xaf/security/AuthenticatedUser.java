package com.xaf.security;

import java.security.*;
import com.xaf.value.*;

public interface AuthenticatedUser extends Principal
{
	public String getUserName();
	public String getUserId();
	public String getUserPassword();
}