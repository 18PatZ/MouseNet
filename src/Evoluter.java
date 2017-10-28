import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Evoluter {

    @Getter private static Evoluter instance = new Evoluter();
    private Random random = new Random();

    public NeuralNet getBest(){

        List<NeuralNet> pool = new ArrayList<>();
        for(int i = 0; i < 4; i++)
            pool.add(new NeuralNet());

        for(int p = 0; p < 50; p++) {

            if(p > 0)
                for(int i = 1; i < pool.size(); i++)
                    pool.get(i).reset();

            for (int t = 0; t < 10000; t++) {
                pool.sort((n1, n2) -> (int) (getFitness(n1) - getFitness(n2)));

                NeuralNet best = pool.get(0);
                for (int i = 1; i < pool.size(); i++) {
                    NeuralNet child = mutate(breed(best, pool.get(i)));
                    pool.set(i, child);
                }

                System.out.println(pool.get(0).getWeights());
            }
        }

        /*Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String[] split = scanner.nextLine().split(" ");
            double[] output = pool.get(0).run(Double.parseDouble(split[0]), Double.parseDouble(split[1]), 1);
            System.out.println("Delta theta: " + output[0]);
        }*/

        return pool.get(0);

    }

    private NeuralNet breed(NeuralNet n1, NeuralNet n2){
        NeuralNet child = n1.clone();
        for(int i = 0; i < child.getWeights().size(); i++)
            if(random.nextBoolean())
                child.getWeights().set(i, n2.getWeights().get(i));
        return child;
    }

    private NeuralNet mutate(NeuralNet net){
        List<Double> weights = net.getWeights();
        for(int i = 0; i < weights.size(); i++)
            if(random.nextDouble() * 10 <= 1)
                weights.set(i, random.nextDouble() * 20 - 10);
        return net;
    }

    double[][] data = {{3.14, 2.5, 3}, {Math.PI, Math.PI * 1.8, 1}, {0.5, 0.8, -1}, {0.5, 0.2, -3}, {0.1, 0.3, 1}, {0.10, 0.01, 1}};
    public double getFitness(NeuralNet net){ // lower the better
        // your angle
        // enemy angle
        // distance to enemy

        double fitness = 0;

        for(double[] d : data) {
            double[] output = net.run(d[0], d[1], d[2]);

            fitness += Math.abs((d[1] - d[0]) - output[0]) + Math.abs(Math.signum(d[2]) - Math.signum(output[1]));
        }

        return fitness;
    }

    public static void main(String[] args){
        NeuralNet best = Evoluter.getInstance().getBest();
        System.out.println("BEST: "+ getInstance().getFitness(best) + " " + get(best.run(8, 12, 1)));
    }

    public static String get(double... args){
        String s = "[";
        for(int i = 0; i < args.length; i++)
            s += args[i] + (i == args.length - 1 ? "]" : " ");
        return s;
    }

}
