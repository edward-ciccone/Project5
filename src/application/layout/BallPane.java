/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.layout;

import java.awt.Insets;
import java.util.ArrayList;

import application.path.Path;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/**
 *
 * @author edward
 */
public class BallPane extends Pane {

	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private Timeline animation;

	// Bean machine measurements
	double width = 600; // Pane default width
	double height = 600; // Pane default height
	double gap; // distance gap between slot lines
	double radius; // peg's radius
	double dropX; // Drop ball starting x point
	double dropY; // Drop ball starting y point //
	int numOfBalls; // numbers of slots

	// Line's sequence drawn matters
	Line baseLine;
	Line leftBaseLine; // left side line (bottom left)
	Line rightBaseLine; // right side line (bottom right)
	Line leftDiagnolLine;
	Line rightDiagnolLine;
	Line topLeftLine;
	Line topRightLine;
	Line[] slotLines; // center slot lines

	static Color pegColor = Color.GREEN;
	
	ArrayList<Double> collisions = new ArrayList<Double>();
	ArrayList<Double> finishLines = new ArrayList<Double>();

	// Triangle pegs
	Circle[][] pegs; // PEEGGSSSSS

	// Dropped balls
	ArrayList<Circle> balls;
	ArrayList<Circle> fineshedBalls;
	ArrayList<Polyline> ballPaths;

	// Animation
	ArrayList<PathTransition> mPathTransitions = new ArrayList<>();

	private BallPane() {
		baseLine = new Line();
		baseLine.translateYProperty().bind(translateXProperty());
		leftBaseLine = new Line();
		rightBaseLine = new Line();
		leftDiagnolLine = new Line();
		rightDiagnolLine = new Line();
		topLeftLine = new Line();
		topRightLine = new Line();
		fineshedBalls = new ArrayList<>();
		ballPaths = new ArrayList<>();
		slotLines = new Line[7];
		pegs = new Circle[7][];
		for (int i = 0; i < pegs.length; i++) {
			pegs[i] = new Circle[pegs.length - i];
		}
	}

	public BallPane(int numOfBalls) {
		this();
		this.numOfBalls = 10;
		balls = new ArrayList<Circle>();
		setMaxSize(width, height);

		drawLayout();

		addLayoutShapes();
		buildBalls();

	}

	private void buildBalls() {
		for (int i = 0; i < numOfBalls; i++) {

			Circle ball = new Circle(dropX, dropY, 5);
			ball.setFill(Color.RED);
			balls.add(ball);
			Path path = generatePath();
			PathTransition pathTransition = new PathTransition(Duration.millis(500), path, ball);
			pathTransition.setOnFinished(handler());
			mPathTransitions.add(pathTransition);
			// getChildren().add(ball);

		}
		mPathTransitions.get(0).setDelay(Duration.millis(1000));
		getChildren().add(balls.get(0));
	}

	private static int index = 0;
	// Circle ball = balls.get(index);
	PathTransition pTran;

	public void dropBall() {
		pTran = mPathTransitions.get(0);
		pTran.play();
	}

	private EventHandler<ActionEvent> handler() {
		return event -> {
			index += 1;

			if (index == mPathTransitions.size()) {
				pTran.stop();
				System.out.println("STOPPED");
			} else {
				getChildren().add(balls.get(index));
				pTran = mPathTransitions.get(index);
				pTran.play();
			}

		};
	}

	private Path generatePath() {
		Path path = new Path(dropX, dropY);

		while (!isAtFinishLine(path.y())) {
			if (collisions.contains(path.y())) {
				System.out.println("Collision at: " + path.y());
				if (RIGHT == (int) (Math.random() * 2))
					path.moveRight();
				else
					path.moveLeft();
			} else {
				System.out.println("Move Down");

				path.moveDown();
			}
		}

		this.generateEndYValues(path);
		/*
		 * double x = dropX; double y = dropY;
		 * 
		 * Polyline path = new Polyline(); ObservableList<Double> list =
		 * path.getPoints(); list.addAll(x, y);
		 * 
		 * while (!isAtFinishLine(y)) { if (collisions.contains(y)) { double
		 * side = Math.random() * 2; if ((int) side == RIGHT) { x += 15; } if
		 * ((int) side == LEFT) { x -= 15; } } y+=1;
		 * 
		 * list.addAll(x, y); } generateEndYValues(x, y, list);
		 */
		return path;
	}

	private boolean atEmptyFinishLine(Path path) {
		if (finishLines.isEmpty())
			return true;
		else if (!finishLines.contains(path.x()))
			return true;
		else
			return false;
	}

