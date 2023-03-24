package com.example.deepspaceimager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


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
    @FXML
    VBox imageviewbox;
    WritableImage writableImage;
    WritableImage originalImage;
    PixelReader pixelReader;
    PixelWriter pixelWriter;
    Stage stage;

    int width;
    int height;
    HashSet<Integer> hashset = new HashSet<>();
    List<Integer> objectRoots = new LinkedList<Integer>();
    int[] pixels;
    List<CelestialObject> celestialObjects = new LinkedList<>();
    List<LinkedList<Integer>> indexes = new ArrayList<>();



    public void addFile(ActionEvent actionEvent) throws IOException {
        if (listview.getItems() != null) {
            listview.getItems().clear();
        }
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        FileInputStream fileInputStream = new FileInputStream(selectedFile.getPath());
        Image image = new Image(fileInputStream,imageview.getFitWidth(),imageview.getFitHeight(),false,true);
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        pixelReader = image.getPixelReader();
        writableImage = new WritableImage(pixelReader, width, height);
        originalImage = new WritableImage(pixelReader, width, height);
        pixelWriter = writableImage.getPixelWriter();
        imageview.setImage(writableImage);
        celestialObjects = new ArrayList<>();
    }

    public void convertToBlackAndWhite() {
        imageview.setImage(writableImage);
        width = (int) writableImage.getWidth();
        height = (int) writableImage.getHeight();
        pixelReader = writableImage.getPixelReader();
        pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = originalImage.getPixelReader().getColor(x, y);
                if (color.getBrightness() < 0.3) {
                    writableImage.getPixelWriter().setColor(x, y, Color.rgb(0, 0, 0));
                } else {
                    writableImage.getPixelWriter().setColor(x,y,Color.rgb(255,255,255));
                }
            }
        }
    }

    public void scanImageForSetsAndUnionFind() {
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

        unionFindOnArray(pixels);
        getNumberOfCelestialObjects(pixels);
        imageview.setImage(originalImage);
        }

    public void unionFindOnArray(int[] pixels) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if ((y*width+x < pixels.length) && (y*width+x+1 < pixels.length)) {
                    DisjointSet.union(getPixels(), y * width + x, y * width + x + 1);
                }
                if (y * width + x + width < pixels.length)
                    DisjointSet.union(getPixels(), y * width + x, y * width + x + width);
            }
        }
    }

    public void getNumberOfCelestialObjects(int[] pixels) {
        hashset.clear();
        for (int i : pixels) {
            if (i + 1 < pixels.length) {
                if ((i + width != -1) && (i + width < pixels.length)) {
                    if ((i != -1) && (i + 1 != -1)) {
                            hashset.add(DisjointSet.find(pixels, i));
                        }
                }
            }
        }
    }

    public void createCelestialObjects() {
        scanImageForSetsAndUnionFind();
        celestialObjects.clear();
        objectRoots.clear();
        for (int s : hashset) {
            if (sizeOfCelestialObjects(DisjointSet.find(pixels, s), pixels) > 50) {
                CelestialObject cj = new CelestialObject(sizeOfCelestialObjects(DisjointSet.find(pixels,s) , pixels), estimatedSulphur(s,pixels), estimatedHydrogen(s,pixels), estimatedOxygen(s,pixels), DisjointSet.find(pixels, s));
                celestialObjects.add(cj);
            }
        }
        Collections.sort(celestialObjects);
        int i = 0;
        for (CelestialObject object : celestialObjects) {
            i++;
            object.objectNumber = i;
            objectRoots.add(object.getRoot());
            Collections.sort(objectRoots);
        }
        for (CelestialObject object : celestialObjects) {
            listview.getItems().add(object.toString());
        }
    }

    private int sizeOfCelestialObjects(int value,int[] pixels) {
        //Initialise counter
        int count = 0;
        //Loop through all pixels on image
        for (int i : pixels) {
            //Ignore -1 (black) pixels
            if (i != -1) {
                //If the value of pixels is equal to s increment count
                if (DisjointSet.find(pixels,i) == value) {
                    count++;
                }
            }
        }
        return count;
    }

    private void createIndexListsList() {
        indexes.clear();
        LinkedList<Integer> list = new LinkedList<>();
        for (int root : objectRoots) {
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] != -1) {
                        if (root == DisjointSet.find(pixels, i)) {
                            list.add(i);
                        }
                    }
                }
        indexes.add(list);
        list = new LinkedList<>();
        }
    }

    public void createCircles() {
        imageview.setImage(originalImage);
        createIndexListsList();

        for (LinkedList<Integer> list : indexes) {
                int top = Collections.min(list)/width;
                int bottom = Collections.max(list)/width;
                int left = Collections.min(list, (a, b) -> a % width - b % width) % width;
                int right = Collections.max(list, (a, b) -> a % width - b % width) % width;
                Circle circle = new Circle();

                Text text = new Text(left-5,bottom-5,"This shouldn't appear");
                if (left != 0) {
                    circle = new Circle(left + ((right - left) / 2.0), top + ((bottom - top) / 2.0), (Math.max((right - left), (bottom - top)) / 2.0) + 2);
                } else {
                    circle = new Circle(right, top + ((bottom - top) / 2.0), ((bottom - top) / 2.0) + 2);
                    text = new Text(imageview.getFitWidth()-10,bottom-5,"This shouldn't appear");
                }
                text.setFill(Color.LIMEGREEN);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.BLUE);
                circle.setTranslateX(imageview.getLayoutX());
                circle.setTranslateY(25);
                for (CelestialObject celestialobject : celestialObjects) {
                    if (list.contains(celestialobject.getRoot())) {
                        Tooltip t = new Tooltip(celestialobject.toString());
                        Tooltip.install(circle, t);
                        text.setText("" + celestialobject.getObjectNumber());
                    }
                }
                pane.getChildren().add(1,circle);
                pane.getChildren().add(2,text);
            }
        }

    private void removeAllCircles() {
        for (LinkedList<Integer> list : indexes) {
            pane.getChildren().remove(1);
        }
    }

    public void removeCircles() {
        //Ran twice because of weird bug that won't remove all circles
        removeAllCircles();
        removeAllCircles();
    }

    public void viewOriginalImage() {
        imageview.setImage(originalImage);
    }

    private double estimatedSulphur(int value , int[] pixels) {
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

    private double estimatedHydrogen(int value , int[] pixels) {
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

    private double estimatedOxygen(int value , int[] pixels) {
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
        for (int i = 0; i < pixels.length; i++) {
            writableImage.getPixelWriter().setColor(i%width,i/width,Color.BLACK);
        }
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

    public void chooseDisjointSetToColour(MouseEvent e) {
        Color color = Color.color(Math.random(), Math.random(), Math.random());
        double pointX = e.getX();
        double pointY = e.getY();
        ImageView view = (ImageView) e.getSource();
        Bounds bounds = view.getLayoutBounds();
        double xScale = bounds.getWidth() / view.getImage().getWidth();
        double yScale = bounds.getHeight() / view.getImage().getHeight();
        pointX /= xScale;
        pointY /= yScale;
        int xCord = (int) pointX;
        int yCord = (int) pointY;
            int s = DisjointSet.find(pixels ,(yCord*width)+xCord);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (s == DisjointSet.find(pixels, y * width + x) && (DisjointSet.find(pixels,y*width+x) != -1)) {
                        writableImage.getPixelWriter().setColor(x, y, color);
                }
            }
        }
            imageview.setImage(writableImage);
    }

//    public void debug() {
//          randomlyColourDisjointSets();
////        for(int i=0;i<pixels.length;i++)
////            System.out.print(DisjointSet.find(pixels,i)+((i+1)%width==0 ? "\n" : " "));
//    }
    
    public int[] getPixels() {
        return pixels;
    }

}
