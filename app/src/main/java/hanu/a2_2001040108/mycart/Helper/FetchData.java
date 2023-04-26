package hanu.a2_2001040108.mycart.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hanu.a2_2001040108.mycart.MainActivity;
import hanu.a2_2001040108.mycart.R;
import hanu.a2_2001040108.mycart.adapter.ProductAdapter;
import hanu.a2_2001040108.mycart.model.Product;

public class FetchData extends AsyncTask<Void, String, String> {

    List<Product> products = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    Activity contextParent;
    ICartRecycler iCartRecycler;

    public FetchData(Activity contextParent, ICartRecycler iCartRecycler) {
        this.contextParent = contextParent;
        this.iCartRecycler = iCartRecycler;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Toast.makeText(contextParent, "Loading products", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = "https://hanu-congnv.github.io/mpr-cart-api/products.json";
        URL link;
        HttpURLConnection urlConnection;
        try {
            //put url to URL object
            link = new URL(url);
            //open connection with the url link
            urlConnection = (HttpURLConnection) link.openConnection();
            urlConnection.connect();
            //read json file
            InputStream is = urlConnection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder result = new StringBuilder();
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                result.append(line);
            }
            publishProgress(result.toString());
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        try {
            JSONArray jsonArray = new JSONArray(values[0]);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject root = (JSONObject) jsonArray.get(i);
                long id = root.getInt("id");
                String thumbnail = root.getString("thumbnail");
                String name = root.getString("name");
                String category = root.getString("category");
                int unitPrice = root.getInt("unitPrice");
                Product newProduct = new Product(id, thumbnail, name, category, unitPrice);
                products.add(newProduct);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        RecyclerView rcv = contextParent.findViewById(R.id.main_rcv);
        SearchView sv = contextParent.findViewById(R.id.sv);
        LinearLayout loading = contextParent.findViewById(R.id.loading);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (products.size() > 0) {
                    loading.setVisibility(View.GONE);
                    sv.setVisibility(View.VISIBLE);
                    ProductAdapter productAdapter = new ProductAdapter(products, iCartRecycler);
                    rcv.setAdapter(productAdapter);
                    rcv.setLayoutManager(new GridLayoutManager(contextParent, 2));
                }
            }
        }, 5000);

    }

    public List<Product> getProducts() {
        return products;
    }
}

