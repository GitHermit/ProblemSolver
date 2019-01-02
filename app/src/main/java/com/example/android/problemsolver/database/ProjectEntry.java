package com.example.android.problemsolver.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "project")
public class ProjectEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @TypeConverters(DatabaseTypeConverter.class)
    @ColumnInfo(name = "Steps")
    private List<String> steps = new ArrayList<>();
    private boolean completed = false;

    @Ignore
    public ProjectEntry(String title, List<String> steps) {
        this.name = title;
        this.steps = steps;
        this.completed = false;

    }

    public ProjectEntry(int id, String name, List<String> steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String description) {
        this.name = description;
    }


    public List<String> getSteps() {return steps;}

    public void setSteps(List<String> steps) {this.steps = steps;}

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
