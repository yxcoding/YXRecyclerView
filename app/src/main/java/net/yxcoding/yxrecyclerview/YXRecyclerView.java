package net.yxcoding.yxrecyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: yxfang
 * Date: 2016-07-18
 * Time: 09:43
 * ------------- Description -------------
 * YXRecyclerView
 * ---------------------------------------
 */
public class YXRecyclerView extends SwipeRefreshLayout
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private OnLoadingListener onLoadingListener;
    private OnItemClickListener onItemClickListener;

    private YXRecyclerViewAdapter yxRecyclerViewAdapter;

    private View header;
    private View loadingMoreFooter;

    private boolean isRefreshLoading = false;
    private boolean isMoreLoading = false;

    // default Recycler's mode
    private Mode mode = Mode.PULL_TO_REFRESH;
    private Context context;

    public YXRecyclerView(Context context)
    {
        super(context);
        init(context);
    }

    public YXRecyclerView(Context context, Mode mode)
    {
        super(context);
        this.mode = mode;
        init(context);
    }

    public YXRecyclerView(Context context, Mode mode, RecyclerView.LayoutManager layoutManager)
    {
        this(context, mode);
        this.layoutManager = layoutManager;
    }

    public YXRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        recyclerView = new RecyclerView(context);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addLoadingMoreFooterView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * show the loading view
     */
    public void showLoadingView()
    {
        try
        {
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
            mCircleView.setAccessible(true);
            View progress = (View) mCircleView.get(this);
            progress.setVisibility(VISIBLE);

            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(this, true, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * set the LayoutManager
     *
     * @param layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager)
    {
        this.layoutManager = layoutManager;
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * return the RecyclerView
     *
     * @return
     */
    public RecyclerView getRecyclerView()
    {
        return recyclerView;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
        addLoadingMoreFooterView();
    }

    /**
     * set the loading view color
     *
     * @param colors
     */
    public void setLoadingColor(int... colors)
    {
        setColorSchemeResources(colors);
    }

    /**
     * set the adapter
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter)
    {
        if (adapter != null)
        {
            yxRecyclerViewAdapter = new YXRecyclerViewAdapter(adapter);
            recyclerView.setAdapter(yxRecyclerViewAdapter);
        }
    }

    public void refresh()
    {
        if (yxRecyclerViewAdapter != null)
        {
            yxRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * add the headerView for the recyclerView
     *
     * @param view
     */
    public void addHeaderView(View view)
    {
        this.header = view;
    }

    /**
     * add the loading more footer when the mode == Mode.BOTH
     */
    private void addLoadingMoreFooterView()
    {
        if (mode == Mode.BOTH)
        {
            loadingMoreFooter = new LoadingMoreFooter(context);
            loadingMoreFooter.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            loadingMoreFooter.setVisibility(View.GONE);
        }
    }

    /**
     * set the OnLoadingListener
     *
     * @param listener
     */
    public void setOnLoadingListener(OnLoadingListener listener)
    {
        if (listener != null)
        {
            onLoadingListener = listener;

            setOnRefreshListener(new OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    isRefreshLoading = true;
                    onLoadingListener.onRefreshLoading();
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                            !isRefreshLoading &&
                            !isMoreLoading)
                    {
                        int lastVisibleItemPosition;

                        // LinearLayoutManager
                        if (layoutManager instanceof LinearLayoutManager)
                        {
                            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        }
                        // GridLayoutManager
                        else if (layoutManager instanceof GridLayoutManager)
                        {
                            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        }
                        // StaggeredGridLayoutManager
                        else
                        {
                            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                            lastVisibleItemPosition = getLastItemPosition(into);
                        }

                        if (layoutManager.getChildCount() > 0
                                && lastVisibleItemPosition >= layoutManager.getItemCount() - 2
                                && layoutManager.getItemCount() > layoutManager.getChildCount())
                        {
                            if (loadingMoreFooter != null && loadingMoreFooter.getVisibility() == View.GONE)
                            {
                                loadingMoreFooter.setVisibility(View.VISIBLE);
                            }

                            isMoreLoading = true;
                            onLoadingListener.onMoreLoading();
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    private int getLastItemPosition(int[] lastPositions)
    {
        int max = lastPositions[0];
        for (int value : lastPositions)
        {
            if (value > max)
            {
                max = value;
            }
        }
        return max;
    }

    public boolean isRefreshLoading()
    {
        return isRefreshLoading;
    }

    /**
     * to call when the data is loaded complete
     */
    public void loadComplete()
    {
        setRefreshing(false);

        isRefreshLoading = false;
        isMoreLoading = false;

        yxRecyclerViewAdapter.notifyDataSetChanged();

        if (mode == Mode.BOTH)
        {
            loadingMoreFooter.setVisibility(View.GONE);
        }
    }

    /**
     * recyclerView loading listener
     * onRefreshLoading() is to pull refresh
     * onMoreLoading() is to load more
     */
    public interface OnLoadingListener
    {
        void onRefreshLoading();

        void onMoreLoading();
    }

    private class YXRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private RecyclerView.Adapter adapter;

        public static final int TYPE_HEADER = -1;
        public static final int TYPE_ITEM = 0;
        public static final int TYPE_FOOTER = 1;

        public YXRecyclerViewAdapter(RecyclerView.Adapter adapter)
        {
            this.adapter = adapter;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView)
        {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager)
            {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
                {
                    @Override
                    public int getSpanSize(int position)
                    {
                        return (isHeaderView(position) || isFooterView(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
        {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeaderView(holder.getLayoutPosition()) || isFooterView(holder.getLayoutPosition())))
            {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        /**
         * check the position is headerView
         *
         * @param position
         * @return
         */
        private boolean isHeaderView(int position)
        {
            return position >= 0 && position < 1;
        }

        /**
         * check the position is footerView
         *
         * @param position
         * @return
         */
        private boolean isFooterView(int position)
        {
            return position + 1 >= getItemCount();
        }

        @Override
        public int getItemViewType(int position)
        {
            if (isFooterView(position))
            {
                return TYPE_FOOTER;
            }
            else if (isHeaderView(position))
            {
                return TYPE_HEADER;
            }
            int adjPosition = position - 1;
            int adapterCount;
            if (adapter != null)
            {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount)
                {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
        {
            if (viewType == TYPE_FOOTER)
            {
                Log.d("onCreateViewHolder", "TYPE_FOOTER");
                return new ViewHolder(loadingMoreFooter);
            }
            else if (viewType == TYPE_HEADER)
            {
                Log.d("onCreateViewHolder", "TYPE_HEADER ");
                return new ViewHolder(header);
            }
            Log.d("onCreateViewHolder", "TYPE_ITEM");
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
        {
            if (isHeaderView(position))
            {
                return;
            }

            final int adjPosition = position - 1;
            int adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount)
            {
                holder.itemView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (onItemClickListener != null)
                        {
                            onItemClickListener.onItemClick(null, holder.itemView, adjPosition);
                        }
                    }
                });
                adapter.onBindViewHolder(holder, adjPosition);
            }

        }

        @Override
        public int getItemCount()
        {
            return 1 + 1 + adapter.getItemCount();
        }

        private class ViewHolder extends RecyclerView.ViewHolder
        {
            public ViewHolder(View itemView)
            {
                super(itemView);
            }
        }
    }

    /**
     * RecyclerView mode
     */
    public enum Mode
    {
        /**
         * pull refresh
         */
        PULL_TO_REFRESH(0x1),

        /**
         * pull refresh and load more
         */
        BOTH(0x2);

        private int mode;

        Mode(int mode)
        {
            this.mode = mode;
        }
    }

    /**
     * custom item click listener
     */
    public interface OnItemClickListener
    {
        void onItemClick(AdapterView<?> parent, View view, int position);
    }

}
