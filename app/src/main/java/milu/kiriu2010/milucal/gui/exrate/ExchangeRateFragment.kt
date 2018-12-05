package milu.kiriu2010.milucal.gui.exrate


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import milu.kiriu2010.milucal.R
import milu.kiriu2010.milucal.entity.ExRateData

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
*/
//private const val ID_EXCHANGE_RATE = "idExchangeRate"


class ExchangeRateFragment : Fragment() {
    /*
    private var param1: String? = null
    private var param2: String? = null
    */

    private var exRateData: ExRateData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            */
            //exRateData = it.getParcelable<ExRateData>(ID_EXCHANGE_RATE)
            val date = it.getString("date") ?: ""
            val base = it.getString("base") ?: ""
            val rateMap: MutableMap<String,Float> = mutableMapOf()
            currencyLst.forEach { key ->
                val currency =  key.substringAfterLast("rateMap")
                Log.d(javaClass.simpleName,"key[$key]cur[$currency]val[${it.getFloat(key)}]")
                //rateMap.put(currency,it.getFloat(key))
                rateMap[currency] = it.getFloat(key)
            }

            exRateData = ExRateData(date,rateMap,base)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exchange_rate, container, false)

        Log.d(javaClass.simpleName,"date[${exRateData?.date}]base[${exRateData?.base}]")
        exRateData?.rateMap?.forEach { key ->
            //val rate = exRateData?.rateMap?.get(key)
            //Log.d(javaClass.simpleName,"currency[$key]rate[${rate}]")
            Log.d(javaClass.simpleName,"currency[$key]")
        }


        return view
    }


    companion object {
        private val currencyLst: MutableList<String> = mutableListOf()

        @JvmStatic
        fun newInstance(exRateData: ExRateData) =
            ExchangeRateFragment().apply {
                arguments = Bundle().apply {
                    /*
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    */
                    //putParcelable(ID_EXCHANGE_RATE,exRateData)

                    putString("date",exRateData.date)
                    putString("base",exRateData.base)
                    exRateData.rateMap.keys.forEach { key ->
                        putFloat("rateMap${key}", exRateData.rateMap.get(key)!! )
                        currencyLst.add("rateMap${key}")
                    }
                }
            }
    }
}
