package com.wishlist.wishlist;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jacek on 05/03/16.
 */
public class DataManager {

    private final static DataManager instance = new DataManager();

    private Context context;
    private RequestQueue requestQueue;

    private DataManager() {}

    public static DataManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.requestQueue.start();
    }

    public void createNewSession(JSONObject callback_information, final LoginActivity loginActivity) {

        final String URL = "https://freetrade.herokuapp.com/users";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, callback_information,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("user", response.toString());
                        loginActivity.goToMain();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);
    }

    public void getWishList(final MainActivity mainActivity) {

        final ArrayList<Product> products = new ArrayList<>();

        JSONObject info = new JSONObject();

        try {
            info.put("facebook_id", Profile.getCurrentProfile().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String URL = "https://freetrade.herokuapp.com/products";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, info,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("producsts", response.toString());

                        try {
                            JSONArray arr = response.getJSONArray("products");
                            for(int i=0; i<arr.length(); i++){
                                JSONObject obj = arr.getJSONObject(i);
                                String id = obj.getString("id");
                                String name = obj.getString("name");
                                Log.d("product", id + " - " + name);
                                products.add(new Product(id, name));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        mainActivity.displayProducts(products);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);
    }

}
