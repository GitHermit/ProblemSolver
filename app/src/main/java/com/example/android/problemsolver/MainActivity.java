

package com.example.android.problemsolver;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.problemsolver.database.AppDatabase;
import com.example.android.problemsolver.database.ProjectEntry;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


public class MainActivity extends AppCompatActivity implements ProjectAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProjectAdapter mAdapter;
    private List<ProjectEntry> currentProjects;
    @BindView(R.id.recyclerViewProjects) RecyclerView mRecyclerView;
    @BindView(R.id.publisherAdView) PublisherAdView mPublisherAdView;


    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-9375865110704984~1654049876");
        ButterKnife.bind(this);
        Toast.makeText(this,getString(R.string.welcome_toast),Toast.LENGTH_LONG).show();



        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ProjectAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

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
                        projects.get(position).setCompleted(true);
                        mDb.projectDao().completeProject(projects.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);


        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProjectIntent = new Intent(MainActivity.this, AddStepsActivity.class);
                startActivity(addProjectIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
         if (menuItemThatWasSelected == R.id.completed_projects) {
            Intent completedIntent = new Intent(MainActivity.this, CompletedActivity.class);
            startActivity(completedIntent);
        }
        return true;
    }


    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getProjects().observe(this, new Observer<List<ProjectEntry>>() {

            @Override
            public void onChanged(@Nullable List<ProjectEntry> projectEntries) {
                Log.d(TAG, getString(R.string.updating_liveData));
                int size;
                try {
                    currentProjects = new ArrayList<>();
                    size = projectEntries.size();
                }catch (NullPointerException e) {
                    size = 0;
                    //maybe add empty textview
                }
                    for (int i = 0; i < size; i++){
                        if(!projectEntries.get(i).isCompleted()){
                            currentProjects.add(projectEntries.get(i));
                        }
                    }

                mAdapter.setProjects(currentProjects);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddStepsActivity.class);
        intent.putExtra(AddStepsActivity.EXTRA_PROJECT_ID, itemId);
        startActivity(intent);
    }
}
