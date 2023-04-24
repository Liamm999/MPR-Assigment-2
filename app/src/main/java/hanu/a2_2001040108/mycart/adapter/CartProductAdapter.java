package hanu.a2_2001040108.mycart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import hanu.a2_2001040108.mycart.Helper.DBHelper;
import hanu.a2_2001040108.mycart.Helper.ICartRecycler;
import hanu.a2_2001040108.mycart.Helper.MoneyFormatter;
import hanu.a2_2001040108.mycart.R;
import hanu.a2_2001040108.mycart.model.Product;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private final List<Product> products;
    Context context;
    ICartRecycler mListener;

    public CartProductAdapter(List<Product> products, ICartRecycler mListener) {
        this.products = products;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public CartProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.cart_item_horizontal, parent, false);

        // Return a new holder instance
        return new ViewHolder(view, this.mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        Product product = products.get(index);
        DBHelper dbHelper = new DBHelper(context);


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

        // amount
        TextView tv_amount = holder.tv_amount;
        String amount = String.valueOf(product.getAmount());
        tv_amount.setText(amount);

        // total price
        TextView tv_total_price = holder.tv_total_price;
        String totalPrice = "VND " + MoneyFormatter.withLargeIntegers(product.getAmount() * product.getUnitPrice());
        tv_total_price.setText(totalPrice);

        // add item
        ImageButton ib_add = holder.ib_add;
        ib_add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (dbHelper.ifItemExist(product.getId())) {
                    int item_amount = dbHelper.getAmountItemsById(product.getId()) + 1;
                    product.setAmount(item_amount);
                    dbHelper.editAmountItemsById(Long.parseLong(String.valueOf(product.getId())), product.getAmount());
                    tv_amount.setText(String.valueOf(product.getAmount()));
                    tv_total_price.setText("VND " + MoneyFormatter.withLargeIntegers(product.getAmount() * product.getUnitPrice()));
                    mListener.setTotalFooterPrice(products);

                }
            }
        });

        // minus item
        ImageButton ib_minus = holder.ib_minus;
        ib_minus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (product.getAmount() > 1) {
                    int item_amount = dbHelper.getAmountItemsById(product.getId()) - 1;
                    product.setAmount(item_amount);
                    dbHelper.editAmountItemsById(Long.parseLong(String.valueOf(product.getId())), product.getAmount());
                    tv_amount.setText(String.valueOf(product.getAmount()));
                    tv_total_price.setText("VND " + MoneyFormatter.withLargeIntegers(product.getAmount() * product.getUnitPrice()));
                    mListener.setTotalFooterPrice(products);
                } else {
                    dbHelper.deleteItem(product.getId());
                    products.remove(products.get(holder.getAdapterPosition()));
                    mListener.setTotalFooterPrice(products);
                    mListener.reloadAdapter(products);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tv_name;
        public TextView tv_item_price;
        public TextView tv_amount;
        public TextView tv_total_price;
        public ImageButton ib_add;
        public ImageButton ib_minus;
        ICartRecycler mListener;
        public ViewHolder(@NonNull View itemView, ICartRecycler mListener) {
            super(itemView);
            this.mListener = mListener;
            iv = itemView.findViewById(R.id.iv_horizontal);
            tv_name = itemView.findViewById(R.id.tv_name_horizontal);
            tv_item_price = itemView.findViewById(R.id.tv_item_price_horizontal);
            tv_amount = itemView.findViewById(R.id.amount_items_horizontal);
            tv_total_price = itemView.findViewById(R.id.total_cost_horizontal);
            ib_add = itemView.findViewById(R.id.btn_add_horizontal);
            ib_minus = itemView.findViewById(R.id.btn_minus_horizontal);
        }
    }
}
