package hanu.a2_2001040108.mycart.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hanu.a2_2001040108.mycart.Helper.DBHelper;
import hanu.a2_2001040108.mycart.Helper.ICartRecycler;
import hanu.a2_2001040108.mycart.Helper.MoneyFormatter;
import hanu.a2_2001040108.mycart.R;
import hanu.a2_2001040108.mycart.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products;

    Context context;
    ICartRecycler mListener;

    public ProductAdapter(List<Product> newProducts, ICartRecycler mListener) {
        products = newProducts;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View productView = inflater.inflate(R.layout.cart_item, parent, false);


        // Return a new holder instance
        return new ViewHolder(productView, this.mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int index) {
        // Get the data model based on position
        Product product = products.get(index);

        // TODO: Set item views based on your views and data model
        // image
        ImageView iv = holder.iv;
        mListener.loadImageFromAPI(product.getThumbnail(), iv);

        // name
        TextView tv_name = holder.tv_name;
        tv_name.setText(product.getName());

        // price
        TextView tv_item_price = holder.tv_item_price;
        String price = "VND " + MoneyFormatter.withLargeIntegers(product.getUnitPrice());
        tv_item_price.setText(price);

        // add to cart
        ImageButton addToCart = holder.ib_add_cart;
        DBHelper dbHelper = new DBHelper(context);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.ifItemExist(product.getId())) {
                    int item_amount = dbHelper.getAmountItemsById(product.getId()) + 1;
                    product.setAmount(item_amount);
                    dbHelper.editAmountItemsById(Long.parseLong(String.valueOf(product.getId())), product.getAmount());
                    Toast.makeText(context, "Added " + product.getAmount() + " items two your cart", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addNewCourse(String.valueOf(product.getId()), product.getName(), product.getThumbnail() ,String.valueOf(product.getUnitPrice()), String.valueOf(product.getAmount() + 1));
                    Toast.makeText(context, "Added 1 item two your cart", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tv_name;
        public TextView tv_item_price;
        public ImageButton ib_add_cart;
        ICartRecycler mListener;

        public ViewHolder(@NonNull View itemView, ICartRecycler mListener) {
            super(itemView);
            this.mListener = mListener;

            iv = itemView.findViewById(R.id.iv);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_item_price = itemView.findViewById(R.id.tv_item_price);
            ib_add_cart = itemView.findViewById(R.id.add_cart_btn);
        }
    }
}
