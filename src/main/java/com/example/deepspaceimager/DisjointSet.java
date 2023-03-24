package com.example.deepspaceimager;

public class DisjointSet {

//    Iterative version of find with path compression
    public static int find(int[] a, int id) {
        if(a[id]==-1) {
            return -1;
        }

        while(a[id] != id) {
            a[id]=a[a[id]];//Compress path
            id=a[id];
        }
        return id;
    }

    //Quick union of disjoint sets containing elements p and q (Version 2)
    public static void union(int[] a, int p, int q) {
        if ((find(a,p) != -1) && (find(a,q) != -1))
        a[find(a,q)]=find(a,p); //The root of q is made reference the root of p
    }

}
