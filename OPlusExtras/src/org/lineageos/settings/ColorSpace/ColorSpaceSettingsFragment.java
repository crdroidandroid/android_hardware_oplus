/*
 * Copyright (C) 2020 The LineageOS Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.colorspace;

import android.annotation.Nullable;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.settingslib.applications.ApplicationsState;

import org.lineageos.settings.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorSpaceSettingsFragment extends PreferenceFragment
    implements ApplicationsState.Callbacks {

    private AllPackagesAdapter mAllPackagesAdapter;
    private ApplicationsState mApplicationsState;
    private ApplicationsState.Session mSession;
    private ActivityFilter mActivityFilter;
    private Map<String, ApplicationsState.AppEntry> mEntryMap =
            new HashMap<String, ApplicationsState.AppEntry>();

    private ColorSpaceUtils mColorSpaceUtils;
    private RecyclerView mAppsRecyclerView;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        mSession = mApplicationsState.newSession(this);
        mSession.onResume();
        mActivityFilter = new ActivityFilter(getActivity().getPackageManager());

        mAllPackagesAdapter = new AllPackagesAdapter(getActivity());

        mColorSpaceUtils = new ColorSpaceUtils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.colorspace_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppsRecyclerView = view.findViewById(R.id.colorspace_rv_view);
        mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAppsRecyclerView.setAdapter(mAllPackagesAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getResources().getString(R.string.colorspace_title));
        rebuild();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSession.onPause();
        mSession.onDestroy();
    }

    @Override
    public void onPackageListChanged() {
        mActivityFilter.updateLauncherInfoList();
        rebuild();
    }

    @Override
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> entries) {
        if (entries != null) {
            handleAppEntries(entries);
            mAllPackagesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadEntriesCompleted() {
        rebuild();
    }

    @Override
    public void onAllSizesComputed() {
    }

    @Override
    public void onLauncherInfoChanged() {
    }

    @Override
    public void onPackageIconChanged() {
    }

    @Override
    public void onPackageSizeChanged(String packageName) {
    }

    @Override
    public void onRunningStateChanged(boolean running) {
    }

    private void handleAppEntries(List<ApplicationsState.AppEntry> entries) {
        final ArrayList<String> sections = new ArrayList<String>();
        final ArrayList<Integer> positions = new ArrayList<Integer>();
        final PackageManager pm = getActivity().getPackageManager();
        String lastSectionIndex = null;
        int offset = 0;

        for (int i = 0; i < entries.size(); i++) {
            final ApplicationInfo info = entries.get(i).info;
            final String label = (String) info.loadLabel(pm);
            final String sectionIndex;

            if (!info.enabled) {
                sectionIndex = "--"; // XXX
            } else if (TextUtils.isEmpty(label)) {
                sectionIndex = "";
            } else {
                sectionIndex = label.substring(0, 1).toUpperCase();
            }

            if (lastSectionIndex == null ||
                    !TextUtils.equals(sectionIndex, lastSectionIndex)) {
                sections.add(sectionIndex);
                positions.add(offset);
                lastSectionIndex = sectionIndex;
            }

            offset++;
        }

        mAllPackagesAdapter.setEntries(entries, sections, positions);
        mEntryMap.clear();
        for (ApplicationsState.AppEntry e : entries) {
            mEntryMap.put(e.info.packageName, e);
        }
    }

    private void rebuild() {
        mSession.rebuild(mActivityFilter, ApplicationsState.ALPHA_COMPARATOR);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private Spinner mode;
        private ImageView icon;
        private View rootView;

        private ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.app_name);
            this.mode = view.findViewById(R.id.app_mode);
            this.icon = view.findViewById(R.id.app_icon);
            this.rootView = view;

            view.setTag(this);
        }
    }

    private class ModeAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private final int[] items = {
                R.string.colorspace_default,
                R.string.colorspace_vivid,
                R.string.colorspace_natural,
                R.string.colorspace_cinematic,
                R.string.colorspace_brilliant,
        };

        private ModeAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView != null) {
                view = (TextView) convertView;
            } else {
                view = (TextView) inflater.inflate(android.R.layout.simple_spinner_dropdown_item,
                        parent, false);
            }

            view.setText(items[position]);
            view.setTextSize(14f);

            return view;
        }
    }

        private class AllPackagesAdapter extends RecyclerView.Adapter<ViewHolder>
                implements AdapterView.OnItemSelectedListener, SectionIndexer {

        private List<ApplicationsState.AppEntry> mEntries = new ArrayList<>();
        private String[] mSections;
        private int[] mPositions;

        public AllPackagesAdapter(Context context) {
            mActivityFilter = new ActivityFilter(context.getPackageManager());
        }

        @Override
        public int getItemCount() {
            return mEntries.size();
        }

        @Override
        public long getItemId(int position) {
            return mEntries.get(position).id;
        }

        @NonNull
        @Override
         public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.colorspace_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Context context = holder.itemView.getContext();

            ApplicationsState.AppEntry entry = mEntries.get(position);

            if (entry == null) {
                return;
            }
            holder.mode.setAdapter(new ModeAdapter(context));
            holder.mode.setOnItemSelectedListener(this);
            holder.title.setText(entry.label);
            holder.title.setOnClickListener(v -> holder.mode.performClick());
            mApplicationsState.ensureIcon(entry);
            holder.icon.setImageDrawable(entry.icon);
            int packageState = mColorSpaceUtils.getStateForPackage(entry.info.packageName);
            holder.mode.setSelection(packageState, false);
            holder.mode.setTag(entry);
        }

        private void setEntries(List<ApplicationsState.AppEntry> entries,
                List<String> sections, List<Integer> positions) {
            mEntries = entries;
            mSections = sections.toArray(new String[sections.size()]);
            mPositions = new int[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                mPositions[i] = positions.get(i);
            }
            notifyDataSetChanged();
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final ApplicationsState.AppEntry entry = (ApplicationsState.AppEntry) parent.getTag();

            int currentState = mColorSpaceUtils.getStateForPackage(entry.info.packageName);
            if (currentState != position) {
                mColorSpaceUtils.writePackage(entry.info.packageName, position);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        @Override
        public int getPositionForSection(int section) {
            if (section < 0 || section >= mSections.length) {
                return -1;
            }

            return mPositions[section];
        }

        @Override
        public int getSectionForPosition(int position) {
            if (position < 0 || position >= getItemCount()) {
                return -1;
            }

            final int index = Arrays.binarySearch(mPositions, position);

            /*
             * Consider this example: section positions are 0, 3, 5; the supplied
             * position is 4. The section corresponding to position 4 starts at
             * position 3, so the expected return value is 1. Binary search will not
             * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
             * To get from that number to the expected value of 1 we need to negate
             * and subtract 2.
             */
            return index >= 0 ? index : -index - 2;
        }

        @Override
        public Object[] getSections() {
            return mSections;
        }
    }

    private class ActivityFilter implements ApplicationsState.AppFilter {

        private final PackageManager mPackageManager;
        private final List<String> mLauncherResolveInfoList = new ArrayList<String>();

        private ActivityFilter(PackageManager packageManager) {
            this.mPackageManager = packageManager;

            updateLauncherInfoList();
        }

        public void updateLauncherInfoList() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(i, 0);

            synchronized (mLauncherResolveInfoList) {
                mLauncherResolveInfoList.clear();
                for (ResolveInfo ri : resolveInfoList) {
                    mLauncherResolveInfoList.add(ri.activityInfo.packageName);
                }
            }
        }

        @Override
        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationsState.AppEntry entry) {
            boolean show = !mAllPackagesAdapter.mEntries.contains(entry.info.packageName);
            if (show) {
                synchronized (mLauncherResolveInfoList) {
                    show = mLauncherResolveInfoList.contains(entry.info.packageName);
                }
            }
            return show;
        }
    }
}
