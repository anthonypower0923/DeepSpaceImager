package com.example.deepspaceimager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DeepSpaceImagerController {
    @FXML
    ImageView imageview;
    @FXML
    ListView<String> listview;
    @FXML
    Pane pane;
    WritableImage writableImage;
    WritableImage originalImage;
    PixelReader pixelReader;
    PixelWriter pixelWriter;
    Stage stage;

    int width;
    int height;
    HashSet<Integer> hashset = new HashSet<>();
    int[] pixels;
    List<CelestialObject> celestialObjects = new LinkedList<>();



    public void addFile(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        FileInputStream fileInputStream = new FileInputStream(selectedFile.getPath());
        Image image = new Image(fileInputStream);
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        pixelReader = image.getPixelReader();
        writableImage = new WritableImage(pixelReader, width, height);
        originalImage = new WritableImage(pixelReader, width, height);
        pixelWriter = writableImage.getPixelWriter();
        imageview.setImage(writableImage);
        celestialObjects = new ArrayList<>();
//        imageview.setSmooth(true);
//        System.out.println(count);
    }

    public void convertToBlackAndWhite() {
        width = (int) writableImage.getWidth();
        height = (int) writableImage.getHeight();
        pixelReader = writableImage.getPixelReader();
        pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                if (color.getBrightness() < 0.3) {
                    writableImage.getPixelWriter().setColor(x, y, Color.rgb(0, 0, 0));
                } else {
                    writableImage.getPixelWriter().setColor(x,y,Color.rgb(255,255,255));
                }
            }
        }
    }

    public void scanImageForSetsAndUnionFind(ActionEvent actionEvent) {
        pixels  = new int[width * height];
        for(int i = 0; i<pixels.length; i++) pixels[i]=i;
        // convert to black and white
        convertToBlackAndWhite();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                if (y*width+x != pixels.length) {
                    if (color.equals(Color.BLACK)) {
                        pixels[y * width + x] = -1;
                    }
                }
            }
        }

//        for(int i=0;i<pixels.length;i++)
//            System.out.print(DisjointSet.find(pixels,i)+((i+1)%width==0 ? "\n" : " "));

        unionFindOnArray();
        getNumberOfCelestialObjects();
        imageview.setImage(originalImage);
        }

    public void createCelestialObjects() {
        for (int s : hashset) {
            if (sizeOfCelestialObjects(DisjointSet.find(pixels, s)) > 500) {
                CelestialObject cj = new CelestialObject(sizeOfCelestialObjects(DisjointSet.find(pixels,s)), estimatedSulphur(s), estimatedHydrogen(s), estimatedOxygen(s), DisjointSet.find(pixels, s));
                celestialObjects.add(cj);
            }
        }
        Collections.sort(celestialObjects);
        int i = 0;
        for (CelestialObject object : celestialObjects) {
            i++;
            object.objectNumber = i;
        }
        for (CelestialObject object : celestialObjects)
            listview.getItems().add(object.toString());
    }

    public void unionFindOnArray() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if((DisjointSet.find(pixels,y*width+x) != -1) && (DisjointSet.find(pixels,y*width+x+1) != -1)) {
                    DisjointSet.union(getPixels(), y * width + x, y * width + x + 1);
                }
                if (y * width + x + width < pixels.length)
                if((DisjointSet.find(pixels,y*width+x) != -1) && (DisjointSet.find(pixels,y*width+x+width) != -1)) {
                    DisjointSet.union(getPixels(), y * width + x, y * width + x + width);
                }
            }
        }
    }

    public void getNumberOfCelestialObjects() {
        for (int pixel : pixels) {
            if (pixel + 1 < pixels.length) {
                    if ((pixel != -1) && (pixel + 1 != -1)) {
                        hashset.add(DisjointSet.find(pixels, pixel));
                }
            }
        }
    }

    public int sizeOfCelestialObjects(int value) {
        //Initialise counter
        int count = 0;
        //Loop through all pixels on image
        for (int pixel : pixels) {
            //Ignore -1 (black) pixels
            if (pixel != -1) {
                //If the value of pixels is equal to s increment count
                if (DisjointSet.find(pixels,pixel) == value) {
                    count++;
                }
            }
        }
        return count;
    }

    public double estimatedSulphur(int value) {
        int count = 0;
        double red = 0.0;
        for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);
                    count++;
                    red += color.getRed();
                }
            }
        if (count != 0)
        red = (red/count);
        return red;
        }

    public double estimatedHydrogen(int value) {
        int count = 0;
        double green = 0.0;
        for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);

                    count++;
                    green += color.getGreen();
            }
        }
        if (count != 0)
        green = (green/count);
        return green;
    }

    public double estimatedOxygen(int value) {
        int count = 0;
        double blue = 0.0;
        for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);
                    count++;
                    blue += color.getBlue();

            }
        }
        if (count != 0)
        blue = (blue/count);
        return blue;
    }

    public void randomlyColourDisjointSets() {
        Color color;
        for (CelestialObject celestialobject : celestialObjects) {
            color = Color.color(Math.random(), Math.random(), Math.random());
            int s = celestialobject.getRoot();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (s == DisjointSet.find(pixels, y*width+x)) {
                            writableImage.getPixelWriter().setColor(x, y, color);
                    }
                }
            }
        }
        imageview.setImage(writableImage);
    }

    public void chooseDisjointSetToColour() {
        Color color = Color.color(Math.random(), Math.random(), Math.random());
        Point p = MouseInfo.getPointerInfo().getLocation();
        int pointX = (int) p.getX();
        int pointY = (int) p.getY();
        System.out.print(p);
        System.out.print(imageview.getFitWidth() + " , " + imageview.getFitHeight());
            int s = DisjointSet.find(pixels ,(pointY*width)+pointX);
            System.out.print(" | " + s + " |  ");
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (s == DisjointSet.find(pixels, y * width + x) && (DisjointSet.find(pixels,y*width+x) != -1)) {
                        writableImage.getPixelWriter().setColor(x, y, color);
                }
            }
        }
            imageview.setImage(writableImage);
    }

//    public Image scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
//        imageview = new ImageView(source);
//        imageview.setPreserveRatio(preserveRatio);
//        imageview.setFitWidth(targetWidth);
//        imageview.setFitHeight(targetHeight);
//        return imageview.snapshot(null, null);
//    }

    public void debug(ActionEvent actionEvent) {
          randomlyColourDisjointSets();
//        for(int i=0;i<pixels.length;i++)
//            System.out.print(DisjointSet.find(pixels,i)+((i+1)%width==0 ? "\n" : " "));
    }
    
    public int[] getPixels() {
        return pixels;
    }

}
