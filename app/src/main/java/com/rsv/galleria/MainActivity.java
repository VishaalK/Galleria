package com.rsv.galleria;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final Integer numViews = 3;

    // 9 Photos, 3 Ann Arbor, 3 New York, 3 Seattle
    private Integer[] defaultImages = {
        R.drawable.ann_arbor_1,
        R.drawable.seattle_2,
        R.drawable.ann_arbor_3,
        R.drawable.new_york_1,
        R.drawable.sample_1,
        R.drawable.ann_arbor_2,
        R.drawable.new_york_2,
        R.drawable.seattle_1,
        R.drawable.sample_0,
        R.drawable.new_york_3,
        R.drawable.new_york_5,
        R.drawable.new_york_6,
        R.drawable.seattle_3,
        R.drawable.new_york_4,
        R.drawable.aussie_1,
        R.drawable.aussie_2
    };

    private Image[] images = {
            new Image("Ann Arbor", "April 5 2015", R.drawable.ann_arbor_1),
            new Image("Seattle", "April 5 2015", R.drawable.seattle_2),
            new Image("Ann Arbor", "April 6 2015", R.drawable.ann_arbor_3),
            new Image("New York", "April 6 2015", R.drawable.new_york_1),
            new Image("New York", "May 5 2015", R.drawable.sample_1),
            new Image("Ann Arbor", "May 5 2014", R.drawable.ann_arbor_2),
            new Image("New York", "May 5 2015", R.drawable.new_york_2),
            new Image("Seattle", "", R.drawable.seattle_1),
            new Image("New York", "", R.drawable.sample_0),
            new Image("New York", "", R.drawable.new_york_3),
            new Image("New York", "", R.drawable.new_york_5),
            new Image("New York", "", R.drawable.new_york_6),
            new Image("Seattle", "", R.drawable.seattle_3),
            new Image("New York", "", R.drawable.new_york_4),
            new Image("Australia", "", R.drawable.aussie_1),
            new Image("Australia", "", R.drawable.aussie_2)
    };

    private Integer[] cachedResults = defaultImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "MainActivity created");
        handleIntent(getIntent());

//        ContentResolver cr = getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//        if (cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                String id = cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                Log.v(TAG, "Contact found: " + name + " with id " + id);
//                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    //Query phone here.  Covered next
//                    Log.v(TAG, " and they have a phone number!");
//                }
//            }
//        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<Integer> initialImages = new ArrayList<>();
        for (Image i: images) {
            initialImages.add(i.id);
        }
        gridview.setAdapter(new ImageAdapter(this, initialImages.toArray(new Integer[initialImages.size()])));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private PopupWindow cachedWindow = new PopupWindow();

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View layout = inflater.inflate(R.lay)
//                if (cachedWindow.isShowing()) {
//                    cachedWindow.dismiss();
//                }
//                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View layout = inflater.inflate(R.layout.layout_popup, (ViewGroup) findViewById(R.id.popup_element));
//
////                ImageView imageView = new ImageView(MainActivity.this);
////                imageView.setImageResource(cachedResults[position]);
//                PopupWindow pw = new PopupWindow(layout, 640, 640);
//                ImageView imageView = (ImageView)findViewById(R.id.imageView);
//                imageView.setImageResource(cachedResults[position]);
////                View parentView = findViewById(R.id.parent_view);
//                pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
//                pw.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
//                pw.setOutsideTouchable(true);
////                pw.setFocusable(true);
//                cachedWindow = pw;
            }
        });

//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            Log.v(TAG, "query");
//            TextView t = (TextView)findViewById(R.id.text_view);
//            t.setText(query);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private Integer[] executeSearch(String query) {
        ArrayList<Integer> results = new ArrayList<>();
        for (Image i: images) {
            if (i.location.toLowerCase().contains(query.toLowerCase()) ||
                    i.date.toLowerCase().contains(query.toLowerCase())) {
                results.add(i.id);
            }
        }
        return (Integer[])results.toArray();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Get search string and set query
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView t = (TextView)findViewById(R.id.text_view);
            t.setText("Searching for \"" + query + "\"");

            /* UPDATE THE GRID VIEW */
            GridView gridview = (GridView) findViewById(R.id.gridview);
            ArrayList<Integer> results = new ArrayList<>();
            for (Image i: images) {
                if (i.location.toLowerCase().contains(query.toLowerCase()) ||
                        i.date.toLowerCase().contains(query.toLowerCase())) {
                    results.add(i.id);
                }
            }
            cachedResults = new Integer[results.size()];
            System.arraycopy(results.toArray(new Integer[0]), 0, cachedResults, 0, results.size());
            gridview.setAdapter(new ImageAdapter(this, results.toArray(new Integer[results.size()])));
            gridview.invalidateViews();

            /* UPDATE THE LIST VIEW */
            Log.v(TAG, "Second clause");
            String whereClause = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            String queryLike = "%" + query + "%";
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    whereClause,
                    new String[] { queryLike },
                    null);

            int numRows = cursor.getCount();
            int numContacts = (numRows < numViews) ? numRows : numViews;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.v(TAG, "Contact found: " + name + " with id " + id);
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        //Query phone here.  Covered next
                        Log.v(TAG, " and they have a phone number!");
                    }
                }
            }

            Integer[] contactViewIds = { R.id.contact_1, R.id.contact_2, R.id.contact_3 };
            for (int i = 0; i < numContacts; i++) {
                Integer id = contactViewIds[i];
                TextView tv = (TextView) findViewById(id);
            }
            for (int i = numContacts; i < numViews; i++) {

            }
            //TODO: put the IDs of the views in an array, iterate through the ones
            //
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        final Context c = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    GridView gridView = (GridView) findViewById(R.id.gridview);
                    gridView.setAdapter(new ImageAdapter(c, defaultImages));
                }
// else if (newText.equals("popup")) {
//                    ImageView imageView = new ImageView(c);
//                    imageView.setImageResource(R.drawable.ann_arbor_1);
//                    PopupWindow popupWindow = new PopupWindow(imageView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
