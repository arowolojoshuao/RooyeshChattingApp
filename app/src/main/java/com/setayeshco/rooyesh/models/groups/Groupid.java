package com.setayeshco.rooyesh.models.groups;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Asrdigital on 29/04/2018.
 */

public class Groupid  extends RealmObject {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    private int id;

    private int ids;

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }
}
