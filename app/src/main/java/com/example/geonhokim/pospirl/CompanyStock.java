package com.example.geonhokim.pospirl;

public class CompanyStock
{
    private String date;
    private String close;
    private String upper;
    private String lower;

    // Default constructor required for calls to DataSnapshot.getValue(CompanyStock.class)
    public CompanyStock()
    {
    }

    public CompanyStock(String date, String close, String upper, String lower)
    {
        this.date = date;
        this.close = close;
        this.upper = upper;
        this.lower = lower;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getClose()
    {
        return close;
    }

    public void setClose(String close)
    {
        this.close = close;
    }

    public String getUpper()
    {
        return upper;
    }

    public void setUpper(String upper)
    {
        this.upper = upper;
    }

    public String getLower()
    {
        return lower;
    }

    public void setLower(String lower)
    {
        this.lower = lower;
    }
}
