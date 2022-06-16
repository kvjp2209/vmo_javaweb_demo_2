package com.vmo.core.models.database.entities;

public enum EStatus {
    ENABLE(1),
    DISABLE(2);

    private int id;

    EStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
