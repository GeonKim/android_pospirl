package com.example.geonhokim.pospirl;

import java.util.Date;

public class CompanyArticle
{
    private String datetime;
    private String title;
    private String links;
    private double scores;
    private String keywords;

    public CompanyArticle(String datetime, String title, String links, double scores, String keywords)
    {
        this.datetime = datetime;
        this.title = title;
        this.links = links;
        this.scores = scores;
        this.keywords = keywords;
    }

    public String getDatetime()
    {
        return datetime;
    }

    public void setDatetime(String datetime)
    {
        this.datetime = datetime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLinks()
    {
        return links;
    }

    public void setLinks(String links)
    {
        this.links = links;
    }

    public double getScores()
    {
        return scores;
    }

    public void setScores(double scores)
    {
        this.scores = scores;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }
}
