package com.tmall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.tmall.R;
import com.tmall.pojo.Product;
import com.tmall.util.VolleyUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SONG on 2016/5/9.
 */
public class ProductListRecyclerViewAdapter
        extends RecyclerView.Adapter<ProductListRecyclerViewAdapter.ViewHolder>{
    private List<Product> products;
    private Context context;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;

    public ProductListRecyclerViewAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;

        context.getTheme().resolveAttribute(R.attr.selectableItemBackground,
                mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item,parent,false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(String.format("%.2f",product.getPrice()));

        ImageLoader imageLoader = VolleyUtil.
                getInstance(context).getImageLoader();
        ImageLoader.ImageListener listener = ImageLoader.
                getImageListener(holder.imageProductImgPath,
                        R.drawable.me,R.mipmap.ic_launcher);
        imageLoader.get(product.getImgPath(),listener);
        
        // 选项的点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_product_img_path)
        ImageView imageProductImgPath;
        @Bind(R.id.tv_product_name)
        TextView tvProductName;
        @Bind(R.id.tv_product_price)
        TextView tvProductPrice;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
