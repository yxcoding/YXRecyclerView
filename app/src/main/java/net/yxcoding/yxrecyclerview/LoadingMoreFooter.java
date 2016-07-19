package net.yxcoding.yxrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * User: yxfang
 * Date: 2016-07-18
 * Time: 13:54
 * ------------- Description -------------
 * loading more view
 * ---------------------------------------
 */
public class LoadingMoreFooter extends LinearLayout
{

    public LoadingMoreFooter(Context context)
    {
        super(context);
        init(context);
    }


    public LoadingMoreFooter(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        setGravity(Gravity.CENTER);
        View root = LayoutInflater.from(context).inflate(R.layout.loading_more_footer, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(root, lp);
    }
}
