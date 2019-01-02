package com.example.android.problemsolver;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.problemsolver.database.AppDatabase;
import com.example.android.problemsolver.database.ProjectEntry;


import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<ProjectEntry>> projects;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the projects from the DataBase");
        projects = database.projectDao().loadAllProjects();
    }

    public LiveData<List<ProjectEntry>> getProjects() {
        return projects;
    }
}
