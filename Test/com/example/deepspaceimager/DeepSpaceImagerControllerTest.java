package com.example.deepspaceimager;

import org.junit.jupiter.api.Test;

import static com.example.deepspaceimager.DisjointSet.find;
import static com.example.deepspaceimager.DisjointSet.union;
import static org.junit.jupiter.api.Assertions.*;

class DeepSpaceImagerControllerTest {

    @Test
    void unionFindOnArrayWorksForMixedArray() {
        int[] pixels = new int[100];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = i;
        }
        for (int pixel : pixels) {
            union(pixels, pixel, pixel + 1);
        }
        assertEquals(0, pixels[0]);
    }

    @Test
    void unionFindOnArrayWorksRandomArray() {
        int[] pixels = new int[100];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (int) Math.round( Math.random());
        }
        for (int pixel : pixels) {
                union(pixels, pixel, pixel + 1);
        }
        assertTrue((pixels[40] == 0) || (pixels[40] == 1));
    }

}