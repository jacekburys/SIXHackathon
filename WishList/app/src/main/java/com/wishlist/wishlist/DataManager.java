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
    private static RequestQueue requestQueue;

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

    public static void getWishList(final MainActivity mainActivity) {

        final ArrayList<Product> products = new ArrayList<>();
        final ArrayList<DiscountedProduct> discountsArrList = new ArrayList<>();

        JSONObject info = new JSONObject();

        try {
            info.put("facebook_id", Profile.getCurrentProfile().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String URL = "https://freetrade.herokuapp.com/api/wishlist";
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
                                String asin = obj.getString("asin");

                                Log.d("product", id + " - " + name);
                                products.add(new Product(id, name, null, asin));
                            }

                            JSONArray discounts = response.getJSONArray("discounts");
                            for(int i=0; i<Math.min(discounts.length(), arr.length()); i++){
                                JSONArray temp = discounts.getJSONArray(i);
                                if(temp.length() > 0) {
                                    String exp = temp.getJSONObject(0).getString("expiry");
                                    double rate = temp.getJSONObject(0).getDouble("rate");
                                    discountsArrList.add(new DiscountedProduct(products.get(i), exp, rate));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        mainActivity.displayProducts(products);
                        mainActivity.displayDiscounts(discountsArrList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);
    }

    public static void updateSearch(final MainActivity mainActivity, String query) {



        JSONObject info = new JSONObject();
        try {
            info.put("index", "All");
            info.put("query", query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//https://freetrade.herokuapp.com
        final String URL = "https://freetrade.herokuapp.com/api/search";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, info,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("search", response.toString());
                        ArrayList<Product> itemsList = new ArrayList<>();

                        JSONArray items = null;
                        try {
                            items = response.getJSONArray("items");
                            for(int i=0; i<items.length(); i++){
                                JSONObject obj = items.getJSONObject(i);
                                String asin = obj.getString("ASIN");
                                String title = obj.getString("title");
                                String url = obj.getString("url");
                                Log.d("item", asin + " - " + title + " - " + url);
                                itemsList.add(new Product(asin, title, url, asin));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        mainActivity.loadHistory(itemsList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);
    }

    public static void addToWishlist(final MainActivity mainActivity,
                                     String asin, String name, String url,
                                     String startDate, String endDate, float probability) {

        JSONObject info = new JSONObject();
        try {
            info.put("facebook_id", Profile.getCurrentProfile().getId());
            info.put("asin", asin);
            info.put("name", name);
            info.put("url", url);
            info.put("start_date", startDate);
            info.put("end_date", endDate);
            info.put("probability", probability);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String URL = "https://freetrade.herokuapp.com/api/wishlist/add";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, info,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("wishlist_add", response.toString());
                        getWishList(mainActivity);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(req);
    }

    public static void removeFromWishlist(final MainActivity mainActivity,
                                     Product product) {

        JSONObject info = new JSONObject();
        try {
            info.put("facebook_id", Profile.getCurrentProfile().getId());
        } catch(JSONException e){
            e.printStackTrace();
        }


        final String URL = "https://freetrade.herokuapp.com/api/wishlist/destroy/" + product.getId();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, info,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("wishlist_remove", response.toString());
                        getWishList(mainActivity);
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
