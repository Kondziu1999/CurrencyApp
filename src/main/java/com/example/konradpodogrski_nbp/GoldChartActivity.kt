package com.example.konradpodogrski_nbp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class GoldChartActivity : AppCompatActivity() {
    private var queue: RequestQueue? = null

    private var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private var goldRates: LinkedList<GoldRate> = LinkedList();

    lateinit var chart30: LineChart;
    lateinit var currentRateView: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_chart)

        queue = Volley.newRequestQueue(applicationContext);

        chart30 = findViewById<View>(R.id.chart30gold) as LineChart
        currentRateView = findViewById(R.id.goldCurrentRate)

        fetchNbpData()
    }

    private fun fetchNbpData() {

        var dateNow = LocalDate.now().format(dateFormatter)
        var datePast = LocalDate.now().minusDays(30).format(dateFormatter);

        val url = "http://api.nbp.pl/api/cenyzlota/${datePast}/${dateNow}?format=json"

        val requestA = JsonArrayRequest(
            Request.Method.GET,url, null,
            Response.Listener {
                    response ->
                    loadData(response)
            },
            Response.ErrorListener{
                    repsonse ->
            });

        queue?.add(requestA)
    }

    private fun loadData(response: JSONArray?) {

        response?.let {
            val ratesCount = response.length()
            val tmpData = arrayOfNulls<GoldRate>(ratesCount)

            for (i in 0 until response.length()) {
                val rate = response.getJSONObject(i);
                val date = rate.getString("data")
                val price = rate.getString("cena")

                tmpData[i] = GoldRate(LocalDate.parse(date, dateFormatter), price.toDouble());
            }
            goldRates.addAll(tmpData as Array<GoldRate>)
        }
        setupChart()
    }

    private fun setupChart() {
        currentRateView.text = goldRates.get(0).price.toString();
        // Pass copy since default List are mutable.......
        prepareDataForChart(chart30, goldRates.toTypedArray(), 30);

    }

    private fun prepareDataForChart(chart: LineChart, data: Array<GoldRate>, days: Int = 30) {
        val X30axis = IntRange(1,days).toList()

        var entries:LinkedList<Entry> = LinkedList<Entry>();

        data.forEachIndexed { index, dailyRate ->
            var entry = Entry(X30axis[index].toFloat(), dailyRate.price.toFloat())
            entries.add(entry);
        }

        var dataSet: LineDataSet = LineDataSet(entries, "rates");
        dataSet.setColor(Color.BLACK);
        dataSet.setValueTextColor(Color.MAGENTA);
        dataSet.valueTextSize = 15f

        var lineData: LineData = LineData(dataSet);
        chart.setData(lineData);

        adjustChart(chart, data)

        chart.invalidate()
    }


    private fun adjustChart(chart: LineChart, data: Array<GoldRate>) {
        // Adjust X axis
        var xAxis: XAxis = chart.getXAxis()

        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(data.size.toFloat()); // the axis maximum is 100
        xAxis.setTextColor(Color.BLACK);

        // Adjust Y axis
        var yAxis: YAxis = chart.getAxisLeft();

        var sortedRates = data.sortedByDescending { it.price };
        var maxY = sortedRates[0].price.toFloat()
        var minY = sortedRates.last().price.toFloat()

        yAxis.setTextSize(12f); // set the text size
        yAxis.setAxisMaximum(maxY); // start at zero
        yAxis.setAxisMinimum(minY); // the axis maximum is 100
        yAxis.setTextColor(Color.BLACK);
    }


}