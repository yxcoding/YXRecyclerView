package net.yxcoding.yxrecyclerview;

/**
 * User: yxfang
 * Date: 2016-07-18
 * Time: 11:20
 * ------------- Description -------------
 * ---------------------------------------
 */
public class News
{
    private String title;
    private String subTitle;

    public News(String title, String subTitle)
    {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSubTitle()
    {
        return subTitle;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle = subTitle;
    }

    @Override
    public String toString()
    {
        return "News{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                '}';
    }
}
