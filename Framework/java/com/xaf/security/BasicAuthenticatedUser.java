package com.xaf.security;

public class BasicAuthenticatedUser implements AuthenticatedUser
{
	private String userName;
	private String userId;
	private String userPassword;

    public BasicAuthenticatedUser(String name, String id, String password)
    {
		userName = name;
		userId = id;
		userPassword = password;
    }

	public String getUserName()
	{
		return userName;
	}

	public String getName() // implementation for java.security.Principal
	{
		return userId;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getUserPassword()
	{
		return userPassword;
	}
}