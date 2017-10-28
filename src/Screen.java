import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Patrick Zhong
 * @date 7 September 2017
 * @description Launches the JavaFX window
 */
public class Screen extends Application implements EventHandler {

    @Getter
    private GraphicsContext context;
    @Getter
    private static Screen instance;
    @Getter
    private static int width = 1000;
    @Getter
    private static int height = 800;
    @Getter
    private static int period = 5;

    private NeuralNet net;

    List<Double> weights = Arrays.asList(-8.05394527425362, -1.2971833060950466, -0.340369465182242, 0.2599191015600866, 1.0023342917513105, -6.311181845869149, 0.1336943015807428, -1.7984450325257324, 1.0119148612564892, -3.406606397344416, 2.8435731511471847, -3.784594662635959, 0.39852876634016, -2.0939111845033276, 7.407887236927216, 7.553688781148541, 6.290271495660669, -5.862881217235277, 9.724155490724474, -8.853743871981266);

    public static void main(String[] args){
        Application.launch();
    }

    /*
     * Sets up the stage with a width, height, and graphics canvas
     */
    @Override
    public void start(Stage stage) throws Exception {

        instance = this;

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // get screen size wrapper
        width = (int)dim.getWidth();
        height = (int)(dim.getHeight() / 1);

        instance = this;

        Canvas canvas = new Canvas(width, height);                  // object representing onscreen canvas
        context = canvas.getGraphicsContext2D();                    // object that handles canvas manipulation

        Scene scene = new Scene(new Group(canvas));                 // create a scene with the canvas as the only node
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle("Mouse Net");

        scene.setOnMouseMoved(this);
        scene.setOnKeyPressed(this);

        stage.show();

        if(weights == null)
            net = Evoluter.getInstance().getBest();
        else {
            net = new NeuralNet();
            for(int i = 0; i < weights.size(); i++)
                net.getWeights().set(i, weights.get(i));
        }
        System.out.println("BEST: "+ Evoluter.getInstance().getFitness(net) + " " + Evoluter.get(net.run(0.3, 0.7, 1)));

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                paint(context);
            }
        }, period, period);
    }

    private void paint(GraphicsContext context){

        //double tM = Math.atan(screenY / screenX) + (screenX < 0 ? Math.PI : 0);
        if(screenX != x) {

            double pM = (Math.atan((screenX - x) / (y - screenY)) + (y < screenY ? Math.PI : 0)) * 180 / Math.PI;

            pM = (pM + 360)  % 360;
            theta = (theta + 360)  % 360;

            double[] results = net.run(theta, pM, Math.sqrt(Math.pow(screenY - y, 2) + Math.pow(screenX - x, 2)));

            theta = (theta + results[0]) % 360;
            //System.out.println(pM + " " + theta + " " + results[0]);

            x += Math.sin(theta * Math.PI / 180) * Math.signum(results[1]) * 2;
            y += - Math.cos(theta * Math.PI / 180) * Math.signum(results[1]) * 2;

        }

        double[] rot = RotationUtil.rotate(x + 10, y + 10, theta);

        context.setFill(Color.BLACK);
        context.fillRect(0, 0, width, height);

        context.setFill(Color.AQUAMARINE);
        context.fillRect(screenX - 5, screenY - 5, 10, 10);

        context.save();
        context.rotate(theta);

        context.setFill(Color.YELLOW);
        context.fillRect(rot[0] - 10, rot[1] - 10, 20, 20);

        context.setFill(Color.RED);
        context.fillRect(rot[0] - 10, rot[1] - 10, 20, 5);

        context.restore();

        //System.out.println(screenX + " " + screenY);
    }

    double screenX = 0;
    double screenY = 0;
    double x = 500;
    double y = 500;
    double theta = 0;

    @Override
    public void handle(Event event) {
        if(event instanceof MouseEvent){
            screenX = ((MouseEvent)event).getScreenX();
            screenY = ((MouseEvent)event).getY();
        }
        else if(event instanceof KeyEvent){
            if(((KeyEvent)event).getCode().getName().equals("Esc"))
                System.exit(0);
        }
    }
}