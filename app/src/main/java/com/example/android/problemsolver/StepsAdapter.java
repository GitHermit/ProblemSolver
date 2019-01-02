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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;


/**
 * This ProjectAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private List<String> mSteps;
    private String step;
    private Context mContext;

    /**
     * Constructor for the ProjectAdapter that initializes the Context.
     *
     * @param context  the current Context
     */
    public StepsAdapter(Context context, List<String> steps) {
        mContext = context;
        mSteps = steps;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new ProjectViewHolder that holds the view for each task
     */
    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.step_layout, parent, false);

        return new StepViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(StepViewHolder holder, final int position) {

        int stepPosition = position + 1;
        String stepNumber = "Step " + stepPosition  + ": ";
        holder.stepView.setText(stepNumber);
        holder.editStepView.setText(mSteps.get(position));
        holder.editStepView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String updatedStep = s.toString();
                mSteps.set(position, updatedStep);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    @Override
    public int getItemCount() {
        if (mSteps == null) {
            return 0;
        }
        return mSteps.size();
    }

    public List<String> getSteps() {
        return mSteps;
    }


    public void addStep(List<String> updateSteps) {
        mSteps = updateSteps;
        notifyDataSetChanged();
    }


    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView stepView;
        EditText editStepView;


        public StepViewHolder(View itemView) {
            super(itemView);

            stepView = itemView.findViewById(R.id.stepTextView);
            editStepView = itemView.findViewById(R.id.stepEditText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = getAdapterPosition();
//            mItemClickListener.onItemClickListener(elementId);
        }
    }
}