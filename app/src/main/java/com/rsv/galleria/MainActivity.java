package com.rsv.galleria;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    // references to our images
//    private Integer[] mThumbIds = {
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7,
//            R.drawable.sample_0, R.drawable.sample_1,
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7
//    };

    // 9 Photos, 3 Ann Arbor, 3 New York, 3 Seattle
    private Integer[] defaultImages = {
        R.drawable.ann_arbor_1,
        R.drawable.seattle_2,
        R.drawable.ann_arbor_3,
        R.drawable.new_york_1,
        R.drawable.ann_arbor_2,
        R.drawable.new_york_2,
        R.drawable.seattle_1,
        R.drawable.new_york_3,
        R.drawable.seattle_3
    };

    private Image[] images = {
            new Image("Ann Arbor", R.drawable.ann_arbor_1),
            new Image("Seattle", R.drawable.seattle_2),
            new Image("Ann Arbor", R.drawable.ann_arbor_3),
            new Image("New York", R.drawable.new_york_1),
            new Image("Ann Arbor", R.drawable.ann_arbor_2),
            new Image("New York", R.drawable.new_york_2),
            new Image("Seattle", R.drawable.seattle_1),
            new Image("New York", R.drawable.new_york_3),
            new Image("Seattle", R.drawable.seattle_3),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "MainActivity created");
        handleIntent(getIntent());

        GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<Integer> initialImages = new ArrayList<>();
        for (Image i: images) {
            initialImages.add(i.id);
        }
        gridview.setAdapter(new ImageAdapter(this, initialImages.toArray(new Integer[initialImages.size()])));

        final Context c = this;
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();

                //PopupWindow popupWindow = new PopupWindow(v, 320, 320);
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
        TextView t = (TextView)findViewById(R.id.text_view);
        t.setText("Searching for \"" + query + "\"");

//        GridView gridView = (GridView) findViewById(R.id.gridview);
        ArrayList<Integer> results = new ArrayList<>();
        for (Image i: images) {
            if (i.location.toLowerCase().contains(query.toLowerCase())) {
                results.add(i.id);
            }
        }
        return results.toArray(new Integer[results.size()]);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView t = (TextView)findViewById(R.id.text_view);
            t.setText("Searching for \"" + query + "\"");

            GridView gridview = (GridView) findViewById(R.id.gridview);
            ArrayList<Integer> results = new ArrayList<>();
            for (Image i: images) {
                if (i.location.toLowerCase().contains(query.toLowerCase())) {
                    results.add(i.id);
                }
            }
            gridview.setAdapter(new ImageAdapter(this, results.toArray(new Integer[results.size()])));
            gridview.invalidateViews();
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
