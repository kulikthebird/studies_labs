package UrnBall;

import java.util.Random;
import Chart.Chart;


public class UrnBalls
{
    class Result
    {
        public Result()
        {}
        public Result(int max, int empty)
        {
            maxNumOfElementsInUrn = max;
            numOfEmptyUrns = empty;
        }
        public int maxNumOfElementsInUrn = 0;
        public int numOfEmptyUrns = 0;
    }

    public Result experiment(int p_ballsNum, int p_urnsNum)
    {
        Random rand = new Random();
        int[] l_urns = new int[p_urnsNum];
        for(int i=0; i<p_ballsNum; i++)
            l_urns[rand.nextInt(p_urnsNum)]++;
        int max = 0;
        int empty = 0;
        for(int i=0; i<p_urnsNum; i++) {
            if(l_urns[i] == 0) empty++;
            if(max < l_urns[i]) max = l_urns[i];
        }
        return new Result(max, empty);
    }

    private Result variance(Result[] data, Result expected)
    {
        Result variance = new Result();
        for(Result res : data) {
            variance.maxNumOfElementsInUrn += Math.pow(expected.maxNumOfElementsInUrn - res.maxNumOfElementsInUrn, 2);
            variance.numOfEmptyUrns += Math.pow(expected.numOfEmptyUrns - res.numOfEmptyUrns, 2);
        }
        variance.maxNumOfElementsInUrn /= data.length - 1;
        variance.numOfEmptyUrns /= data.length - 1;
        return variance;
    }

    public Result expected(Result[] data)
    {
        Result l_expected = new Result();
        for(Result result : data)
        {
            l_expected.maxNumOfElementsInUrn += result.maxNumOfElementsInUrn;
            l_expected.numOfEmptyUrns += result.numOfEmptyUrns;
        }
        l_expected.maxNumOfElementsInUrn /= data.length;
        l_expected.numOfEmptyUrns /= data.length;
        return l_expected;
    }

    public Result[] repeatExperiment(int p_ballsNum, int p_urnsNum, int p_experimentsNum)
    {
        Result[] data = new Result[p_experimentsNum];
        for(int i=0; i<p_experimentsNum; i++)
            data[i] = experiment(p_ballsNum, p_urnsNum);
        return data;
    }

    public int theoryEmptyUrnExpected(int n, int k) {
        return (int)(((float)n)*Math.pow((1.0f-(1.0f/(float)n)), k));
    }

    public int theoryEmptyUrnVariance(int n, int k) {
        return (int)(theoryEmptyUrnExpected(n, k) + (n*(n-1))*Math.pow((1.0f-2.0f/(float)n), k) - n*n*Math.pow((1.0f-1.0f/(float)n), 2*k));
    }

    public float theoryMaxExpected(int n, int k) {
        return (float)(Math.log(k)/Math.log(Math.log(k)));
    }
    public float theoryMaxVariance(int n, int k) {
        return (float)(3.0f*Math.log(k)/Math.log(Math.log(k)));
    }


    public float chebyshev(int variance) {
        return (int)(2*Math.sqrt(variance));
    }

    public void createChart(int p_maxBallsNum, int p_urnsNum, int p_repeatsPerGivenParams)
    {
        Chart chartMax = new Chart("Urns and balls", "Max elem in urn");
        Chart chartEmpty = new Chart("Urns and balls", "Empty urns");
        for(int i=0; i<p_maxBallsNum; i++) {
            Result[] data = repeatExperiment(i, p_urnsNum, p_repeatsPerGivenParams);
            Result l_expected = expected(data);
            Result l_variance = variance(data, l_expected);

            chartMax.dataset.addValue( l_expected.maxNumOfElementsInUrn, "E[maxElement]" , Integer.toString(i) );
            chartMax.dataset.addValue( l_variance.maxNumOfElementsInUrn, "Var[maxElement]" , Integer.toString(i) );
            chartMax.dataset.addValue( theoryMaxExpected(p_urnsNum, i), "Theory E[maxElement]" , Integer.toString(i) );
            chartMax.dataset.addValue( theoryMaxVariance(p_urnsNum, i), "Theory Var[maxElement]" , Integer.toString(i) );

            chartEmpty.dataset.addValue( l_expected.numOfEmptyUrns, "E[emptyUrns]" , Integer.toString(i) );
            chartEmpty.dataset.addValue( l_variance.numOfEmptyUrns, "Var[emptyUrns]" , Integer.toString(i) );
            chartEmpty.dataset.addValue( theoryEmptyUrnExpected(p_urnsNum, i), "Theory E[emptyUrns]" , Integer.toString(i) );
            chartEmpty.dataset.addValue( theoryEmptyUrnVariance(p_urnsNum, i), "Theory Var[emptyUrns]" , Integer.toString(i) );
            chartEmpty.dataset.addValue( l_expected.numOfEmptyUrns - chebyshev(l_variance.numOfEmptyUrns), "odchylenie1", Integer.toString(i));
            chartEmpty.dataset.addValue( l_expected.numOfEmptyUrns + chebyshev(l_variance.numOfEmptyUrns), "odchylenie2", Integer.toString(i));
//            for(Result dat : data) {
//                chartMax.dataset.addValue(dat.maxNumOfElementsInUrn, "Result", Integer.toString(i));
//                chartEmpty.dataset.addValue(dat.numOfEmptyUrns, "Result", Integer.toString(i));
//            }
        }
        chartMax.render();
        chartEmpty.render();
    }
}
