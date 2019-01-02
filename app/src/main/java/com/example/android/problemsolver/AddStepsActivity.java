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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.android.problemsolver.database.AppDatabase;
import com.example.android.problemsolver.database.ProjectEntry;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_LONG;


public class AddStepsActivity extends AppCompatActivity implements ProjectAdapter.ItemClickListener {

    public static final String EXTRA_PROJECT_ID = "extraProjectId";
    public static final String INSTANCE_PROJECT_ID = "instanceProjectId";

    private static final int DEFAULT_PROJECT_ID = -1;

    private static final String TAG = AddStepsActivity.class.getSimpleName();
    private static final String ARRAY_INSTANCE = "array" ;


    StepsAdapter mAdapter;
    List<String> oldSteps;
    List<String> steps;
@BindView(R.id.editTextProjectName) EditText mEditText;
@BindView(R.id.addStepsButton)Button mStepsButton;
@BindView(R.id.stepsListView) RecyclerView mRecyclerView;
@BindView(R.id.saveButton) Button mButton;

    private int mProjectId = DEFAULT_PROJECT_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);
        Toast.makeText(this,R.string.step_tutorial,Toast.LENGTH_LONG).show();


        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_PROJECT_ID)) {
            mProjectId = savedInstanceState.getInt(INSTANCE_PROJECT_ID, DEFAULT_PROJECT_ID);
            oldSteps = savedInstanceState.getStringArrayList(ARRAY_INSTANCE);
            mAdapter = new StepsAdapter(this,oldSteps);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mAdapter);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_PROJECT_ID)) {
            mButton.setText(R.string.update_button);
            if (mProjectId == DEFAULT_PROJECT_ID) {
                mProjectId = intent.getIntExtra(EXTRA_PROJECT_ID, DEFAULT_PROJECT_ID);

                AddProjectViewModelFactory factory = new AddProjectViewModelFactory(mDb, mProjectId);
                final AddProjectViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddProjectViewModel.class);

                viewModel.getProject().observe(this, new Observer<ProjectEntry>() {
                    @Override
                    public void onChanged(@Nullable ProjectEntry projectEntry) {
                        viewModel.getProject().removeObserver(this);
                        populateUI(projectEntry);
                    }
                });
            }
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        String name = mEditText.getText().toString();
                        if (oldSteps.size() > 0) {
                            oldSteps.remove(position);
                            final ProjectEntry projectEntry = new ProjectEntry(name,oldSteps);
                            projectEntry.setId(mProjectId);
                            mDb.projectDao().updateProject(projectEntry);
                            AddStepsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    populateUI(projectEntry);
                                }
                            });
                        }

                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_PROJECT_ID, mProjectId);
        outState.putStringArrayList(ARRAY_INSTANCE, (ArrayList<String>) oldSteps);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        mStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddStepButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param project the projectEntry to populate the UI
     */
    private void populateUI(ProjectEntry project) {
        if (project == null) {
            return;
        }
        mEditText.setText(project.getName());
        setStepsInViews(project.getSteps());
        mAdapter = new StepsAdapter(this,project.getSteps());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setStepsInViews(List<String> steps) {
        oldSteps = steps;
    }



    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String name = mEditText.getText().toString();

        final ProjectEntry project = new ProjectEntry(name,oldSteps);
        populateUI(project);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mProjectId == DEFAULT_PROJECT_ID) {
                    // insert new project
                    mDb.projectDao().insertProject(project);
                } else { 
                    //update project
                    project.setId(mProjectId);
                    mDb.projectDao().updateProject(project);
                }
                finish();
            }
        });
    }



    public void onAddStepButtonClicked() {
        String name = mEditText.getText().toString();
        try {
            oldSteps.isEmpty();
        } catch (NullPointerException e) {
            oldSteps = new ArrayList<>();

        }

        if (mProjectId != DEFAULT_PROJECT_ID) {
                oldSteps.add("");
                final ProjectEntry project = new ProjectEntry(name, oldSteps);
                mAdapter.addStep(oldSteps);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        project.setId(mProjectId);
                        mDb.projectDao().updateProject(project);
                    }
                });
            } else {
                Toast.makeText(this, R.string.toast_update, LENGTH_LONG).show();
        }
    }


    @Override
    public void onItemClickListener(int itemId) {

    }
}
