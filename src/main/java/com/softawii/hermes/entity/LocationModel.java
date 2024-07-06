package com.softawii.hermes.entity;

import javax.persistence.*;
import java.text.DecimalFormat;

@Entity
public class LocationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String world;
    @Column
    private double x;
    @Column
    private double y;
    @Column
    private double z;

    public LocationModel() {

    }

    public LocationModel(String world, String name, double x, double y, double z) {
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String toDisplayString() {
        DecimalFormat f = new DecimalFormat("##.00");
        return name + " (" + f.format(x) + ", " + f.format(y) + ", " + f.format(z) + ")";
    }
}
