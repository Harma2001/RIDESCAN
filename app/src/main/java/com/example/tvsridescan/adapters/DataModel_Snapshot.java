package com.example.tvsridescan.adapters;


public class DataModel_Snapshot
{
    public DataModel_Snapshot(String val, String shrtdesc)
    {
        this.val = val;
        this.shrtdesc = shrtdesc;
    }

    public String getVal()
    {
        return val;
    }

    public String getShrtdesc()
    {
        return shrtdesc;
    }

    String val;
    String shrtdesc;

}
