package com.example.geonhokim.pospirl;

public class CompanyArticle
{

    private String title;
    private String links;
    private String contents;
    private String up_down;
    private String key_prob;

    public CompanyArticle(String title, String links, String contents, String up_down, String key_prob)
    {
        this.title = title;
        this.links = links;
        this.contents = contents;
        this.up_down = up_down;
        this.key_prob = key_prob;
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

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

    public String getUp_down()
    {
        return up_down;
    }

    public void setUp_down(String up_down)
    {
        this.up_down = up_down;
    }

    public String getKey_prob()
    {
        return key_prob;
    }

    public void setKey_prob(String key_prob)
    {
        this.key_prob = key_prob;
    }
}
