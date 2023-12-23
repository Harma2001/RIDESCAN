package com.example.tvsridescan.adapters;


public class DataModel_dtc
{


    public DataModel_dtc(String dtccode, String shrtdesc, String status, int colorcode) {
        this.dtccode = dtccode;
        this.shrtdesc = shrtdesc;
        this.status = status;
        this.colorcode = colorcode;
    }

    public String getDtccode() {
        return dtccode;
    }

    public String getShrtdesc() {
        return shrtdesc;
    }

    public String getStatus() {
        return status;
    }

    String dtccode;
    String shrtdesc;
    String status;

    public int getColorcode() {
        return colorcode;
    }

    int colorcode;



}
