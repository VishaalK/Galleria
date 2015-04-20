package com.rsv.omnisearch;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Locale;

import static java.lang.Math.abs;

public class MainActivity extends Activity {
    public class LatLongId {
        Integer _ID;
        Double Lat;
        Double Long;
    }

    private static final String TAG = "MainActivity";

    private static final Integer numViews = 3;

    private static final Integer[] contactViewIds = { R.id.contact_1, R.id.contact_2, R.id.contact_3 };

    private static final Integer[] musicViewIds = { R.id.music_1, R.id.music_2, R.id.music_3 };

    private final Context c = this;

    private static Integer[] cachedIds = new Integer[numViews];

    private static Integer[] cachedMusicIds = new Integer[numViews];

    private ArrayList<LatLongId> cachedLatLongIds;

    private ArrayList<Integer> lolresults;

    private Hashtable<String, ArrayList<Integer>> hash;

    private static int buf = 0;

    private static PackageInfo packageInfo;

    private List<PackageInfo> packageList;

    private PackageInfo curResultPackage;
    // 9 Photos, 3 Ann Arbor, 3 New York, 3 Seattle

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }




    //define source of MediaStore.Images.Media, internal or external storage
    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    final Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
    final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;

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


//        for (PackageInfo pi : packageList) {
//            Log.v(TAG, getPackageManager().getApplicationLabel(pi.applicationInfo).toString());
//            Log.v(TAG, pi.packageName);
//        }

        GridView gridview = (GridView) findViewById(R.id.gridview);

//        ArrayList<Uri> fileList = new ArrayList<Uri>();
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

        cachedLatLongIds = new ArrayList<>();
        lolresults = new ArrayList<>();
        for ( int i = 0 ; i < actualimagecursor.getCount() ; i++ )
        {
            actualimagecursor.moveToPosition(i);
            String fileName = actualimagecursor.getString(actual_image_column_index);
//            Uri uripic = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, fileName);
            LatLongId tmp = new LatLongId();
            tmp._ID = actualimagecursor.getInt(actualimagecursor.getColumnIndex(MediaStore.Images.Media._ID));
            tmp.Lat = actualimagecursor.getDouble(actualimagecursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
            tmp.Long = actualimagecursor.getDouble(actualimagecursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));


//            Log.v("GPS", "ID: " + actualimagecursor.getInt(
//                    actualimagecursor.getColumnIndex(MediaStore.Images.Media._ID)));
//            Log.v("GPS", "Lat: " + actualimagecursor.getDouble(
//                    actualimagecursor.getColumnIndex(MediaStore.Images.Media.LATITUDE)));
//            Log.v("GPS", "Long: "+actualimagecursor.getDouble(
//                    actualimagecursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE)));
            if(abs(tmp.Lat) > 0.5 && abs(tmp.Long) > 0.5) {
                cachedLatLongIds.add(tmp);
            }
            lolresults.add(tmp._ID);

//            fileList.add(( uripic));
//            Log.v(TAG, fileName);
        }

        hash = new Hashtable<>();
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        for (LatLongId x: cachedLatLongIds) {
            List<Address> addresses = new ArrayList<>();
            try {
                addresses = gc.getFromLocation(x.Lat, x.Long, 1);
            } catch (IOException e) {
                Log.v(TAG, "Reverse geocoding went wrong");
            }
            for (Address a: addresses) {
                String word = a.getLocality().toLowerCase();

                if(hash.containsKey(word)){
                    hash.get(word).add(x._ID);
                } else{
                    hash.put(word, new ArrayList<Integer>());
                    hash.get(word).add(x._ID);
                }
                Log.v("searchGPS", "adding "+word+" to hash table");
            }
        }


        gridview.setAdapter(new ImageAdapter(this, lolresults.toArray(new Integer[lolresults.size()])));
        gridview.invalidateViews();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("itemclick", "position: " + position);
                getThumbnail(lolresults.get(position));
            }
        });

        Log.v("mainActivity", "done with main activity");
    } //onCreate()

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private Uri getPhotoUriFromID(String id) {
        try {
            Cursor cur = getContentResolver()
                    .query(ContactsContract.Data.CONTENT_URI,
                            null,
                            ContactsContract.Data.CONTACT_ID
                                    + "="
                                    + id
                                    + " AND "
                                    + ContactsContract.Data.MIMETYPE
                                    + "='"
                                    + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                                    + "'", null, null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
        return Uri.withAppendedPath(person,
                ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
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


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Get search string and set query
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(query.endsWith(" ")){
                query = query.substring(0, query.length()-1);
                Log.v("query", "Query is now: " + query + ".");
            }
            query = query.toLowerCase();
            TextView t = (TextView)findViewById(R.id.text_view);
            t.setText("Searching for \"" + query + "\"");

            /* UPDATE THE GRID VIEW */
            String searchQuery = query;
            for(String s : hash.keySet()){
                if(s.startsWith(query)){
                    Log.v("searchQuery", "switched from "+query+" to "+s);
                    searchQuery = s;
                }
            }
            if(hash.containsKey(searchQuery)) {
                final ArrayList<Integer> geocodedResults = hash.get(searchQuery);
                GridView gridview = (GridView) findViewById(R.id.gridview);
                gridview.setAdapter(new ImageAdapter(this, geocodedResults.toArray(new Integer[geocodedResults.size()])));
                gridview.invalidateViews();
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.v("itemclick", "position: " + position);
                        getThumbnail(geocodedResults.get(position));
                    }
                });
            } else{
                GridView gridview = (GridView) findViewById(R.id.gridview);
                gridview.invalidateViews();
                Integer [] tmpArray = new Integer[0];
                gridview.setAdapter(new ImageAdapter(this, tmpArray));
            }


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
                Uri uri = getPhotoUriFromID(contactIdString);
                Drawable yourDrawable;
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    yourDrawable = Drawable.createFromStream(inputStream, uri.toString() );
                } catch (FileNotFoundException e) {
                    yourDrawable = getResources().getDrawable(R.drawable.icon);
                } catch (NullPointerException e) {
                    yourDrawable = getResources().getDrawable(R.drawable.icon);
                }
                yourDrawable.setBounds(0, 0, 40, 40);
                tv.setCompoundDrawables(yourDrawable, null, null, null);
                tv.setCompoundDrawablePadding(15);
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







            /* UPDATE THE TEXT VIEW OF MUSIC */
            String music_where = MediaStore.Audio.Media.TITLE + " LIKE ?";
            String music_query = "%" + query + "%";
            String[] params = new String[] { music_query };

            ContentResolver cr2 = getContentResolver();

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
                if(q.isAfterLast()){
                    numq1 = numTrueMusic;
                    break;
                }
            }

            for (int i = numq1; i < numContactsMusic; i++) {
                if(q2.isAfterLast()){
                    numContactsMusic = numTrueMusic;
                    break;
                }
                Integer id = musicViewIds[i];
                TextView tv2 = (TextView) findViewById(id+buf);

                String musicIdString = q2.getString(q2.getColumnIndex( MediaStore.Audio.Media._ID));
                Integer musicId = Integer.parseInt(musicIdString);
                cachedMusicIds[i] = musicId;

                String name = q2.getString(q2.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                boolean flag = false;
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
//                    GridView gridView = (GridView) findViewById(R.id.gridview);
//                    gridView.setAdapter(new ImageAdapter(c, defaultImages));

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
        thumbCursor.close();
        return thumbBitmap;
    }
}
