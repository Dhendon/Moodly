/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hendon.moodsy;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidplot.LineRegion;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.hendon.moodsy.data.Mood;
import com.hendon.moodsy.data.MoodDataSource;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class BarPlotExampleActivity extends Activity {

    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    // Create a couple arrays of y-values to plot:
    Number[] series1Numbers;
    // Lists that hold mood data.
    List<Integer> ratings;
    List<String> descriptions;
    List<Calendar> dates;
    private XYPlot plot;
    private XYSeries series1;
    private MyBarFormatter formatter1;
    private MoodDataSource datasource;
    private MyBarFormatter selectionFormatter;
    private TextLabelWidget selectionWidget;
    private Pair<Integer, XYSeries> selection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datasource = new MoodDataSource(this);
        datasource.open();
        if (!fillOutLists()) {
            String emptyListText = "Sorry! Record some moods and check back later";
            Toast.makeText(this, emptyListText, Toast.LENGTH_SHORT).show();
        }

        Number[] ratingsNumberArray = new Number[ratings.size()];
        for (int i = 0; i < ratingsNumberArray.length; i++) {
            ratingsNumberArray[i] = ratings.get(i);
        }

        // TODO: Redo the layout for the graph.
        setContentView(R.layout.bar_plot_example);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        series1Numbers = ratingsNumberArray;
        formatter1 = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        //TODO: Make color change with rating as when made.

        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new SizeMetrics(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, XLayoutStyle.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        //plot.getGraphWidget().setGridPadding(30, 10, 30, 0);
        plot.getGraphWidget().setGridPadding(10,10,10,10);

        plot.setTicksPerDomainLabel(2);

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                int year = (int) (value + 0.5d) / 12;
                int month = (int) ((value + 0.5d) % 12);
                return new StringBuffer(DateFormatSymbols.getInstance().getShortMonths()[month] + " '0" + year);
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
        updatePlot();

    }

    private void updatePlot() {

        // Remove all current series from each plot
        Iterator<XYSeries> iterator1 = plot.getSeriesSet().iterator();
        while (iterator1.hasNext()) {
            XYSeries setElement = iterator1.next();
            plot.removeSeries(setElement);
        }

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Moods");

        // add a new series' to the xyplot:
        plot.addSeries(series1, formatter1);

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        int moodIndex = 0;
        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.getGraphWidget().getGridRect().contains(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (XYSeries series : plot.getSeriesSet()) {
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            moodIndex = i;
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            moodIndex = i;
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if (selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            // TODO: Show title here
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
            Toast.makeText(this, "Mood: " + descriptions.get(moodIndex), Toast.LENGTH_SHORT).show();
        }
        plot.redraw();
    }

    /**
     * Fills out individual lists of Mood properties
     *
     * @return Returns false if there are no recorded moods.
     * Returns true if there are recorded moods
     */
    public boolean fillOutLists() {
        List<Mood> moods = datasource.getAllMoods();
        ratings = new ArrayList<Integer>(moods.size());
        descriptions = new ArrayList<String>(moods.size());
        dates = new ArrayList<Calendar>(moods.size());

        for (Mood mood : moods) {
            ratings.add(mood.getRating());
            descriptions.add(mood.getDescription());
            dates.add(mood.getCreatedDate());
        }

        return moods.size() > 0;
    }

    private enum SeriesDateRange {
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection formatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if(selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}