/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.problemsolver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.problemsolver.database.ProjectEntry;

import java.util.List;


public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {


    final private ItemClickListener mItemClickListener;
    private List<ProjectEntry> mProjectEntries;
    private Context mContext;

    /**
     * Constructor for the ProjectAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public ProjectAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new ProjectViewHolder that holds the view for each task
     */
    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the project_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.project_layout, parent, false);

        return new ProjectViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        ProjectEntry projectEntry = mProjectEntries.get(position);
        String name = projectEntry.getName();
        List<String> projectSteps = projectEntry.getSteps();

        holder.projectNameView.setText(name);
        String step = "";
        try {
            for (int i = 0; i < projectSteps.size(); i++) {

                step = step + "Step " + (i+1) + ": " + projectSteps.get(i) + "\n";
            }
//        }
        } catch (NullPointerException e) {
        }
        holder.steps.setText(step);
    }



    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mProjectEntries == null) {
            return 0;
        }
        return mProjectEntries.size();
    }

    public List<ProjectEntry> getProjects() {
        return mProjectEntries;
    }


    /**
     * When data changes, this method updates the list of projectEntries
     * and notifies the adapter to use the new values on it
     */
    public void setProjects(List<ProjectEntry> projectEntries) {
        mProjectEntries = projectEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView projectNameView;
        TextView steps;

        /**
         * Constructor for the ProjectViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ProjectViewHolder(View itemView) {
            super(itemView);

            projectNameView = itemView.findViewById(R.id.projectName);
            steps = itemView.findViewById(R.id.steps);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mProjectEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}