package com.example.konradpodogrski_nbp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.blongho.country_data.World
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {
    var tableAData: LinkedList<CurrencyDetails> = LinkedList<CurrencyDetails>();
    var tableBData: LinkedList<CurrencyDetails> = LinkedList<CurrencyDetails>();

    var isDataFetched: Boolean = false;

    lateinit var infoTextView: TextView;
    lateinit var tableABtn: Button
    lateinit var tableBBtn: Button
    lateinit var goldBtn: Button
    lateinit var converterBtn: Button

    private var tableCounter: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infoTextView = findViewById(R.id.loadingInfo);
        infoTextView.text = "";

        tableABtn = findViewById(R.id.tableA)
        tableBBtn = findViewById(R.id.tableB)
        goldBtn = findViewById(R.id.gold)
        converterBtn = findViewById(R.id.converter)

        tableABtn.setOnClickListener{fetchNbpData({navigateToTableA()})}
        tableBBtn.setOnClickListener{fetchNbpData({navigateToTableB()})}
        goldBtn.setOnClickListener{navigateToGoldChart()}
        converterBtn.setOnClickListener{fetchNbpData({navigateToExchange()})}

        World.init(getApplicationContext())
    }

    private fun switchLoadingInfo() {
        if (infoTextView.text.isEmpty()) {
            infoTextView.text = "Loading..."
            return
        }
        infoTextView.text = ""
    }

    private fun fetchNbpData(callback: () -> Unit) {
        if(!isInternetAvailable()) {
            Toast.makeText(this,"You are offline :(", Toast.LENGTH_LONG).show()
            return
        }

        if(isDataFetched) {
            callback()
            return
        }
        isDataFetched = true;

        switchLoadingInfo();

        val queue = newRequestQueue(applicationContext);
        val urlA = "http://api.nbp.pl/api/exchangerates/tables/A/last/2?format=json"
        val urlB = "http://api.nbp.pl/api/exchangerates/tables/B/last/2?format=json"

        val requestA = JsonArrayRequest(
            Request.Method.GET,urlA, null,
            Response.Listener {
                response ->
                    loadData(response, tableAData,"A")
                    callback()
            },
            Response.ErrorListener{
                repsonse ->
                    tableCounter +=1;

            }
        );

        val requestB = JsonArrayRequest(
            Request.Method.GET,urlB, null,
            Response.Listener {
                    response ->
                loadData(response,tableBData,"B")
                callback()
            },
            Response.ErrorListener{
                    repsonse ->
                tableCounter +=1;
            }
        );

        queue.add(requestA)
        queue.add(requestB)
    }

    private fun navigateToGoldChart() {
        if(!isInternetAvailable()) {
            Toast.makeText(this,"You are offline :(", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, GoldChartActivity::class.java)

        startActivity(intent);
    }

    private fun loadData(respose: JSONArray?, tableData: LinkedList<CurrencyDetails>,tableCode: String) {

        respose?.let {
            var rates = respose.getJSONObject(1).getJSONArray("rates");
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesCount)
            for (i in 0 until ratesCount) {
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getString("mid")
                val currencyObject = CurrencyDetails(currencyCode, currencyRate.toDouble(),tableCode)
                tmpData[i] = currencyObject
            }
            tableData.addAll(tmpData as Array<CurrencyDetails>)
        }
        respose?.let {
            var rates = respose.getJSONObject(0).getJSONArray("rates");
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesCount)
            for (i in 0 until ratesCount) {
                val currencyRate = rates.getJSONObject(i).getString("mid")
                tableData[i].increase = tableData[i].rate >= currencyRate.toDouble()
            }
        }

        tableCounter +=1;
    }

    private fun navigateToTableA(){
        var bundle = CurrenciesBundle();
        bundle.currenciesDetails = tableAData.toTypedArray();
        navigateToCurrencies(bundle)
    }

    private fun navigateToTableB() {
        var bundle = CurrenciesBundle();
        bundle.currenciesDetails = tableBData.toTypedArray();
        navigateToCurrencies(bundle)
    }

    private fun navigateToCurrencies(bundle: CurrenciesBundle) {
        val intent = Intent(this, CurrenciesActivity::class.java)
        intent.putExtra("currencies", bundle)

        startActivity(intent);
    }

    private fun navigateToExchange() {
        if(!isInternetAvailable()) {
            Toast.makeText(this,"You are offline :(", Toast.LENGTH_LONG).show()
            return
        }

        if(tableCounter == 2) {
            var bundle = CurrenciesBundle();
            bundle.currenciesDetails = tableAData.toTypedArray().plus(tableBData.toTypedArray())

            val intent = Intent(this, CurrencyExchangeActivity::class.java)
            intent.putExtra("currencies", bundle)

            startActivity(intent);
        }
    }

    fun isInternetAvailable(context: Context = this): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}