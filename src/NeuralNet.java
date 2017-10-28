import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class NeuralNet {

    int numInput = 3;
    int numHidden = 4;
    int numOutput = 2;

    /**
     * Two input nodes: angle of and distance to closest enemy
     * Branch into four hidden nodes
     * Sum into two output nodes: turn and direction
     */
    @Getter @Setter List<Double> weights = new ArrayList<>();

    public NeuralNet(){
        for(int i = 0; i < numInput * numHidden + numHidden * numOutput; i++)
            weights.add(Math.random() * 20 - 10);
    }

    public void reset(){
        for(int i = 0; i < weights.size(); i++)
            weights.set(i, Math.random() * 20 - 10);
    }

    public double[] run(double... input){

        if(input.length != numInput)
            throw new RuntimeException("Incorrect amount of input!");

        double[] hidden = new double[numHidden];
        for(int i = 0; i < input.length; i++)
            for (int j = 0; j < hidden.length; j++)
                hidden[j] += input[i] * weights.get(i * numHidden + j);

        double[] output = new double[numOutput];
        for(int i = 0; i < output.length; i++)
            for(int j = 0; j < hidden.length; j++)
                output[i] += hidden[j] * weights.get(numInput * numHidden + i * numHidden + j);

        return output;

    }

    public NeuralNet clone(){
        NeuralNet net = new NeuralNet();
        for(int i = 0; i < weights.size(); i++)
            net.getWeights().set(i, weights.get(i));
        return net;
    }
}
