package com.rsv.galleria;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final Integer numViews = 3;

    private static final Integer[] contactViewIds = { R.id.contact_1, R.id.contact_2, R.id.contact_3 };

    private final Context c = this;

    private static Integer[] cachedIds = new Integer[numViews];

    private static PackageInfo packageInfo;

    private List<PackageInfo> packageList;

    private PackageInfo curResultPackage;
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

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "MainActivity created");
        handleIntent(getIntent());

        TextView tv = (TextView) findViewById(R.id.contact_1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(cachedIds[0]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.contact_2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(cachedIds[1]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.contact_3);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(cachedIds[2]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.app_1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageInfo pi = curResultPackage;
                Log.v(TAG, getPackageManager().getApplicationLabel(pi.applicationInfo).toString());
                Log.v(TAG, pi.packageName);

                Log.v(TAG, "2 pkgName: " + pi.applicationInfo.packageName);
                Log.v(TAG, "2 name: " + pi.applicationInfo.name);
//                ComponentName name = new ComponentName(pi.packageName, pi.name);
//                Intent i = new Intent(Intent.ACTION_MAIN);
//                i.addCategory(Intent.CATEGORY_LAUNCHER);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                i.setComponent(name);
////                Log.v(TAG, "PackageName: " + activity.packageName);
////                Log.v(TAG, "Name: " + activity.name);
                Intent i = getPackageManager().getLaunchIntentForPackage(pi.packageName);
                startActivity(i);
            }
        });


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

        PackageManager packageManager = getPackageManager();
        packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

//        packageList1 = new ArrayList<PackageInfo>();

//        /*To filter out System apps*/
//        for (PackageInfo pi : packageList) {
//            boolean b = isSystemPackage(pi);
//            if (!b) {
//                packageList1.add(pi);
//            }
//        }

        for (PackageInfo pi : packageList) {
            Log.v(TAG, getPackageManager().getApplicationLabel(pi.applicationInfo).toString());
            Log.v(TAG, pi.packageName);
        }

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

                //PopupWindow popupWindow = new PopupWindow(v, 320, 320);
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

    private List<PackageInfo> searchPackages(String query, List<PackageInfo> packages) {
        List<PackageInfo> results = new ArrayList<PackageInfo>();
        int currentMin = Integer.MAX_VALUE;
        for (PackageInfo pi : packages) {
//            int found = getPackageManager().getApplicationLabel(pi.applicationInfo).toString().toLowerCase()
//                    .indexOf(query.toLowerCase());
//            if (found != -1 && found < currentMin) {
//                currentMin = found;
//                if (getPackageManager().getLaunchIntentForPackage(pi.packageName) != null) {
//                    results.add(pi);
//                }
//            }
            if (getPackageManager().getApplicationLabel(pi.applicationInfo).toString().toLowerCase().
                    contains(query.toLowerCase())) {
                if (getPackageManager().getLaunchIntentForPackage(pi.packageName) != null) {
                    results.add(pi);
                }
            }
        }
        return results;
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

//        TextView t = (TextView)findViewById(R.id.text_view);
//        t.setText("Searching for \"" + query + "\"");

////        GridView gridView = (GridView) findViewById(R.id.gridview);
//        ArrayList<Integer> results = new ArrayList<>();
//        for (Image i: images) {
//            if (i.location.toLowerCase().contains(query.toLowerCase())) {
//                results.add(i.id);
//            }
//        }
//        return results.toArray(new Integer[results.size()]);
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
            //Log.v(TAG, "Second clause");
            String whereClause = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            String queryLike = "%" + query + "%";
//            String orderBy = "LOCATE('" + query + "'," + ContactsContract.Contacts.DISPLAY_NAME + ")";
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    whereClause,
                    new String[] { queryLike },
                    null);

            int numRows = cursor.getCount();
            int numContacts = (numRows < numViews) ? numRows : numViews;
//            if (cursor.getCount() > 0) {
//                while (cursor.moveToNext()) {
//                    String id = cursor.getString(
//                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cursor.getString(
//                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    Log.v(TAG, "Contact found: " + name + " with id " + id);
//                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                        //Query phone here.  Covered next
//                        Log.v(TAG, " and they have a phone number!");
//                    }
//                }
//            }
            cursor.moveToNext();


            for (int i = 0; i < numContacts; i++) {
                Integer id = contactViewIds[i];
                TextView tv = (TextView) findViewById(id);

                String contactIdString = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Integer contactId = Integer.parseInt(contactIdString);
                cachedIds[i] = contactId;

                String name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                tv.setText(name);
                tv.setVisibility(View.VISIBLE);
                cursor.moveToNext();
            }

            for (int i = numContacts; i < numViews; i++) {
                TextView tv = (TextView) findViewById(contactViewIds[i]);
                tv.setVisibility(View.GONE);
            }

            /* UPDATE THE TEXT VIEW OF PACKAGES */
            List<PackageInfo> packageResults = searchPackages(query, packageList);
            TextView packageView = (TextView) findViewById(R.id.app_1);
            if (packageResults.size() == 0) {
                packageView.setVisibility(View.GONE);
            } else {
                packageView.setVisibility(View.VISIBLE);
//                PackageInfo pi = packageResults.get(packageResults.size() - 1);
                PackageInfo pi = packageResults.get(0);
                curResultPackage = pi;
                String appLabel = getPackageManager().getApplicationLabel(pi.applicationInfo).toString();
                packageView.setText(appLabel);
                Drawable appIcon = getPackageManager().getApplicationIcon(pi.applicationInfo);
                appIcon.setBounds(0, 0, 40, 40);
                packageView.setCompoundDrawables(appIcon, null, null, null);
                packageView.setCompoundDrawablePadding(15);
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

                    for (int i = 0; i < numViews; i++) {
                        TextView tv = (TextView) findViewById(contactViewIds[i]);
                        tv.setVisibility(View.GONE);
                    }

                    TextView tv = (TextView) findViewById(R.id.app_1);
                    tv.setVisibility(View.GONE);
//                    tv = (TextView) findViewById(R.id.apps_label);
                }
//                else if (newText.equals("popup")) {
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
