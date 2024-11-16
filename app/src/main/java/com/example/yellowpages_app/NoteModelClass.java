package com.example.yellowpages_app;

public class NoteModelClass {

    private String id;
    private String email;
    private String title;
    private String description;
    private boolean completed;

    // No-argument constructor required for Firebase
    public NoteModelClass() {
    }
    public NoteModelClass(String id, String email, String title, String description, boolean completed) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
