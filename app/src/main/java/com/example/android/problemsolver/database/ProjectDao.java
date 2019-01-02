package com.example.android.problemsolver.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProjectDao {

    @Query("SELECT * FROM project ORDER BY id")
    LiveData<List<ProjectEntry>> loadAllProjects();

    @Insert
    void insertProject(ProjectEntry projectEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProject(ProjectEntry projectEntry);
    @Update(onConflict =  OnConflictStrategy.REPLACE)
    void completeProject(ProjectEntry projectEntry);

    @Delete
    void deleteProject(ProjectEntry projectEntry);

    @Query("SELECT * FROM project WHERE id = :id")
    LiveData<ProjectEntry> loadProjectById(int id);
}
