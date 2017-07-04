package com.wasseemb.applock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;


public class PackageListActivity extends AppCompatActivity {
    private Helper mHelper;

    private ListView mListView = null;
    private ArrayList<ApplicationInfo> mAppList = new ArrayList<ApplicationInfo>();
    private ArrayList<ApplicationInfo> mFilteredAppList = new ArrayList<ApplicationInfo>();

    private MenuItem mSearchItem;
    private AppListAdaptor mAppListAdaptor;
    private String mNameFilter;
    private Set<String> mSet = null;
    private Set<String> nSet = new HashSet<String>();




    static class ViewHolder {
        TextView app_name;
        TextView app_package;
        ImageView app_icon;
        int position;
        ApplicationInfo app_info;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
        //if (Utils.hasActionBar())
         //   getActionBar().setDisplayHomeAsUpEnabled(true);


        mHelper = new Helper(getApplicationContext());
   // if(!mHelper.getSharedPrefBol(SettingsKeys.INTRO_PREFRENCE_KEY))
        //startActivity(new Intent(this,PackageListActivity.class));

        mSet = new HashSet<String>();
        mSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);
        if(!mHelper.isAccessibilitySettingsOn())
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Accessibility is disabled");
            builder.setMessage("Open Settings and enable AppLock accessibility service");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent,0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.show();
        }

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ApplicationInfo app = mFilteredAppList.get(position);

                try {
                    Intent intent = getPackageManager()
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
                   // Toast.makeText(PackageListActivity.this, app.packageName, Toast.LENGTH_SHORT).show();
                  //  Snackbar.make(findViewById(R.id.relativeLayout),app.packageName,Snackbar.LENGTH_SHORT).show();
                    if (null != intent) {
                        //  startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PackageListActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(PackageListActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        new PrepareAppsAdapterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        MenuItemCompat.setOnActionExpandListener(mSearchItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mNameFilter = query;
                        mAppListAdaptor.getFilter().filter(mNameFilter);
                        findViewById(R.id.action_search).clearFocus();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mNameFilter = newText;
                        mAppListAdaptor.getFilter().filter(mNameFilter);
                        return false;
                    }

                });
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mAppListAdaptor.getFilter().filter("");
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onSearchRequested() {
        mSearchItem.expandActionView();
        return super.onSearchRequested();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mListView == null)
            return;

        if (requestCode >= mListView.getFirstVisiblePosition() && requestCode <= mListView.getLastVisiblePosition()) {
            View v = mListView.getChildAt(requestCode - mListView.getFirstVisiblePosition());
            mListView.getAdapter().getView(requestCode, v, mListView);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Handle background loading of apps
    private class PrepareAppsAdapterTask extends AsyncTask<Void,Void,AppListAdaptor> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(((ListView) findViewById(R.id.listView)).getContext());
            dialog.setMessage("Loading…");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected AppListAdaptor doInBackground(Void... params) {
            if (mAppList.size() == 0) {
                loadApps(dialog);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final AppListAdaptor result) {
            mAppListAdaptor = new AppListAdaptor(PackageListActivity.this, mAppList);
            mListView.setAdapter(mAppListAdaptor);

            try {
                dialog.dismiss();
            } catch (Exception e) {

            }
        }
    }

    @SuppressLint("DefaultLocale")
    class AppListAdaptor extends ArrayAdapter<ApplicationInfo> implements SectionIndexer {

        private Map<String, Integer> alphaIndexer;
        private String[] sections;
        private Filter filter;

        @SuppressLint("DefaultLocale")
        public AppListAdaptor(Context context, List<ApplicationInfo> items) {
            super(context, R.layout.app_list_item, new ArrayList<ApplicationInfo>(items));

            mFilteredAppList.addAll(items);
            filter = new AppListFilter(this);
            alphaIndexer = new HashMap<String, Integer>();
            for(int i = mFilteredAppList.size() - 1; i >= 0; i--)
            {
                ApplicationInfo app = mFilteredAppList.get(i);
                String appName = app.name;
                String firstChar;
                if (appName == null || appName.length() < 1) {
                    firstChar = "@";
                } else {
                    firstChar = appName.substring(0, 1).toUpperCase();
                    if(firstChar.charAt(0) > 'Z' || firstChar.charAt(0) < 'A')
                        firstChar = "@";
                }

                alphaIndexer.put(firstChar, i);
            }


            Set<String> sectionLetters = alphaIndexer.keySet();

            // create a list from the set to sort
            List<String> sectionList = new ArrayList<String>(sectionLetters);

            Collections.sort(sectionList);

            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Load or reuse the view for this row
            View row = convertView;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.app_list_item, parent, false);
            }

            ApplicationInfo app = mFilteredAppList.get(position);

            if (row.getTag() == null) {
                ViewHolder holder = new ViewHolder();
                holder.app_icon = (ImageView) row.findViewById(R.id.app_icon);
                holder.app_name = (TextView) row.findViewById(R.id.app_name);
                holder.app_package = (TextView) row.findViewById(R.id.app_package);
                holder.position = position;
                holder.app_info = app;
                row.setTag(holder);
            }
            mHelper = new Helper(getApplicationContext());
            ViewHolder holder = (ViewHolder) row.getTag();

            nSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);
            CheckBox cb = (CheckBox) row.findViewById(R.id.cb);
            if (nSet.contains(app.packageName))
                cb.setChecked(true);
            else
                cb.setChecked(false);

            holder.app_name.setText(app.name == null ? "" : app.name);
            holder.app_package.setTextColor(mHelper.isEnabled(app.packageName, null)
                    ? Color.parseColor("#0099CC") : Color.RED);
            holder.app_package.setText(app.packageName);
            holder.app_icon.setTag(app.packageName);



            new ImageLoader(holder.app_icon, app.packageName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    app);

            return row;
        }

        @Override
        public int getPositionForSection(int section) {
            if (section >= sections.length)
                return mFilteredAppList.size() - 1;

            return alphaIndexer.get(sections[section]);
        }

        @Override
        public int getSectionForPosition(int position) {

            // Iterate over the sections to find the closest index
            // that is not greater than the position
            int closestIndex = 0;
            int latestDelta = Integer.MAX_VALUE;

            for (int i = 0; i < sections.length; i++) {
                int current = alphaIndexer.get(sections[i]);
                if (current == position) {
                    // If position matches an index, return it immediately
                    return i;
                } else if (current < position) {
                    // Check if this is closer than the last index we inspected
                    int delta = position - current;
                    if (delta < latestDelta) {
                        closestIndex = i;
                        latestDelta = delta;
                    }
                }
            }

            return closestIndex;
        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public Filter getFilter() {
            return filter;
        }
    }

    private class AppListFilter extends Filter {

        private AppListAdaptor adaptor;

        AppListFilter(AppListAdaptor adaptor) {
            super();
            this.adaptor = adaptor;
        }

        @SuppressLint("WorldReadableFiles")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.

            ArrayList<ApplicationInfo> items = new ArrayList<ApplicationInfo>();
            synchronized (this) {
                items.addAll(mAppList);
            }

            FilterResults result = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                Pattern regexp = Pattern.compile(constraint.toString(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
                for (Iterator<ApplicationInfo> i = items.iterator(); i.hasNext(); ) {
                    ApplicationInfo app = i.next();
                    if (!regexp.matcher(app.name == null ? "" : app.name).find()
                            && !regexp.matcher(app.packageName).find()) {
                        i.remove();
                    }
                }
            }

            result.values = items;
            result.count = items.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            mFilteredAppList = (ArrayList<ApplicationInfo>) results.values;
            adaptor.clear();
            adaptor.addAll(mFilteredAppList);
            adaptor.notifyDataSetInvalidated();
        }
    }


    @SuppressLint("DefaultLocale")
    private void loadApps(ProgressDialog dialog) {
        mAppList.clear();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(0);
        dialog.setMax(apps.size());
        dialog.setProgressNumberFormat(null);
        int i = 1;
        for (ApplicationInfo appInfo : apps) {
            dialog.setProgress(i++);

            if (appInfo == null)
                continue;

            appInfo.name = appInfo.loadLabel(pm).toString();
            try {
				/* Slower app startup, but fewer apps for the user to go through */
                PackageInfo pkgInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_ACTIVITIES);
                ActivityInfo[] list = pkgInfo.activities;
                if (list != null) {
                    if (null != pm.getLaunchIntentForPackage(appInfo.packageName)) {
                        if(!appInfo.packageName.equals("com.wasseemb.applock"))
                            mAppList.add(appInfo);
                    }
                }
            } catch (Exception e) {
                continue;
            }

        }


        Collections.sort(mAppList, new Comparator<ApplicationInfo>() {
            @SuppressLint("DefaultLocale")
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                if (lhs.name == null) {
                    return -1;
                } else if (rhs.name == null) {
                    return 1;
                } else {
                    return lhs.name.toUpperCase().compareTo(rhs.name.toUpperCase());
                }
            }
        });
    }

    class ImageLoader extends AsyncTask<Object, Void, Drawable> {
        private ImageView imageView;
        private String mPackageName;

        public ImageLoader(ImageView view, String packageName) {
            mPackageName = packageName;
            imageView = view;
        }

        @Override
        protected Drawable doInBackground(Object... params) {
            ApplicationInfo info = (ApplicationInfo) params[0];
            return getPackageManager().getApplicationIcon(info);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if (imageView.getTag().toString().equals(mPackageName)) {
                imageView.setImageDrawable(result);
            }
        }
    }
}