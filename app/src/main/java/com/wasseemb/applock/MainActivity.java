package com.wasseemb.applock;
//This one is not used be careful
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


        private ListView listView = null;
        private PackageManager packageManager = null;
        private List<ApplicationInfo> applist = null;
        private ApplicationAdapter listadaptor = null;
        private Set<String> mSet = null;
        private Helper mHelper = null;
    private SearchView searchView;
    private MenuItem searchMenuItem;


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            packageManager = getPackageManager();
            listView = (ListView) findViewById(R.id.list);
            new LoadApplications().execute();
            mHelper = new Helper(getApplicationContext());
            mSet = new HashSet<String>();
            mSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ApplicationInfo app = applist.get(position);
                    try {
                        Intent intent = packageManager
                                .getLaunchIntentForPackage(app.packageName);
                        CheckBox cb = (CheckBox)view.findViewById(R.id.cb);

                        cb.setChecked(!cb.isChecked());
                        if(cb.isChecked())
                        {
                            mHelper.addToHashSet(mSet,app.packageName,SettingsKeys.LOCKED_APPS);
                        }
                        else
                        {
                                mHelper.removeFromHashSet(mSet,app.packageName,SettingsKeys.LOCKED_APPS);
                        }

                        if (null != intent) {
                          //  startActivity(intent);
                        }
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            SearchManager searchManager = (SearchManager)
                    getSystemService(Context.SEARCH_SERVICE);
            searchMenuItem = menu.findItem(R.id.search);
            searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);

            return true;

        }
//
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
//                case R.id.set_pin: {
//                    Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
//                    intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
//                    startActivityForResult(intent, 11);
//                    break;
//                }

                case R.id.action_settings: {
                    Intent mIntent = new Intent(MainActivity.this,SettingsActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);

                }
                default: {
                    result = super.onOptionsItemSelected(item);

                    break;
                }
            }

            return result;
        }





        private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();


            for (ApplicationInfo info : list) {
                try {
                    if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                             applist.add(info);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(applist, new ApplicationInfo.DisplayNameComparator(packageManager));
            return applist;
        }

        private class LoadApplications extends AsyncTask<Void, Void, Void> {
            private ProgressDialog progress = null;

            @Override
            protected Void doInBackground(Void... params) {
                applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
                listadaptor = new ApplicationAdapter(MainActivity.this,
                        R.layout.snippet_list_row, applist);

                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected void onPostExecute(Void result) {
                listView.setAdapter(listadaptor);

                progress.dismiss();
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(MainActivity.this, null,
                        "Loading application info...");
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }
        }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        listadaptor.getFilter().filter(newText);
        return false;
    }


}




