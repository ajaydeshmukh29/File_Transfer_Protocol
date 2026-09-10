package com.fileflow.fileflowbackend.dto;

/**
 * Response payload returned after a successful login: the JWT the
 * frontend must send back as "Authorization: Bearer <token>" on every
 * subsequent request, plus a plain message for display.
 */
public class AuthResponse
{
    private String message;
    private String token;
    private String name;
    private String email;

    public AuthResponse(String message, String token, String name, String email)
    {
        this.message = message;
        this.token = token;
        this.name = name;
        this.email = email;
    }

    public String getMessage()
    {
        return message;
    }

    public String getToken()
    {
        return token;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }
}
