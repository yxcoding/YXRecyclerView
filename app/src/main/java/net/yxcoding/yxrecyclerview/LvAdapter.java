package net.yxcoding.yxrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * User: yxfang
 * Date: 2016-07-18
 * Time: 10:05
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class LvAdapter extends RecyclerView.Adapter<LvAdapter.ViewHolder>
{
    private final List<News> data;

    public LvAdapter(List<News> data)
    {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_layout, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        News news = data.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvSubTitle.setText(news.getSubTitle());
    }


    @Override
    public int getItemCount()
    {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TextView tvSubTitle;

        public ViewHolder(View itemView)
        {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tv_subTitle);
        }
    }
}
