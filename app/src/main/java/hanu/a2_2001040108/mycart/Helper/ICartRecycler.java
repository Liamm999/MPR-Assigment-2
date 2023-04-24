package hanu.a2_2001040108.mycart.Helper;

import android.widget.ImageView;

import java.util.List;

import hanu.a2_2001040108.mycart.model.Product;

public interface ICartRecycler {
    void setTotalFooterPrice(List<Product> products);
    void reloadAdapter(List<Product> products);
    void loadImageFromAPI(String url, ImageView iv);
}
