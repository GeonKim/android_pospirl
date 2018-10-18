package com.example.geonhokim.pospirl;

public class CompanyArticle
{
//    String title, links;
//    double up_prob, down_prob;
//    String word1, word2, word3, word4, word5, word6;
//    double prob1, prob2, prob3, prob4, prob5, prob6;


    private String title;
    private String links;
    private String contents;
    private double scores;
    private double up_prob;
    private double down_prob;
    private String word1;
    private String word2;
    private String word3;
    private String word4;
    private String word5;
    private String word6;

    private double prob1;
    private double prob2;
    private double prob3;
    private double prob4;
    private double prob5;
    private double prob6;


    public CompanyArticle(String title, String links, String contents, double scores, double up_prob, double down_prob, String word1, String word2, String word3, String word4, String word5, String word6, double prob1, double prob2, double prob3, double prob4, double prob5, double prob6)
    {
        this.title = title;
        this.links = links;
        this.contents = contents;
        this.scores = scores;
        this.up_prob = up_prob;
        this.down_prob = down_prob;
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
        this.word4 = word4;
        this.word5 = word5;
        this.word6 = word6;
        this.prob1 = prob1;
        this.prob2 = prob2;
        this.prob3 = prob3;
        this.prob4 = prob4;
        this.prob5 = prob5;
        this.prob6 = prob6;
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

    public double getScores()
    {
        return scores;
    }

    public void setScores(double scores)
    {
        this.scores = scores;
    }

    public double getUp_prob()
    {
        return up_prob;
    }

    public void setUp_prob(double up_prob)
    {
        this.up_prob = up_prob;
    }

    public double getDown_prob()
    {
        return down_prob;
    }

    public void setDown_prob(double down_prob)
    {
        this.down_prob = down_prob;
    }

    public String getWord1()
    {
        return word1;
    }

    public void setWord1(String word1)
    {
        this.word1 = word1;
    }

    public String getWord2()
    {
        return word2;
    }

    public void setWord2(String word2)
    {
        this.word2 = word2;
    }

    public String getWord3()
    {
        return word3;
    }

    public void setWord3(String word3)
    {
        this.word3 = word3;
    }

    public String getWord4()
    {
        return word4;
    }

    public void setWord4(String word4)
    {
        this.word4 = word4;
    }

    public String getWord5()
    {
        return word5;
    }

    public void setWord5(String word5)
    {
        this.word5 = word5;
    }

    public String getWord6()
    {
        return word6;
    }

    public void setWord6(String word6)
    {
        this.word6 = word6;
    }

    public double getProb1()
    {
        return prob1;
    }

    public void setProb1(double prob1)
    {
        this.prob1 = prob1;
    }

    public double getProb2()
    {
        return prob2;
    }

    public void setProb2(double prob2)
    {
        this.prob2 = prob2;
    }

    public double getProb3()
    {
        return prob3;
    }

    public void setProb3(double prob3)
    {
        this.prob3 = prob3;
    }

    public double getProb4()
    {
        return prob4;
    }

    public void setProb4(double prob4)
    {
        this.prob4 = prob4;
    }

    public double getProb5()
    {
        return prob5;
    }

    public void setProb5(double prob5)
    {
        this.prob5 = prob5;
    }

    public double getProb6()
    {
        return prob6;
    }

    public void setProb6(double prob6)
    {
        this.prob6 = prob6;
    }
}
