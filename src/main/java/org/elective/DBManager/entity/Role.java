package org.elective.DBManager.entity;

import java.io.Serializable;

public class Role implements Serializable, Entity {
    int id;
    String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {

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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
