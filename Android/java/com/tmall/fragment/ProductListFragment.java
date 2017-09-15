package com.tmall.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.tmall.R;
import com.tmall.adapter.ProductListRecyclerViewAdapter;
import com.tmall.pojo.Product;
import com.tmall.util.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 1.MainActivity 中菜单 refresh 事件
 *   1.1 根据 某某 排序
 *   1.2 搜索 功能
 * 2.订单
 * 3.使用轮询推送 折扣/新宝贝
 */
public class ProductListFragment extends Fragment {
    private List<Product> products;
    private ProductListRecyclerViewAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private int lastVisibleItem;
    private int page = 1;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        products = new ArrayList<>();
        loadProductList();
        adapter = new ProductListRecyclerViewAdapter(getActivity(), products);
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //此处是android自带的只支持下拉刷新:加载最新数据 (未读数据)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              
                //swipeRefreshLayout.setRefreshing(true);
                //page--;  // 设置 page=3;
                //loadProductList();
                loadHeadProductList();

            }
        });

        // 上拉刷新:加载更多数据 (历史数据)
        //此处我们是对recyclerview添加scrollListener ,监听滑倒最后一个可见的条目的时候，刷新加载数据
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //SCROLL_STATE_DRAGGING  和   SCROLL_STATE_IDLE 两种效果自己看着来
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    swipeRefreshLayout.setRefreshing(true);
                    page++;
                    loadProductList();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        return view;
    }

    // 下拉刷新加载最新数据 (未读数据)
    private void loadHeadProductList() {
        Product product = new Product();
        product.setId(100);
        product.setName("Nexus x5");
        product.setPrice(3000d);
        product.setImgPath(VolleyUtil.BASE_URL + "images/nexus5.jpg");
        products.add(0, product);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 通过 Volley 加载云端商品列表数据
     */
    private void loadProductList() {
        // total 总数据数目
        // size 每页显示数目
        // int pageSize = total % size == 0 ? total / size : (total / size + 1);
       
        // 当前页不能大于总页数
        if (page > 3) {
            Product product = new Product();
            product.setId(100);
            product.setName("Nexus x5");
            product.setPrice(3000d);
            product.setImgPath(VolleyUtil.BASE_URL + "images/nexus5.jpg");
            products.add(product);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        JsonArrayRequest request = new JsonArrayRequest(
                VolleyUtil.BASE_URL + "ProductServletJson?pageno=" + page,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ja) {
                        Log.v(VolleyUtil.TAG, ja.toString());
                        try {
                            for (int i = 0; i < ja.length(); i++) {
                                // {"id":2,"name":"iphone5","price":5600,"remark":"超长超薄","imgPath":"images\/iphone6.jpg"}
                                JSONObject jo = ja.getJSONObject(i);
                                Product product = new Product();
                                product.setId(jo.getInt("id"));
                                product.setName(jo.getString("name"));
                                product.setPrice(jo.getDouble("price"));
                                product.setRemark("remark");
                                product.setImgPath(VolleyUtil.BASE_URL + jo.getString("imgPath"));
                                products.add(product);
                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception e) {
                            Log.v(VolleyUtil.TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(VolleyUtil.TAG, error.getMessage(), error);
                    }
                }
        );
        VolleyUtil.getInstance(getActivity()).addToRequestQueue(request);
    }
}
