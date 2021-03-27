package com.example.konradpodogrski_nbp

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.blongho.country_data.World

class CurrenciesListAdapter(var dataSet: Array<CurrencyDetails> ): RecyclerView.Adapter<CurrenciesListAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    var context: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyCodeTextView: TextView
        val rateTextView: TextView
        val increaseView: ImageView
        val flagImageView: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            currencyCodeTextView = view.findViewById(R.id.currency)
            rateTextView = view.findViewById(R.id.rate)
            flagImageView = view.findViewById(R.id.flag)
            increaseView = view.findViewById(R.id.increase)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.currency_row, viewGroup, false)

        return ViewHolder(view)
    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currency = dataSet[position]
        val cur = currency.currencyCode.toUpperCase();

        val country = World.getCountryFrom(Countries.codeToCountry.getOrDefault(cur, "xx"));

        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.rateTextView.text = currency.rate.toString()

        viewHolder.increaseView.setImageResource(if(currency.increase) R.drawable.increase else R.drawable.decrease)
        viewHolder.flagImageView.setImageResource(World.getFlagOf(country.id))

        viewHolder.itemView.setOnClickListener{ _ -> goToDetails(viewHolder.itemView.context, currency.currencyCode, currency.table)}

    }

    private fun goToDetails(ctx: Context ,currencyCode: String, table: String = "A") {
        var intent = Intent(ctx, CurrencyChartActivity::class.java);
        intent.putExtra("table", table)
        intent.putExtra("currencyCode", currencyCode)

        ctx.startActivity(intent);
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

