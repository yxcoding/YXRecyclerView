package net.yxcoding.yxrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements YXRecyclerView.OnLoadingListener
{
    private YXRecyclerView yxRecyclerView;
    private LvAdapter lvAdapter;

    private List<News> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yxRecyclerView = (YXRecyclerView) findViewById(R.id.yxRecyclerView);
        yxRecyclerView.setMode(YXRecyclerView.Mode.BOTH);
        yxRecyclerView.setLoadingColor(R.color.holo_orange_light, R.color.holo_orange_dark, R.color.holo_purple);
        yxRecyclerView.setOnLoadingListener(this);
        yxRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //yxRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        ViewPager viewPager = new ViewPager(this);
        final List<View> pages = new ArrayList<>();
        pages.add(LayoutInflater.from(this).inflate(R.layout.page1, null));
        pages.add(LayoutInflater.from(this).inflate(R.layout.page2, null));
        viewPager.setAdapter(new PagerAdapter()
        {
            @Override
            public int getCount()
            {
                return pages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object)
            {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position)
            {
                container.addView(pages.get(position));
                return pages.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object)
            {
                container.removeView(pages.get(position));
            }
        });
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, getResources().getDisplayMetrics())));
        yxRecyclerView.addHeaderView(viewPager);

        yxRecyclerView.setOnItemClickListener(new YXRecyclerView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position)
            {
                Toast.makeText(MainActivity.this, data.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        lvAdapter = new LvAdapter(data);
        yxRecyclerView.setAdapter(lvAdapter);

        yxRecyclerView.showLoadingView();
    }

    @Override
    public void onRefreshLoading()
    {
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void onMoreLoading()
    {
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (yxRecyclerView.isRefreshLoading())
            {
                data.clear();
                for (int i = 0; i < 20; i++)
                {
                    News news = new News("我是标题" + i, "我是副标题" + i);
                    data.add(news);
                }
            }
            else
            {
                int size = data.size();
                for (int i = size; i < size + 20; i++)
                {
                    News news = new News("我是标题" + i, "我是副标题" + i);
                    data.add(news);
                }
            }
            yxRecyclerView.loadComplete();
        }
    };
}
