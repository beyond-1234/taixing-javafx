package com.self.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Item used to store list item from reports
 */
public class Item {
    private String name;
    private String spec;
    private double toStorage;
    private String company;
    private Date inDate;

    public Item() {
    }

    public Item(String name, String spec, double toStorage, String company, Date inDate) {
        this.name = name;
        this.spec = spec;
        this.toStorage = toStorage;
        this.company = company;
        this.inDate = inDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public double getToStorage() {
        return toStorage;
    }

    public void setToStorage(double toStorage) {
        this.toStorage = toStorage;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getInDate() {
        return inDate;
    }

    public String getInDate(boolean str) {
        return new SimpleDateFormat("yyyy.MM.dd").format(inDate);
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", spec='" + spec + '\'' +
                ", toStorage=" + toStorage +
                ", company='" + company + '\'' +
                ", inDate='" + new SimpleDateFormat("yyyy.MM.dd").format(inDate) + '\'' +
                '}';
    }
}
