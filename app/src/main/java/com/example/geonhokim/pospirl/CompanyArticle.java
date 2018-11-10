package com.example.geonhokim.pospirl;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class CompanyArticle
{

    private String title;
    private String links;
    private String contents;
    private String up_down;
    private String key_prob;

    public CompanyArticle()
    {
        // Default constructor required for calls to DataSnapshot.getValue(CompanyArticle.class)
    }

    public CompanyArticle(String title, String links, String contents, String up_down, String key_prob)
    {
        this.title = title;
        this.links = links;
        this.contents = contents;
        this.up_down = up_down;
        this.key_prob = key_prob;
    }

    // [START CompanyArticle_to_map]
    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("links", links);
        result.put("contents", contents);
        result.put("up_down", up_down);
        result.put("key_prob", key_prob);

        return result;
    }
    // [END CompanyArticle_to_map]

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
