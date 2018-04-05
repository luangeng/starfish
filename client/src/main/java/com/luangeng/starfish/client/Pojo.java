package com.luangeng.starfish.client;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by LG on 2017/12/2.
 */
public class Pojo implements Serializable {

    private int id;

    private String name;

    private Date birth;

    private boolean man;

    private List<String> list;

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

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public boolean isMan() {
        return man;
    }

    public void setMan(boolean man) {
        this.man = man;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birth=" + birth +
                ", man=" + man +
                ", list=" + list +
                '}';
    }
}
