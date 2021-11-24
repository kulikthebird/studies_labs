package QSort;

import java.util.Random;
import Chart.Chart;

public class SortCompare
{
    int[] generateRandomIntArray(int elementNum)
    {
        Random rand = new Random();
        int[] tab = new int[elementNum];
        tab[0] = -0xFFFF;
        for (int j = 1; j < elementNum; j++) tab[j] = rand.nextInt() % 0xFFFF;
        return tab;
    }

    void generateResultForGivenElementNum(int elementNum, int tries, Chart swapsChart, Chart compChart)
    {
        for(int i=0; i<tries; i++) {
            CommonSortFunc qsort = new QuickSort();
            CommonSortFunc dual = new DualPivot();
            int[] tab = generateRandomIntArray(elementNum);
            int[] tab2 = new int[elementNum];
            System.arraycopy(tab, 0, tab2, 0, tab.length);
            qsort.sort(tab);
            dual.sort(tab2);
            swapsChart.dataset.addValue( qsort.swaps , "QSort Swap" , Integer.toString(elementNum) );
            compChart.dataset.addValue( qsort.compares, "QSort Compares" , Integer.toString(elementNum) );
            swapsChart.dataset.addValue( dual.swaps, "QSort.DualPivot Swap" , Integer.toString(elementNum) );
            compChart.dataset.addValue( dual.compares, "QSort.DualPivot Compares" ,Integer.toString(elementNum) );
        }
        swapsChart.dataset.addValue( 0.33*elementNum*java.lang.Math.log(elementNum) - 0.58*elementNum + java.lang.Math.log(elementNum) , "Theory QSort Swap" , Integer.toString(elementNum) );
        compChart.dataset.addValue( 2*elementNum*java.lang.Math.log(elementNum) - 1.51*elementNum + java.lang.Math.log(elementNum) , "Theory QSort Compares" , Integer.toString(elementNum) );
        swapsChart.dataset.addValue( 0.6*elementNum*java.lang.Math.log(elementNum) - 0.08*elementNum + java.lang.Math.log(elementNum), "Theory QSort.DualPivot Swap" , Integer.toString(elementNum) );
        compChart.dataset.addValue( 1.9*elementNum*java.lang.Math.log(elementNum) - 2.46*elementNum + java.lang.Math.log(elementNum), "Theory QSort.DualPivot Compares" ,Integer.toString(elementNum) );
    }

    public void compare(int N, int P)
    {
        Chart chart1 = new Chart("QuickSort", "QuickSortSwaps");
        Chart chart2 = new Chart("QuickSort", "QuickSortCompares");
        for(int i=2 ; i<N; i++)
            generateResultForGivenElementNum(i, P, chart1, chart2);
        chart1.render();
        chart2.render();
    }
}
