package com.example.semmes.lab2_dict_1;

/**
 * Created by SemmEs on 31.03.2016.
 */
public class Pair {
    String rus;
    String engl;
    int status;
    int count;
    int id;

    public Pair() {
    }

    public Pair(String rus, String engl, int status, int count) {
        this.rus = rus;
        this.engl = engl;
        this.status = status;
        this.count = count;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "rus='" + rus + '\'' +
                ", engl='" + engl + '\'' +
                ", status=" + status +
                ", count=" + count +
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRus() {
        return rus;
    }

    public void setRus(String rus) {
        this.rus = rus;
    }

    public String getEngl() {
        return engl;
    }

    public void setEngl(String engl) {
        this.engl = engl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
