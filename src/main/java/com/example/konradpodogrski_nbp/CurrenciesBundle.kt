package com.example.konradpodogrski_nbp

import java.io.Serializable

class CurrenciesBundle: Serializable{
    var currenciesDetails: Array<CurrencyDetails>? = null
        get() = field
        set(value) {
            field = value
        }

}