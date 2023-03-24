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
        return Integer.compare(otherObject.getSize(), getSize());
    }

    public int getSize() {
        return size;
    }

    public int getRoot() {
        return root;
    }

    public int getObjectNumber() {
        return objectNumber;
    }
}
