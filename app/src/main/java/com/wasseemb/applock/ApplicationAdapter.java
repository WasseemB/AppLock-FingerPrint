package com.wasseemb.applock;

/**
 * Created by Wasseem on 10/05/2016.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo>  {
    private List<ApplicationInfo> appsList = null;
    private List <ApplicationInfo> mListAppInfoFiltered = null;
    private Context context;
    private PackageManager packageManager;
    private Helper mHelper = null;
    private Set<String> mSet = new HashSet<String>();
    private ArrayList<ApplicationInfo> filteredList;
    AppFilter mFilter = null;



    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
        mHelper = new Helper(context);
        mSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);

    }

    @Override
    public int getCount() {
        return ((null != filteredList) ? filteredList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != filteredList) ? filteredList.get(position) : null);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Filter getFilter() {
        if(mFilter==null)
            mFilter= new AppFilter();
    return mFilter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mSet = mHelper.getHashSet(SettingsKeys.LOCKED_APPS);

        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        ApplicationInfo applicationInfo = filteredList.get(position);
        if (null != applicationInfo) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);


            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            if (mSet.contains(applicationInfo.packageName))
                cb.setChecked(true);
            else
                cb.setChecked(false);

                appName.setText(applicationInfo.loadLabel(packageManager));
                //packageName.setText(applicationInfo.packageName);
                iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));

        }
        return view;
    }


    private class AppFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<ApplicationInfo> tempList = new ArrayList<ApplicationInfo>();

                // search content in friend list
                for (ApplicationInfo user : appsList) {
                    if (user.loadLabel(packageManager).toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = appsList.size();
                filterResults.values = appsList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<ApplicationInfo>) results.values;
            notifyDataSetChanged();

        }
    }
}