package hanu.a2_2001040108.mycart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hanu.a2_2001040108.mycart.Helper.DBHelper;
import hanu.a2_2001040108.mycart.Helper.ICartRecycler;
import hanu.a2_2001040108.mycart.Helper.ImageHandler;
import hanu.a2_2001040108.mycart.Helper.MoneyFormatter;
import hanu.a2_2001040108.mycart.adapter.CartProductAdapter;
import hanu.a2_2001040108.mycart.adapter.ProductAdapter;
import hanu.a2_2001040108.mycart.model.Product;



@SuppressLint("CustomSplashScreen")
public class CartActivity extends AppCompatActivity implements ICartRecycler {
    private RecyclerView recyclerView;
    private TextView tv_total_price_footer;
    List<Product> productList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.light_green)));
        productList = new ArrayList<>();
        getView();
        loadProducts();
        setTotalFooterPrice(productList);
    }

    private void getView() {
        recyclerView = findViewById(R.id.cart_rcv);
        tv_total_price_footer = findViewById(R.id.total_price_footer);
    }

    private void loadProducts() {
        DBHelper db = new DBHelper(CartActivity.this);
        productList = db.getAllItems();
        CartProductAdapter cartAdapter = new CartProductAdapter(productList, this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(CartActivity.this, 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_cart) {
            Toast.makeText(this, "You already in your Cart", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setTotalFooterPrice(List<Product> products) {
        int total = 0;
        for (Product product : products) {
            total += product.getUnitPrice() * product.getAmount();
        }
        tv_total_price_footer.setText("VND " + MoneyFormatter.withLargeIntegers(total));
    }

    @Override
    public void reloadAdapter(List<Product> newProducts) {
        CartProductAdapter cartAdapter = new CartProductAdapter(newProducts, this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(CartActivity.this, 1));
    }

    @Override
    public void loadImageFromAPI(String url, ImageView imageView) {
        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bmImg = ImageHandler.fetchImage(url);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bmImg != null) {
                            imageView.setImageBitmap(bmImg);
                        }
                    }
                });
            }
        });
    }
}
