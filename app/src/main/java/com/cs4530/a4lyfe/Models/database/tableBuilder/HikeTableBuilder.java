package com.cs4530.a4lyfe.models.database.tableBuilder;

import com.cs4530.a4lyfe.models.database.tables.HikeTable;

public class HikeTableBuilder {
    private String hikeJson;

    public HikeTableBuilder setHikeJson(String hikeJson) {
        this.hikeJson = hikeJson;
        return this;
    }

    public HikeTable createHikeTable() {
        return new HikeTable(hikeJson);
    }
}