	private void generateEndYValues(Path path) {
		if (!finishLines.isEmpty()) {
			if (finishLines.contains(path.x()))
				for (int i = 0; i < finishLines.size(); i++) {
					if (finishLines.get(i) == path.x())
						path.moveUp();
				}
		}
		finishLines.add(path.x());
		finishLines.add(path.y());
		System.out.println(finishLines.toString());
	}

	private boolean isAtFinishLine(double y) {
		return y + 5 >= baseLine.getEndY();
	}

	private void addLayoutShapes() {
		getChildren().addAll(baseLine, leftBaseLine, rightBaseLine, this.leftDiagnolLine, this.rightDiagnolLine,
				this.topLeftLine, this.topRightLine);
		getChildren().addAll(slotLines);

		for (int i = 0; i < pegs.length; i++) {
			getChildren().addAll(pegs[i]);
		}
		// getChildren().addAll(pegs);
	}

	private void drawLayout() {
		// sequence matters...
		drawBase();
		drawSlots();
		drawLeftBaseLine();
		drawRightBaseLine();
		drawPegs();
		drawLeftDiagnolLine();
		drawRightDiagnolLine();
		drawTopLeftLine();
		drawTopRightLine();
		// drawMidLeftAndRightSide();
		// drawTopLeftAndRightSide();
		dropX = pegs[6][0].getCenterX();
		dropY = pegs[6][0].getCenterY() - 15;
	}

	private void drawTopLeftLine() {
		double y = this.leftDiagnolLine.getEndY();
		double x = this.leftDiagnolLine.getEndX();
		
		this.topLeftLine.setStartX(x);
		this.topLeftLine.setStartY(y);
		this.topLeftLine.setEndX(x);
		this.topLeftLine.setEndY(y - 25);
	}
	
	private void drawTopRightLine() {
		double y = this.rightDiagnolLine.getEndY();
		double x = this.rightDiagnolLine.getEndX();
		
		this.topRightLine.setStartX(x);
		this.topRightLine.setStartY(y);
		this.topRightLine.setEndX(x);
		this.topRightLine.setEndY(y - 25);
	}
	
	private void drawLeftDiagnolLine() {
		double y = leftBaseLine.getEndY();
		double x = leftBaseLine.getEndX();
		this.leftDiagnolLine.setStartX(x);
		this.leftDiagnolLine.setStartY(y);
		this.leftDiagnolLine.setEndX(this.slotLines[2].getEndX() + 10);
		this.leftDiagnolLine.setEndY(y - 220);
	}
	
	private void drawRightDiagnolLine() {
		double y = rightBaseLine.getEndY();
		double x = rightBaseLine.getEndX();
		
		this.rightDiagnolLine.setStartX(x);
		this.rightDiagnolLine.setStartY(y);
		this.rightDiagnolLine.setEndX(this.slotLines[4].getEndX() - 10);
		this.rightDiagnolLine.setEndY(y - 220);
		
		
		
	}

	private void drawBase() {
		double y = height * 0.9;
		double x = width * 0.2;
		double length = 240;

		baseLine.setStartX(x);
		baseLine.setStartY(y);
		baseLine.setEndX(x + length);
		baseLine.setEndY(y);
	}

	private void drawLeftBaseLine() {
		double y = baseLine.getStartY();
		double x = baseLine.getStartX();
		double length = 40;

		leftBaseLine.setStartX(x);
		leftBaseLine.setStartY(y);
		leftBaseLine.setEndX(x);
		leftBaseLine.setEndY(y - length);
	}

	private void drawRightBaseLine() {
		double y = baseLine.getEndY();
		double x = baseLine.getEndX();
		double length = 40;

		rightBaseLine.setStartX(x);
		rightBaseLine.setStartY(y);
		rightBaseLine.setEndX(x);
		rightBaseLine.setEndY(y - length);
	}

	private void drawSlots() {
		double slotGap = 30;
		double x = baseLine.getStartX() + slotGap;
		double y = baseLine.getStartY();
		double length = 40;

		for (int i = 0; i < 7; i++) {
			Line line = new Line();
			line.setStartX(x);
			line.setStartY(y);
			line.setEndX(x);
			line.setEndY(y - length);
			slotLines[i] = line;
			x += slotGap;
		}
	}

	private void drawPegs() {
		double x = slotLines[0].getStartX();
		double y = slotLines[0].getEndY() - 5;

		for (int i = 0; i < pegs.length; i++) {
			collisions.add(y - 10);
			for (int j = 0; j < pegs[i].length; j++) {
				Circle peg = new Circle(5.0);
				peg.setFill(pegColor);
				peg.setCenterX(x);
				peg.setCenterY(y);
				pegs[i][j] = peg;
				x += 30;
			}
			x = slotLines[0].getStartX();
			y -= 30;
			if (i != 6) {
				x += (pegs.length - pegs[i + 1].length) * 15;
			}
		}
	}

}