package com.wishlist.wishlist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by jacek on 05/03/16.
 */
public class MainActivity extends Activity {

    private ViewFlipper viewFlipper;
    private float lastX;

    MyCustomAdapter dataAdapter = null;
    MyDiscountsAdapter discountsAdapter = null;

    private Menu menu;

    private ListView listView;
    private ArrayList<Product> products;
    private ArrayList<DiscountedProduct> discounts;

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

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8a9cd9")));
    }


    public void displayProducts(ArrayList<Product> products) {
        this.products = products;
        setAdapter(products);
    }

    public void displayDiscounts(ArrayList<DiscountedProduct> discounts) {
        this.discounts = discounts;
        setDiscountsAdapter(discounts);
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

    private void setDiscountsAdapter(ArrayList<DiscountedProduct> discounts) {
        //create an ArrayAdaptar from the String Array
        this.discounts = discounts;
        discountsAdapter = new MyDiscountsAdapter(this,
                R.layout.product_item, discounts);
        ListView listView = (ListView)findViewById(R.id.listDiscounts);
        // Assign adapter to ListView
        listView.setAdapter(discountsAdapter);
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
        public View getView(final int position, View convertView, ViewGroup parent) {

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
                        DataManager.removeFromWishlist(mainActivity, productsList.get(position));
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


    private class MyDiscountsAdapter extends ArrayAdapter<DiscountedProduct> {

        private ArrayList<DiscountedProduct> productsList;

        public MyDiscountsAdapter(Context context, int textViewResourceId,
                               ArrayList<DiscountedProduct> productsList) {
            super(context, textViewResourceId, productsList);
            this.productsList = new ArrayList<DiscountedProduct>();
            this.productsList.addAll(productsList);
        }

        private class ViewHolder {
            TextView name;
            Button button;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.discount_item, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView);
                holder.button = (Button) convertView.findViewById(R.id.button);
                convertView.setTag(holder);

                final String name = holder.name.getText().toString();
                holder.button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext().getApplicationContext(),
                                "DISCOUNT!",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DiscountedProduct product = productsList.get(position);
            holder.name.setText(product.getName());
            holder.button.setText(String.valueOf((int)(product.getRate() * 100)) + "%");
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
                    menu.findItem(R.id.search).setVisible(true);
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
                    menu.findItem(R.id.search).setVisible(false);
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


    public void loadHistory(ArrayList<Product> items) {

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
            /*search.requestFocusFromTouch();
            search.dispatchSetActivated(true);
            search.requestFocus();
            search.callOnClick();*/
            search.onActionViewExpanded();

        }

    }




    public class ExampleAdapter extends CursorAdapter {

        private List<Product> items;

        private TextView text;

        public ExampleAdapter(Context context, Cursor cursor, List<Product> items) {

            super(context, cursor, false);

            this.items = items;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            text.setText(items.get(cursor.getPosition()).getName());

        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.search_item, parent, false);

            text = (TextView) view.findViewById(R.id.search_item);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchItemClicked(items.get(cursor.getPosition()));
                }
            });

            return view;

        }

        private void searchItemClicked(final Product product) {
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();

            alertDialog.setTitle("TITLE");

            alertDialog.setMessage("Would you like to add this to your wishlist?\n " + product.getName());

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    /*Toast.makeText(getApplicationContext(),
                            "cancel",
                            Toast.LENGTH_SHORT).show();*/
                    // DO NOTHING
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "More", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    /*Toast.makeText(getApplicationContext(),
                            "url: "+url,
                            Toast.LENGTH_SHORT).show();*/

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(product.getUrl()));
                    startActivity(i);

                }});

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "  OK  ", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(getApplicationContext(),
                            "ok",
                            Toast.LENGTH_SHORT).show();
                    //TODO : ADD TO WISHLIST
                    getStartDate(product);
                }});
            alertDialog.show();
        }

    }

    /// DATE pickers

    public void getStartDate(final Product product) {

        int year = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
        int month = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH);;
        final int day = GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH);;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("startDate", year + " - " + monthOfYear + " - " + dayOfMonth);
                String startDate = year + "-" + monthOfYear + "-" + day;
                getEndDate(product, startDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void getEndDate(final Product product, final String startDate) {

        int year = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
        int month = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH);;
        final int day = GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH);;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("endDate", year + " - " + monthOfYear + " - " + dayOfMonth);
                String endDate = year + "-" + monthOfYear + "-" + day;
                getProbability(product, startDate, endDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void getProbability(final Product product, final String startDate, final String endDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        builder.setTitle("TITLE");

        builder.setMessage("How likely are you to buy this item?");

        View content = inflater.inflate(R.layout.probability, null);

        final NumberPicker np = (NumberPicker)content.findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setMaxValue(100);
        np.setValue(90);

        builder.setView(content).

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                    /*Toast.makeText(getApplicationContext(),
                            "cancel",
                            Toast.LENGTH_SHORT).show();*/
                // DO NOTHING
            }
        }).

        setPositiveButton("  OK  ", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),
                        "ok",
                        Toast.LENGTH_SHORT).show();
                float probability = (float)np.getValue();
                DataManager.addToWishlist(mainActivity, product.getId(), product.getName(), product.getUrl(), startDate, endDate, probability);
            }});

        builder.create().show();
    }

}
