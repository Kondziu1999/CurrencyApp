package com.example.konradpodogrski_nbp

import java.io.Serializable

data class CurrencyDetails(var currencyCode: String, var rate: Double, var table: String, var increase:Boolean = true): Serializable {

}
