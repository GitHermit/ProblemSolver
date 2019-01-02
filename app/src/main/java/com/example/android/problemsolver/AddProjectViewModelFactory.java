package com.example.android.problemsolver;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.problemsolver.database.AppDatabase;



public class AddProjectViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mProjectId;

    public AddProjectViewModelFactory(AppDatabase database, int projectId) {
        mDb = database;
        mProjectId = projectId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {

        return (T) new AddProjectViewModel(mDb, mProjectId);
    }
}
