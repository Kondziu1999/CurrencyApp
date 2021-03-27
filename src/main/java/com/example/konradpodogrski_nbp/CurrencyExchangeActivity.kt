package com.example.konradpodogrski_nbp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.Error


class CurrencyExchangeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var adapter: ArrayAdapter<String>;
    lateinit var currencies: Array<CurrencyDetails>;
    lateinit var currenciesCodes: Array<String>;

    lateinit var currenciesDropDown: Spinner;
    lateinit var convertBtn: Button;
    lateinit var currencyTextField: EditText;
    lateinit var outputText: TextView;

    lateinit var radioGrp: RadioGroup;

    var convetToPln: Boolean = true;
    lateinit var selectedCurrency: CurrencyDetails;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curency_exchange)

        currencies = (intent.getSerializableExtra("currencies") as CurrenciesBundle).currenciesDetails!!

        radioGrp = findViewById(R.id.radioGrp)
        radioGrp.check(R.id.toPln)
        currenciesDropDown = findViewById(R.id.currency_select)
        convertBtn = findViewById(R.id.convertBtn)
        currencyTextField = findViewById(R.id.currencyInput)
        outputText = findViewById(R.id.converterOutput)

        convertBtn.setOnClickListener{convertCurrency()}
        currenciesCodes = getCurrenciesCodes(currencies).toTypedArray()

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currenciesCodes )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currenciesDropDown.setAdapter(adapter);

        currenciesDropDown.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCurrency = currencies.find { x -> x.currencyCode == currenciesCodes[position] }!!

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        currenciesDropDown.setSelection(0,true)
    }

    private fun getCurrenciesCodes(currencies: Array<CurrencyDetails>): List<String> {
        return currencies.map { currency -> currency.currencyCode }.sorted();
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.toPln ->
                    if (checked) {
                        convetToPln = true
                    }
                R.id.toOther ->
                    if (checked) {
                        convetToPln = false
                    }
            }
        }
    }

    fun convertCurrency() {
        var result: String;
        var amount: Double? = null

        try {
            amount = (currencyTextField.text.toString()).toDoubleOrNull()
        }
        catch (e: NumberFormatException){
            Toast.makeText(this, "place valid amount", Toast.LENGTH_SHORT).show()
        }

        if (amount == null) {
            Toast.makeText(this, "place valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (convetToPln) {
            result = "${amount*selectedCurrency.rate} PLN"

        }
        else{
            result = "${amount/selectedCurrency.rate} ${selectedCurrency.currencyCode}"
        }

        outputText.text = result
    }
}