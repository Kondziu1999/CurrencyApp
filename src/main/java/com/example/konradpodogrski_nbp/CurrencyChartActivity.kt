package com.example.konradpodogrski_nbp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.json.JSONObject
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CurrencyChartActivity : AppCompatActivity() {
    lateinit var currentRateView: TextView;
    lateinit var yesterdayRateView: TextView;

    lateinit var chart7: LineChart;
    lateinit var chart30: LineChart;

    private var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var ratesToDisplay: LinkedList<DailyRate> = LinkedList<DailyRate>();

    private lateinit var table: String;
    private lateinit var code: String;

    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_chart)

        queue = Volley.newRequestQueue(applicationContext);

        currentRateView = findViewById(R.id.currentRate)
        yesterdayRateView = findViewById(R.id.yesterdayRate)

        table = intent.getStringExtra("table")
        code = intent.getStringExtra("currencyCode")

        chart7 = findViewById<View>(R.id.chart7) as LineChart
        chart30 = findViewById<View>(R.id.chart30) as LineChart

        fetchNbpData()
    }

    private fun fetchNbpData() {
        var dateNow = LocalDate.now().format(dateFormatter)
        var datePast = LocalDate.now().minusDays(30).format(dateFormatter);

        val url = "http://api.nbp.pl/api/exchangerates/rates/${table.toLowerCase()}/${code.toLowerCase()}/${datePast}/${dateNow}?format=json"

        val requestA = JsonObjectRequest(
            Request.Method.GET,url, null,
            Response.Listener {
                    response ->
                loadData(response)
            },
            Response.ErrorListener{
                    repsonse ->
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            });

        queue?.add(requestA)
    }

    private fun loadData(respose: JSONObject?) {
        respose?.let {
            var rates = respose.getJSONArray("rates");
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<DailyRate>(ratesCount)
            for (i in 0 until ratesCount) {
                val rate = rates.getJSONObject(i);
                val no = rate.getString("no")
                val date = rate.getString("effectiveDate")
                val mid = rate.getString("mid")

                tmpData[i] = DailyRate(no, LocalDate.parse(date, dateFormatter), mid.toDouble());
            }
            ratesToDisplay.addAll(tmpData as Array<DailyRate>)
        }
        setupCharts()
    }

    private fun setupCharts() {
        setupTopLabels()

        // Pass copy since default List are mutable.......
        prepareDataForChart(chart30, ratesToDisplay.toTypedArray(), 30);

        var last7Days = ratesToDisplay
                .filter { rate -> rate.effectiveDate.isAfter(ChronoLocalDate.from(LocalDate.now().minusDays(7))) }
                .toTypedArray()

        prepareDataForChart(chart7, last7Days, 7)
    }

    private fun prepareDataForChart(chart: LineChart, data: Array<DailyRate>, days: Int = 30) {
        val X30axis = IntRange(1,days).toList()

        var entries:LinkedList<Entry> = LinkedList<Entry>();

        data.forEachIndexed { index, dailyRate ->
            var entry = Entry(X30axis[index].toFloat(), dailyRate.mid.toFloat())
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

    private fun setupTopLabels() {
        yesterdayRateView.text = "previous rate: ${ratesToDisplay[ratesToDisplay.size - 2].mid}"
        currentRateView.text = "current rate: ${ratesToDisplay.last().mid}"
    }

    private fun adjustChart(chart: LineChart, data: Array<DailyRate>) {
        // Adjust X axis
        var xAxis: XAxis = chart.getXAxis()

        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(data.size.toFloat()); // the axis maximum is 100
        xAxis.setTextColor(Color.BLACK);

        // Adjust Y axis
        var yAxis: YAxis = chart.getAxisLeft();

        var sortedRates = data.sortedByDescending { it.mid };
        var maxY = sortedRates[0].mid.toFloat()
        var minY = sortedRates.last().mid.toFloat()

        yAxis.setTextSize(12f); // set the text size
        yAxis.setAxisMaximum(maxY); // start at zero
        yAxis.setAxisMinimum(minY); // the axis maximum is 100
        yAxis.setTextColor(Color.BLACK);
    }
}