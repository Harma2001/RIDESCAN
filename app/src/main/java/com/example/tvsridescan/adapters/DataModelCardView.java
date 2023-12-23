package com.example.tvsridescan.adapters;

public class DataModelCardView
{
    String lpno;
    String lpname;
    String lpval1;


    public DataModelCardView(String lpno, String lpname, String lpval1, int colorcode)
    {
        this.lpno = lpno;
        this.lpname = lpname;
        this.lpval1 = lpval1;
        this.colorcode = colorcode;
    }

    public String getLpno() {
        return lpno;
    }
    public String getLpname() {
        return lpname;
    }

    public String getLpval1() {
        return lpval1;
    }

    public int getColorcode() {
        return colorcode;
    }

    int colorcode =0;


}
