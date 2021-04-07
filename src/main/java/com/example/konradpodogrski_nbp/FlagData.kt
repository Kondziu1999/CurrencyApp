package com.example.konradpodogrski_nbp

import android.content.Context
import com.blongho.country_data.Country
import com.blongho.country_data.World

object FlagData {
    private lateinit var countries: List<Country>

    fun init(context: Context){
        World.init(context)
        countries = World.getAllCountries().distinctBy { i -> i.currency.code }
    }

    fun getFlag(currencyCode: String): Int{

        return when (currencyCode){
            "USD" -> R.drawable.us
            "EUR" -> R.drawable.eu
            "GBP" -> R.drawable.gb
            "HKD" -> R.drawable.hk
            "IDR" -> R.drawable.id
            "NZD" -> R.drawable.nz
            "CHF" -> R.drawable.ch
            "STN" -> R.drawable.st
            "ZWL" -> R.drawable.zw
            "XPF" -> R.drawable.pf
            "GIP" -> R.drawable.gi
            "GHS" -> R.drawable.gh
            "ZMW" -> R.drawable.zm
            "ERN" -> R.drawable.er
            "TMT" -> R.drawable.tm
            "MRU" -> R.drawable.mr
            "MOP" -> R.drawable.mo
            "BYN" -> R.drawable.by
            "WST" -> R.drawable.ws
            "INR" -> R.drawable.`in`
            else -> countries.find { i -> i.currency.code == currencyCode}?.flagResource ?: World.getWorldFlag()
        }
    }
}