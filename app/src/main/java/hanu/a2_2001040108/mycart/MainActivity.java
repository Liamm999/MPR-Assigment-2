package hanu.a2_2001040108.mycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hanu.a2_2001040108.mycart.Helper.ICartRecycler;
import hanu.a2_2001040108.mycart.Helper.ImageHandler;
import hanu.a2_2001040108.mycart.Helper.JSONPlaceholder;
import hanu.a2_2001040108.mycart.adapter.ProductAdapter;
import hanu.a2_2001040108.mycart.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ICartRecycler {
    private RecyclerView rcv;
    private SearchView sv;
    private LinearLayout loading;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.light_green)));
        getView();
        sv.setQueryHint("Search");

        // load products
        products = new ArrayList<>();
        loadProducts();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterProducts(s);
                return false;
            }
        });
    }

    private void getView() {
        loading = findViewById(R.id.loading);
        rcv = findViewById(R.id.main_rcv);
        sv = findViewById(R.id.sv);
    }

    private void loadProducts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hanu-congnv.github.io/mpr-cart-api/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        JSONPlaceholder jsonPlaceholder = retrofit.create(JSONPlaceholder.class);
        Call<List<Product>> call = jsonPlaceholder.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Handler handler = new Handler(Looper.getMainLooper());

                if (response.isSuccessful()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            sv.setVisibility(View.VISIBLE);
                            List<Product> productList = response.body();
                            assert productList != null;
                            products.addAll(productList);
                            ProductAdapter productAdapter = new ProductAdapter(products, MainActivity.this);
                            rcv.setAdapter(productAdapter);
                            rcv.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        }
                    }, 5000);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: filter products
    private void filterProducts(String value) {
        if (value.isEmpty()) {
            ProductAdapter productAdapter = new ProductAdapter(products, MainActivity.this);
            rcv.setAdapter(productAdapter);
            rcv.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        } else {
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(value.toLowerCase()) || product.getCategory().contains(value)) {
                    filteredProducts.add(product);
                }
            }
            ProductAdapter productAdapter = new ProductAdapter(filteredProducts, MainActivity.this);
            rcv.setAdapter(productAdapter);
            rcv.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    // TODO: switch screen when click to cart button on header
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_cart) {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTotalFooterPrice(List<Product> products) {
    }

    @Override
    public void reloadAdapter(List<Product> products) {
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