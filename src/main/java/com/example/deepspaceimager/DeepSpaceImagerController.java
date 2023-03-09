package com.example.deepspaceimager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DeepSpaceImagerController {
    @FXML
    ImageView imageview;
    @FXML
    ListView<String> listview;
    WritableImage writableImage;
    WritableImage originalImage;
    PixelReader pixelReader;
    PixelWriter pixelWriter;
    Stage stage;

    static int width;
    static int height;
    HashSet<Integer> hashset = new HashSet<Integer>();
    int[] pixels;
    List<CelestialObject> celestialObjects = new ArrayList<>();



    public void AddFile(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
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
//        Scale(writableImage,512,512,false);
//        imageview.setSmooth(true);
//        System.out.println(count);
    }

    public void ConvertToBlackAndWhite() {
        width = (int) writableImage.getWidth();
        height = (int) writableImage.getHeight();
        pixelReader = writableImage.getPixelReader();
        pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                Color color = pixelReader.getColor(y, x);
                if (color.getBrightness() < 0.3) {
                    writableImage.getPixelWriter().setColor(y, x, Color.rgb(0, 0, 0));
                } else {
                    writableImage.getPixelWriter().setColor(y,x,Color.rgb(255,255,255));
                }
            }
        }
    }

    public void ScanImageForSetsAndUnionFind(ActionEvent actionEvent) {
        pixels  = new int[width*height];
        for(int i=0;i<pixels.length;i++) pixels[i]=i;
        // convert to black and white
        ConvertToBlackAndWhite();

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

        UnionFindOnArray();
        GetNumberOfCelestialObjects();
        }

    public void CreateCelestialObjects() {
        for (Integer s : hashset) {
            if (SizeOfCelestialObjects(s) > 100) {
                CelestialObject cj = new CelestialObject(SizeOfCelestialObjects(s), EstimatedSulphur(s), EstimatedHydrogen(s), EstimatedOxygen(s), s);
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

    public void UnionFindOnArray() {
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

    public void GetNumberOfCelestialObjects() {
        for (int pixel : pixels) {
            if (pixel != -1) {
                hashset.add(pixel);
            }
        }
    }

    public int SizeOfCelestialObjects(Integer value) {
        //Initialise counter
        int count = 0;
        //Loop through all pixels on image
        for (int pixel : pixels) {
            //Ignore -1 (black) pixels
            if (pixel != -1) {
                //If the value of pixels is equal to s increment count
                if (pixel == value) {
                    count++;
                }
            }
        }
        return count;
    }

    public double EstimatedSulphur(Integer value) {
        int count = 0;
        double red = 0.0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != -1) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);
//                    System.out.print(color.getRed());
                    count++;
                    red += color.getRed();
//                    red += red;
//                    System.out.print(red);
                    }
                }
            }
        if (count != 0)
//        System.out.print(count);
        red = (red/count);
        return red;
        }

    public double EstimatedHydrogen(Integer value) {
        int count = 0;
        double green = 0.0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != -1) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);
//                    System.out.print(color.getRed());
                    count++;
                    green += color.getGreen();
//                    red += red;
//                    System.out.print(red);
                }
            }
        }
        if (count != 0)
//        System.out.print(count);
        green = (green/count);
        return green;
    }

    public double EstimatedOxygen(Integer value) {
        int count = 0;
        double blue = 0.0;
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] != -1) {
                if (pixels[i] == value) {
                    Color color = originalImage.getPixelReader().getColor(i%width,i/width);
                    count++;
                    blue += color.getBlue();

                }
            }
        }
        if (count != 0)
        blue = (blue/count);
        return blue;
    }

    public void ColourDisjointSets() {
    }

    public void Scale(WritableImage source, int targetWidth, int targetHeight, boolean preserveRatio) {
        imageview = new ImageView(source);
        imageview.setPreserveRatio(preserveRatio);
        imageview.setFitWidth(targetWidth);
        imageview.setFitHeight(targetHeight);
    }

    public void Debug(ActionEvent actionEvent) {
//        ColourDisjointSets();
//        CreateCelestialObjects();
//        double test = EstimatedOxygen(4756400);
//        System.out.println(test);
        for(int i=0;i<pixels.length;i++)
            System.out.print(DisjointSet.find(pixels,i)+((i+1)%width==0 ? "\n" : " "));
    }
    
    public int[] getPixels() {
        return pixels;
    }
}
