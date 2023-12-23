package com.example.tvsridescan.adapters;


public class DataModel_Config
{

    public DataModel_Config(String vciserial, String expdate) {
        this.vciserial = vciserial;
        this.expdate = expdate;
    }

    public String getVciserial() {
        return vciserial;
    }

    public String getExpdate() {
        return expdate;
    }

    String vciserial;
    String expdate;



}
