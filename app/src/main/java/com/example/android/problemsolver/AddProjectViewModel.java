package com.example.android.problemsolver;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import com.example.android.problemsolver.database.AppDatabase;
import com.example.android.problemsolver.database.ProjectEntry;


public class AddProjectViewModel extends ViewModel {

    private LiveData<ProjectEntry> project;

    public AddProjectViewModel(AppDatabase database, int projectId) {
        project = database.projectDao().loadProjectById(projectId);
    }

    public LiveData<ProjectEntry> getProject() {
        return project;
    }
}
