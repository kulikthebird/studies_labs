package Chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;


public class Chart extends ApplicationFrame
{
    public DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

    public void render()
    {
        this.pack();
        RefineryUtilities.centerFrameOnScreen( this );
        this.setVisible( true );
    }


    public Chart( String applicationTitle , String chartTitle ) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "X","Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane(chartPanel);
    }
}
