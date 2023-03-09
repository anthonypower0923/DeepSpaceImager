package com.example.deepspaceimager;

public class CelestialObject implements Comparable<CelestialObject> {
    int objectNumber;
    static int objectCounter;
    int size;
    double sulphur;
    double hydrogen;
    double oxygen;
    int root;

    public CelestialObject(int size, double sulphur, double hydrogen, double oxygen , int root) {
        objectNumber = objectCounter;
        this.size = size;
        this.sulphur = sulphur;
        this.hydrogen = hydrogen;
        this.oxygen = oxygen;
        this.root = root;
        objectCounter++;
    }

    @Override
    public String toString() {
        return
                "Celestial object number: " + objectNumber + '\n' +
                " Estimated size (pixel units): " + size + '\n' +
                " Estimated sulphur: " + sulphur + '\n' +
                " Estimated hydrogen: " + hydrogen + '\n' +
                " Estimated oxygen: " + oxygen + '\n' +
                " Root Node: " + root + '\n' +
                "-------------------------------------------------" + '\n';
    }

    @Override
    public int compareTo(CelestialObject otherObject) {
        return Integer.compare(getSize(), otherObject.getSize());
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getSulphur() {
        return sulphur;
    }

    public void setSulphur(double sulphur) {
        this.sulphur = sulphur;
    }

    public double getHydrogen() {
        return hydrogen;
    }

    public void setHydrogen(double hydrogen) {
        this.hydrogen = hydrogen;
    }

    public double getOxygen() {
        return oxygen;
    }

    public void setOxygen(double oxygen) {
        this.oxygen = oxygen;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }
}
