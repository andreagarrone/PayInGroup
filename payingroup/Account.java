package com.example.andre.payingroup;

/**
 * Created by Andre on 13/04/18.
 */

public class Account {

    public String accountId;
    public String name;
    public String email;

    public Account(){}

    public Account(String accountId, String name, String email){
        this.accountId = accountId;
        this.name = name;
        this.email = email;
    }
}
