package com.wishlist.wishlist;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacek on 05/03/16.
 */
public class MainActivity extends Activity {

    private ViewFlipper viewFlipper;
    private float lastX;

    MyCustomAdapter dataAdapter = null;
    private Menu menu;

    private ListView listView;
    private ArrayList<Product> products;

    private MainActivity mainActivity;
    private long lastSearchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        this.mainActivity = this;

        DataManager.getInstance().getWishList(this);
        this.lastSearchTime = System.currentTimeMillis();
    }


    public void displayProducts(ArrayList<Product> products) {
        this.products = products;
        setAdapter(products);
    }

    private void setAdapter(ArrayList<Product> products) {
        //create an ArrayAdaptar from the String Array
        this.products = products;
        dataAdapter = new MyCustomAdapter(this,
                R.layout.product_item, products);
        ListView listView = (ListView)findViewById(R.id.listViewWishlist);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }









    private class MyCustomAdapter extends ArrayAdapter<Product> {

        private ArrayList<Product> productsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Product> productsList) {
            super(context, textViewResourceId, productsList);
            this.productsList = new ArrayList<Product>();
            this.productsList.addAll(productsList);
        }

        private class ViewHolder {
            TextView code;
            Button name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.product_item, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.textView);
                holder.name = (Button) convertView.findViewById(R.id.button);
                convertView.setTag(holder);

                final String name = holder.code.getText().toString();
                holder.name.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext().getApplicationContext(),
                                name + " should be removed",
                                Toast.LENGTH_LONG).show();
                    }
                });

                /*
                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Product product = (Product) cb.getTag();

                        Toast.makeText(getContext().getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();

                        product.setSelected(cb.isChecked());
                    }
                });
                */
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Product product = productsList.get(position);
            holder.code.setText(product.getName());
            //holder.name.setText(friend.getName());
            //holder.name.setChecked(friend.isSelected());
            //holder.name.setTag(friend);

            return convertView;

        }
    }


    ////////////////////////////////////////////////

    // Using the following method, we will handle all screen swaps.
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();

                // Handling left to right screen swap.
                if (lastX < currentX) {
                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                    break;
                    // Next screen comes in from left.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                    // Display next screen.
                    viewFlipper.showNext();
                }

                // Handling right to left screen swap.
                if (lastX > currentX) {
                    // If there is a child (to the left), kust break.
                    if (viewFlipper.getDisplayedChild() == 1)
                    break;
                    // Next screen comes in from right.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    // Current screen goes out from left
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                    // Display previous screen.
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example, menu);
        this.menu = menu;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setIconifiedByDefault(false);
            search.setFocusable(true);
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    long timeDiff = (System.currentTimeMillis() - lastSearchTime) / 1000;
                    //if(timeDiff > 3) {
                    DataManager.updateSearch(mainActivity, query);
                    //}

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {


                    return true;
                }

            });


        }

        return true;
    }


    public void loadHistory(ArrayList<String> items) {

        lastSearchTime = System.currentTimeMillis();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };

            MatrixCursor cursor = new MatrixCursor(columns);

            for(int i = 0; i < items.size(); i++) {

                temp[0] = i;
                temp[1] = items.get(i);
                cursor.addRow(temp);

            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

            search.setSuggestionsAdapter(new ExampleAdapter(this, cursor, items));
            search.requestFocusFromTouch();
            search.dispatchSetActivated(true);
            search.requestFocus();
            search.callOnClick();
            search.onActionViewExpanded();
            
        }

    }




    public class ExampleAdapter extends CursorAdapter {

        private List<String> items;

        private TextView text;

        public ExampleAdapter(Context context, Cursor cursor, List<String> items) {

            super(context, cursor, false);

            this.items = items;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            text.setText(items.get(cursor.getPosition()));

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.search_item, parent, false);

            text = (TextView) view.findViewById(R.id.search_item);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return view;

        }

    }

}
