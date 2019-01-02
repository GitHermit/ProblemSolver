package com.example.android.problemsolver;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.problemsolver.database.AppDatabase;
import com.example.android.problemsolver.database.ProjectEntry;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class CompletedActivity extends AppCompatActivity implements ProjectAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_RESULTS = "searchResults";
    private ProjectAdapter mAdapter;
    private List<ProjectEntry> completedProjects;
    private String found;
    @BindView(R.id.completedPublisherAdView) PublisherAdView mPublisherAdView;
    @BindView(R.id.recyclerViewCompletedProjects) RecyclerView mRecyclerView;
    @BindView(R.id.completed_search) EditText mEditText;
    @BindView(R.id.searched_project) TextView mTextView;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        ButterKnife.bind(this);
        Toast.makeText(this,R.string.completed_tutorial,Toast.LENGTH_LONG).show();

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ProjectAdapter(this,  this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        if (savedInstanceState != null && savedInstanceState.containsKey(SEARCH_RESULTS)) {
            found = savedInstanceState.getString(SEARCH_RESULTS);
            mTextView.setText(found);
        }

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
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
                        List<ProjectEntry> projects = mAdapter.getProjects();
                        mDb.projectDao().deleteProject(projects.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }
    private void makeCompletedDataSearchQuery() {
        String projectQuery = mEditText.getText().toString();
        new ProjectQueryTask().execute(projectQuery);
    }

    public class ProjectQueryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String search = params[0];
            String projectSearchResults = null;
            int size = completedProjects.size();
            for (int i = 0; i < size; i++) {
                if (completedProjects.get(i).getName().equals(search)){
                    projectSearchResults = search;
                }
            }

            return projectSearchResults;
        }

        @Override
        protected void onPostExecute(String projectSearchResults) {
            if (projectSearchResults != null && !projectSearchResults.equals("")) {
                found = projectSearchResults + getString(R.string.found);
                mTextView.setText(found);
            }
            else {
                found = getString(R.string.not_found);
                mTextView.setText(R.string.project_status);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.completed, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.app_bar_search) {
            makeCompletedDataSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_RESULTS, found );
        super.onSaveInstanceState(outState);
    }



    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getProjects().observe(this, new Observer<List<ProjectEntry>>() {
            @Override
            public void onChanged(@Nullable List<ProjectEntry> projectEntries) {
                Log.d(TAG, getString(R.string.updating_liveData));
                try {
                    completedProjects = new ArrayList<>();
                    for (int i = 0; i < projectEntries.size(); i++){
                        if (projectEntries.get(i).isCompleted()){
                            completedProjects.add(projectEntries.get(i));
                        }
                    }
                }catch (NullPointerException e) {
                    //maybe add an empty textview message here?
                }
                mAdapter.setProjects(completedProjects);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {

    }
}
