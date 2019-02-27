package com.example.andre.payingroup;

/**
 * Created by Andre on 29/04/18.
 */

public class Table {

    public String tableCreatorId;
    public String tableName;
    public String tablePassword;
    public String tableId;

    public Table(){}

    public String getTableName() {
        return tableName;
    }

    public String getTablePassword() {
        return tablePassword;
    }


    public Table(String tableCreatorId, String tableName, String tablePassword, String tableId){
        this.tableCreatorId = tableCreatorId;
        this.tableName = tableName;
        this.tablePassword = tablePassword;
        this.tableId = tableId;
    }
}
