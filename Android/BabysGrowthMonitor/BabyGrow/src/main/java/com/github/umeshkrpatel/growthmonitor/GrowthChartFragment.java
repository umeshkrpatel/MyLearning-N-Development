package com.github.umeshkrpatel.growthmonitor;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.umeshkrpatel.growthmonitor.data.ChartData;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.IHCPercentileData;
import com.github.umeshkrpatel.growthmonitor.data.IHPercentileData;
import com.github.umeshkrpatel.growthmonitor.data.IWPercentileData;

import java.util.ArrayList;

/**
 * Created by umpatel on 1/25/2016.
 */
public class GrowthChartFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "GrowthChartFragment";
    private static final String ARG_X_AXIS = "x_axis";
    private static final String ARG_Y_AXIS = "y_axis";
    private int mKidsId = 0, mKidsGen = 0;
    private Long mKidsDob = System.currentTimeMillis();
    private static ArrayList<String> mXAxis = new ArrayList<>();

    private CombinedChart mGrowthChart = null;
    private int ixChart;
    private int iyChart;
    public GrowthChartFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GrowthChartFragment newInstance(ChartData.ChartType xAxis, ChartData.ChartType yAxis) {
        GrowthChartFragment fragment = new GrowthChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_X_AXIS, xAxis.ordinal());
        args.putInt(ARG_Y_AXIS, yAxis.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_growth_chart, container, false);
        mGrowthChart = (CombinedChart) rootView.findViewById(R.id.growth_chart);

        mKidsId = BabysInfo.getCurrentBabyId();

        ixChart = getArguments().getInt(ARG_X_AXIS);
        iyChart = getArguments().getInt(ARG_Y_AXIS);
        ChartData.ChartType yChart = ChartData.fromInt(iyChart);
        Cursor c1 = GrowthDataProvider.get()
                .queryTable(IDataInfo.kBabyInfoTable, null, "_id=" + mKidsId,
                        null, null, null, null);
        if (c1 == null || c1.getCount() < 1)
            return rootView;
        if (c1.moveToNext()) {
            mKidsDob = c1.getLong(IDataInfo.INDEX_DOB_DATE);
            mKidsGen = c1.getInt(IDataInfo.INDEX_GENDER);
        }

        mGrowthChart.setDescription("");
        mGrowthChart.setBackgroundColor(Color.WHITE);
        mGrowthChart.setDrawGridBackground(true);
        mGrowthChart.setDrawBarShadow(false);
        mGrowthChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER,
        });
        XAxis xAxis = mGrowthChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateChart();
    }

    private float getYAxisMinPosition(ChartData.ChartType yChart) {
        Integer min = ChartData.minRange();
        switch (yChart) {
            case AGE:
                return 0f;
            case WEIGHT:
                return IWPercentileData.YAxisMin[min];
            case HEIGHT:
                return IHPercentileData.YAxisMin[min];
            case HEADCIRCUM:
                return IHCPercentileData.YAxisMin[min];
            default:
                return 0f;
        }
    }

    private float getYAxisMaxPosition(ChartData.ChartType yChart) {
        Integer max = ChartData.maxRange();
        switch (yChart) {
            case AGE:
                return 53f;
            case WEIGHT:
                return IWPercentileData.YAxisMax[max];
            case HEIGHT:
                return IHPercentileData.YAxisMax[max];
            case HEADCIRCUM:
                return IHCPercentileData.YAxisMax[max];
            default:
                return 53f;
        }
    }

    private ArrayList<String> getXAxisData(ChartData.ChartType xChart) {
        Integer rangeMin = ChartData.minRange();
        Integer rangeMax = ChartData.maxRange();
        mXAxis.clear();
        switch (xChart) {
            case AGE:
                for (int i = ChartData.rangeToMinIndex(rangeMin);
                     i <= ChartData.rangeToMaxIndex(rangeMax);
                     i++) {
                    mXAxis.add(i + "w");
                }
                break;
            case WEIGHT:
                for (float i = (IWPercentileData.YAxisMin[rangeMin] - 1);
                     i < IWPercentileData.YAxisMin[rangeMax] + 1;
                     i += 0.125f ) {
                    mXAxis.add(i + "");
                }
                break;
            case HEIGHT:
                for (float i = IHPercentileData.YAxisMin[rangeMin] - 1;
                     i < IHPercentileData.YAxisMin[rangeMax] + 1;
                     i += 0.25f ) {
                    mXAxis.add(i + "");
                }
                break;
            case HEADCIRCUM:
                for (float i = IHCPercentileData.YAxisMin[rangeMin] - 1;
                     i < IHCPercentileData.YAxisMin[rangeMax] + 1;
                     i += 0.125f ) {
                    mXAxis.add(i + "");
                }
                break;
        }
        Log.d(TAG, "mXAxis size = " + mXAxis.size());
        return mXAxis;
    }

    private LineData generateLineData(ChartData.ChartType yChart) {
        Integer rangeMin = ChartData.minRange();
        Integer rangeMax = ChartData.maxRange();
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        double[][] yData = IWPercentileData.Data[mKidsGen];
        switch (yChart) {
            case WEIGHT:
                yData = IWPercentileData.Data[mKidsGen];
                break;
            case HEIGHT:
                yData = IHPercentileData.Data[mKidsGen];
                break;
            case HEADCIRCUM:
                yData = IHCPercentileData.Data[mKidsGen];
            default:
                break;
        }

        rangeMin = ChartData.rangeToMinIndex(rangeMin);
        rangeMax = ChartData.rangeToMaxIndex(rangeMax);
        for (int ds = 0; ds < 14; ds++) {
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = rangeMin; i < rangeMax; i++) {
                Log.d(TAG, "Current Index = " + (i - rangeMin));
                entries.add(new Entry((float) yData[i][ds], i - rangeMin));
            }
            Log.d(TAG, "Size = " + entries.size());
            LineDataSet set = new LineDataSet(entries, "DataSet#" + (ds+1));
            set.setColor(Color.rgb(138, 46, 47));
            set.setDrawCircles(false);
            set.setLineWidth(ds % 13 == 0 ? 2.0f : 1.0f);
            dataSets.add(set);
        }
        return new LineData(mXAxis, dataSets);
    }

    protected ScatterData generateScatterData(ChartData.ChartType yChart) {
        Integer rangeMin = ChartData.minRange();
        Integer rangeMax = ChartData.maxRange();

        ScatterData scatterData = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<>();
        Cursor c = GrowthDataProvider.get()
                .queryTable(IDataInfo.kGrowthInfoTable, null,
                        IDataInfo.BABY_ID + "=" + mKidsId + " AND "
                        + IDataInfo.DATE + ">="
                            + (mKidsDob + ChartData.rangeToMinIndex(rangeMin)
                                * Utility.kMilliSecondsInDays * 7) + " AND "
                        + IDataInfo.DATE + "<="
                            + (mKidsDob + ChartData.rangeToMaxIndex(rangeMax)
                                * Utility.kMilliSecondsInDays * 7),
                        null, null, null, null);
        if (c == null || c.getCount() <= 0)
            return scatterData;

        int dataIndex = yChart.ordinal() + IDataInfo.INDEX_BABY_ID;
        int i;
        rangeMin = ChartData.rangeToMinIndex(rangeMin);
        while (c.moveToNext()) {
            Float data = c.getFloat(dataIndex);
            Long day = c.getLong(IDataInfo.INDEX_DATE);
            Integer days = Utility.fromMilliSecondsToDays(day - mKidsDob);
            i = (days / 7) - rangeMin;
            Log.d(TAG, "Index " + i + " Day " + day + " data " + data);
            if (i <= 64){
                entries.add(new Entry(data, i));
            }
        }

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set.setColor(Color.BLUE);
        set.setScatterShapeSize(10f);
        set.setValueTextSize(10f);
        scatterData.addDataSet(set);

        return scatterData;
    }

    public void updateChart() {
        ChartData.ChartType xChart = ChartData.fromInt(ixChart);
        ChartData.ChartType yChart = ChartData.fromInt(iyChart);

        YAxis leftAxis = mGrowthChart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue(getYAxisMinPosition(yChart));
        leftAxis.setAxisMaxValue(getYAxisMaxPosition(yChart));

        YAxis rightAxis = mGrowthChart.getAxisRight();
        rightAxis.setStartAtZero(false);
        rightAxis.setAxisMinValue(getYAxisMinPosition(yChart));
        rightAxis.setAxisMaxValue(getYAxisMaxPosition(yChart));

        CombinedData data = new CombinedData(getXAxisData(xChart));

        data.setData(generateLineData(yChart));
        if (Utility.fromMilliSecondsToDays(System.currentTimeMillis() - mKidsDob) > 5 * 365) {
            Toast.makeText(
                    getContext(), "Age crosses to accepted timeline ", Toast.LENGTH_LONG)
                    .show();
        } else {
            data.setData(generateScatterData(yChart));
        }

        mGrowthChart.setData(data);
        mGrowthChart.invalidate();
    }
}