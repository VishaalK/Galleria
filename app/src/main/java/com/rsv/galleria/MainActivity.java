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
import android.media.ExifInterface;
import android.media.MediaPlayer;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

//    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private static final Integer numViews = 3;

    private static final Integer[] contactViewIds = { R.id.contact_1, R.id.contact_2, R.id.contact_3 };

    private static final Integer[] musicViewIds = { R.id.music_1, R.id.music_2, R.id.music_3 };

    private final Context c = this;

    private static Integer[] cachedIds = new Integer[numViews];

    private static Integer[] cachedMusicIds = new Integer[numViews];

    private static int buf = 0;

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
//        R.drawable.home1,
//        R.drawable.home2,
//        R.drawable.home3,
//        R.drawable.home4,
//        R.drawable.food1
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
            /*new Image("Ann Arbor", "April 15 2015", R.drawable.home1),
            new Image("Ann Arbor", "April 15 2015", R.drawable.home2),
            new Image("Ann Arbor", "April 15 2015", R.drawable.home3),
            new Image("Ann Arbor", "April 15 2015", R.drawable.home4)*/
    };

    private Integer[] cachedResults = defaultImages;

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }




    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    //define source of MediaStore.Images.Media, internal or external storage
    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    final Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
    final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;

    //SimpleCursorAdapter mySimpleCursorAdapter;
    MyAdapter mySimpleCursorAdapter;

    GridView myGridView;

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
//                Log.v(TAG, getPackageManager().getApplicationLabel(pi.applicationInfo).toString());
//                Log.v(TAG, pi.packageName);
//
//                Log.v(TAG, "2 pkgName: " + pi.applicationInfo.packageName);
//                Log.v(TAG, "2 name: " + pi.applicationInfo.name);
                Intent i = getPackageManager().getLaunchIntentForPackage(pi.packageName);
                startActivity(i);
            }
        });

        tv = (TextView) findViewById(R.id.music_1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(cachedMusicIds[0]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.music_2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(cachedMusicIds[1]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.music_3);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(cachedMusicIds[2]));
                intent.setData(uri);
                c.startActivity(intent);
            }
        });


        PackageManager packageManager = getPackageManager();
        packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);


        for (PackageInfo pi : packageList) {
//            Log.v(TAG, getPackageManager().getApplicationLabel(pi.applicationInfo).toString());
//            Log.v(TAG, pi.packageName);
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
            }
        });


        ArrayList<Uri> fileList = new ArrayList<Uri>();
        String[] proj = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        ContentResolver crap = getContentResolver();
        Cursor actualimagecursor = crap.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                null, null, MediaStore.Images.Media.DATE_ADDED);

        String[] projection = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE
        };
        String selection = "latitude IS NOT NULL AND longitude IS NOT NULL";
        String[] selectionArgs = null;
        actualimagecursor = crap.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);

        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        for ( int i = 0 ; i < actualimagecursor.getCount() ; i++ )
        {
            actualimagecursor.moveToPosition(i);
            String fileName = actualimagecursor.getString(actual_image_column_index);
            Uri uripic = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, fileName);

            Log.v("GPS", "ID: "+actualimagecursor.getInt(
                    actualimagecursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.v("GPS", "Long: "+actualimagecursor.getDouble(
                    actualimagecursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
            Log.v("GPS", "Lat: "+actualimagecursor.getDouble(
                    actualimagecursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));


            fileList.add(( uripic));
            Log.v(TAG, fileName);
        }



        gridview.setAdapter(new ImageAdapter(this, initialImages.toArray(new Integer[initialImages.size()])));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private PopupWindow cachedWindow = new PopupWindow();

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });


        String[] from = {MediaStore.MediaColumns.TITLE};
        int[] to = {android.R.id.text1};

        CursorLoader cursorLoader = new CursorLoader( this, sourceUri, null, null, null,
                MediaStore.Audio.Media.TITLE);

        Cursor cursor = cursorLoader.loadInBackground();

        mySimpleCursorAdapter = new MyAdapter( this, android.R.layout.simple_list_item_1, cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        gridview.setAdapter(mySimpleCursorAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mySimpleCursorAdapter.getCursor();
                cursor.moveToPosition(position);

                int int_ID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                getThumbnail(int_ID);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private List<PackageInfo> searchPackages(String query, List<PackageInfo> packages) {
        List<PackageInfo> results = new ArrayList<PackageInfo>();
        int currentMin = Integer.MAX_VALUE;
        for (PackageInfo pi : packages) {

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
        for (Image i : images) {
            if (i.location.toLowerCase().contains(query.toLowerCase()) ||
                    i.date.toLowerCase().contains(query.toLowerCase())) {
                results.add(i.id);
            }
        }
        return (Integer[]) results.toArray();
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

            cursor.moveToNext();
            int numTrueContacts = 0;
            String[] myIntArray = {"|", "|", "|"};
            for (int i = 0; i < numContacts; i++) {
                Integer id = contactViewIds[i];
                TextView tv = (TextView) findViewById(id);

                String contactIdString = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Integer contactId = Integer.parseInt(contactIdString);
                cachedIds[i] = contactId;

                String name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                boolean flag = false;
                for(int j=0; j<i; j++){
                    if(name.equalsIgnoreCase(myIntArray[j])){
                        flag = true;
                    }
                }
                if(flag == false) {
                    tv.setText(name);
                    tv.setVisibility(View.VISIBLE);
                    myIntArray[i] = name;
                    numTrueContacts++;
                } else if(flag == true){
                    Log.v("contacts", name + " already appears");
                    i--; //random backend flaw: Mitigates multiple ppl of same name, ie "David Lee"
                }
                cursor.moveToNext();
                if(cursor.isAfterLast()){
                    numContacts = numTrueContacts;
                    break;
                }
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








//            String[] projection = { BaseColumns._ID,
//                    MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Media.TITLE };

            String music_where = MediaStore.Audio.Media.TITLE + " LIKE ?";
            String music_query = "%" + query + "%";
            String[] params = new String[] { music_query };

            ContentResolver cr2 = getContentResolver();
//            Cursor q = cr2.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    projection, music_where, params, MediaStore.Audio.Media.TITLE);
//
//            while (q.moveToNext()) {
//                Log.v("song", q.getString(1) + " " + q.getString(2));
//            }
//            q.close();

            Cursor q = cr2.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    //projection, music_where, params, MediaStore.Audio.Media.TITLE);
                    null, music_where, params, MediaStore.Audio.Media.TITLE);

            ContentResolver cr3 = getContentResolver();
            String music_where2 = MediaStore.Audio.Media.ARTIST + " LIKE ?";
            Cursor q2 = cr3.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, music_where2, params, MediaStore.Audio.Media.ARTIST);

            int numRowsMusic = q.getCount();
            int numContactsMusic = (numRowsMusic < numViews) ? numRowsMusic : numViews;
            q.moveToNext();

            int numq1 = numContactsMusic;
            int numq2 = q2.getCount();
            if(numq1 >= numViews){
                ;
            } else{
                if(numq1 + numq2 >= numViews){
                    numContactsMusic = numViews;
                    numq2 = numContactsMusic - numq1;
                } else{
                    numContactsMusic = numq1 + numq2;
                    //numq2 remains the same
                }
            }
            q2.moveToNext();
            Log.v("numMusic", "titles: " + numq1 + "; artists: " + numq2);

            int numTrueMusic = 0;
            String[] myMusArray = {"|", "|", "|"};

            for (int i = 0; i < numq1; i++) {
                Integer id = musicViewIds[i];
                TextView tv2 = (TextView) findViewById(id+buf);

                String musicIdString = q.getString(q.getColumnIndex( MediaStore.Audio.Media._ID));
                Integer musicId = Integer.parseInt(musicIdString);
                cachedMusicIds[i] = musicId;

                String name = q.getString(q.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                boolean flag = false;
//                Log.v("sanity", "we got here");
                for(int j=0; j<i; j++){
                    if(name.equalsIgnoreCase(myMusArray[j])){
                        flag = true;
                        break;
                    }
                }
                if(flag == false) {
                    tv2.setText(name);
                    tv2.setVisibility(View.VISIBLE);
                    myMusArray[i] = name;
                    numTrueMusic++;
                } else if(flag == true){
                    Log.v("music", name + " already appears!");
                    i--;
                }
                q.moveToNext();
//                Log.v("sanity", "sanity2");
                if(q.isAfterLast()){
                    numq1 = numTrueMusic;
                    break;
                }
//                Log.v("sanity", "sanity3");
            }

            for (int i = numq1; i < numContactsMusic; i++) {
//                Log.v("sanity2", "sanity4");
                if(q2.isAfterLast()){
                    numContactsMusic = numTrueMusic;
                    break;
                }
//                Log.v("sanity2", "sanity5");
                Integer id = musicViewIds[i];
                TextView tv2 = (TextView) findViewById(id+buf);

                String musicIdString = q2.getString(q2.getColumnIndex( MediaStore.Audio.Media._ID));
                Integer musicId = Integer.parseInt(musicIdString);
                cachedMusicIds[i] = musicId;

                String name = q2.getString(q2.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                boolean flag = false;
//                Log.v("sanity2", "sanity6");
                for(int j=0; j<i; j++){
                    if(name.equalsIgnoreCase(myMusArray[j])){
                        flag = true;
                    }
                }
                if(flag == false) {
                    tv2.setText(name);
                    tv2.setVisibility(View.VISIBLE);
                    myMusArray[i] = name;
                    numTrueMusic++;
                } else if(flag == true){
                    Log.v("music2", name + " already appears!");
                    i--;
                }
                q2.moveToNext();
            }

            for (int i = numContactsMusic; i < numViews; i++) {
                TextView tv2 = (TextView) findViewById(musicViewIds[i]+buf);
                tv2.setVisibility(View.GONE);
            }






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

                    for (int i = 0; i < numViews; i++) {
                        TextView tv = (TextView) findViewById(musicViewIds[i]);
                        tv.setVisibility(View.GONE);
                    }

                    TextView tv = (TextView) findViewById(R.id.app_1);
                    tv.setVisibility(View.GONE);
//                    tv = (TextView) findViewById(R.id.apps_label);
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












    public class MyAdapter extends SimpleCursorAdapter {

        Cursor myCursor;
        Context myContext;

        public MyAdapter(Context context, int layout, Cursor c, String[] from,
                         int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            myCursor = c;
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.row, parent, false);
            }

            ImageView thumbV = (ImageView) row.findViewById(R.id.thumb);

            myCursor.moveToPosition(position);

            int myID = myCursor.getInt(myCursor.getColumnIndex(MediaStore.Images.Media._ID));

            String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};
            CursorLoader thumbCursorLoader = new CursorLoader(
                    myContext,
                    thumbUri,
                    thumbColumns,
                    thumb_IMAGE_ID + "=" + myID,
                    null,
                    null);
            Cursor thumbCursor = thumbCursorLoader.loadInBackground();

            Bitmap myBitmap = null;
            if (thumbCursor.moveToFirst()) {
                int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);
                String thumbPath = thumbCursor.getString(thCulumnIndex);
                myBitmap = BitmapFactory.decodeFile(thumbPath);
                thumbV.setImageBitmap(myBitmap);
            }

            return row;
        }
    }

    private Bitmap getThumbnail(int id){

        String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};

        CursorLoader thumbCursorLoader = new CursorLoader(
                this,
                thumbUri,
                thumbColumns,
                thumb_IMAGE_ID + "=" + id,
                null,
                null);

        Cursor thumbCursor = thumbCursorLoader.loadInBackground();

        Bitmap thumbBitmap = null;
        if(thumbCursor.moveToFirst()){
            int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);

            String thumbPath = thumbCursor.getString(thCulumnIndex);

            Toast.makeText(getApplicationContext(),
                    thumbPath,
                    Toast.LENGTH_LONG).show();

            thumbBitmap = BitmapFactory.decodeFile(thumbPath);

            //Create a Dialog to display the thumbnail
            AlertDialog.Builder thumbDialog = new AlertDialog.Builder(MainActivity.this);
            ImageView thumbView = new ImageView(MainActivity.this);
            thumbView.setImageBitmap(thumbBitmap);
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(thumbView);
            thumbDialog.setView(layout);
            thumbDialog.show();
        }else{
            Toast.makeText(getApplicationContext(),
                    "NO Thumbnail!",
                    Toast.LENGTH_LONG).show();
        }

        return thumbBitmap;
    }
}
