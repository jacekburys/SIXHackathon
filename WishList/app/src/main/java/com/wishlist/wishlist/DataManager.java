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

import org.json.JSONObject;

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

    public void createNewSession(JSONObject callback_information) {

        final String URL = "http://172.16.10.139:3002/users/auth/facebook/callback";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, callback_information,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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
