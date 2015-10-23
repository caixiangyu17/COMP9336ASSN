package leo.unsw.comp9336assn.methods;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.HashMap;

/**
 * Created by LeoPC on 2015/10/15.
 */
public class SeriesManager {
    private int countSeries = 0;
    private int countPoints = 0;
    public PointsGraphSeries<DataPoint> allPointsSeries = new PointsGraphSeries<>();

    public class MySeries {
        public int id;
        public PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();


        public MySeries(int id) {
            this.id = id;
        }
    }

    private MySeries createSeries() {
        countSeries++;
        return new MySeries(countSeries);
    }

    private HashMap<String, MySeries> seriesDict = new HashMap<>();

    public void push(String ssid, int level, int color) {
        countPoints++;
        DataPoint tempPoint = new DataPoint(countPoints, level);
        allPointsSeries.appendData(tempPoint, false, 100);
        MySeries mySeries;
        if (seriesDict.containsKey(ssid)) {
            mySeries = seriesDict.get(ssid);
        } else {
            mySeries = createSeries();
        }
        DataPoint point = new DataPoint(mySeries.id, level);
        mySeries.series.appendData(point, false, 100);
        mySeries.series.setSize(10);
        mySeries.series.setColor(color);
        seriesDict.put(ssid, mySeries);
    }


    public HashMap<String, MySeries> getSeriesDict() {
        return seriesDict;
    }

    public PointsGraphSeries<DataPoint> getAllPointsSeries() {
        allPointsSeries.setSize(10);
        return allPointsSeries;
    }
}
