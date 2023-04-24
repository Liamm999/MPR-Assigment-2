package hanu.a2_2001040108.mycart.Helper;

import java.util.List;

import hanu.a2_2001040108.mycart.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;

public interface JSONPlaceholder {
    @GET("products.json")
    Call<List<Product>> getProducts();
}
