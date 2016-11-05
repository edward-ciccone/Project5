/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.path;

import javafx.collections.ObservableList;
import javafx.scene.shape.Polyline;

/**
 *
 * @author edward
 */
public class Path extends Polyline {

    private static final int RIGHT = 15;
    private static final int LEFT = -15;
    private static final int DOWN = 1;
    private static final int UP = -10;
    private static final int NO_CHANGE = 0;
    
    private static final int Y_OFFSET = 1;
    private static final int X_OFFSET = 2;

    private ObservableList<Double> pathList;

    public Path(double x, double y) {
        pathList = this.getPoints();
        pathList.addAll(x, y);
    }

    public int size() {
        return pathList.size();
    }

    public double x() {
        return pathList.get(size() - X_OFFSET);
    }

    public double y() {
        return pathList.get(size() - Y_OFFSET);
    }
    
    public void moveDown() {
        move(NO_CHANGE, DOWN);
    }
    
    public void moveUp() {
    	move(NO_CHANGE, UP);
    }

    public void moveRight() {
        move(RIGHT, DOWN);
    }

    public void moveLeft() {
        move(LEFT, DOWN);
    }

    private void move(int x, int y) {
        pathList.addAll(x() + x, y() + y);
    }
}