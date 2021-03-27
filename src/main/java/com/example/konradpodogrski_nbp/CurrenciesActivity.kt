package com.example.konradpodogrski_nbp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class CurrenciesActivity : AppCompatActivity() {
    lateinit var currenciesDetails: Array<CurrencyDetails>;

    internal lateinit var currenciesListRecyclerView: RecyclerView;
    internal lateinit var adapter: CurrenciesListAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currencies)
        var currenciesDetails = (intent.getSerializableExtra("currencies") as CurrenciesBundle).currenciesDetails

        currenciesListRecyclerView = findViewById(R.id.currenciesListRecyclerView)
        currenciesListRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CurrenciesListAdapter(currenciesDetails.orEmpty() as Array<CurrencyDetails>)
        currenciesListRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }




}