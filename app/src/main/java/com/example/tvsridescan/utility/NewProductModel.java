package com.example.tvsridescan.utility;

public class NewProductModel {




    private String prod_id,prod_name;
    private int img_url;

    public NewProductModel(String prod_id, String prod_name, int img_url)
    {
        this.prod_id = prod_id;
        this.prod_name = prod_name;
        this.img_url = img_url;
    }

    public String getProd_id()
    {
       return prod_id;
    }
    public void setProd_id(String id)
    {
        this.prod_id = id;
    }

    public String getProd_name()
    {
        return prod_name;
    }
    public void setProd_name(String name)
    {
        this.prod_name = name;
    }
    public int getImg_url()
    {
        return img_url;
    }
    public void setImg_url(int img_url)
    {
        this.img_url = img_url;
    }

}
