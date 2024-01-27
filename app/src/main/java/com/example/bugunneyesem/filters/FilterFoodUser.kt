package com.example.bugunneyesem.filters

import android.widget.Filter
import com.example.bugunneyesem.adapters.TarifAdapterUser
import com.example.bugunneyesem.models.ModelTarif

class FilterFoodUser: Filter {
    //hangisinde aradğımızı seçen filterlist
    var filterList: ArrayList<ModelTarif>

    var adapterImgAdmin: TarifAdapterUser

    constructor(filterList: ArrayList<ModelTarif>, adapterImgAdmin: TarifAdapterUser) : super() {
        this.filterList = filterList
        this.adapterImgAdmin = adapterImgAdmin
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint:CharSequence? = constraint //arama için value
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<ModelTarif>()
            for (i in filteredModels.indices){
                if(filterList[i].baslik.lowercase().contains(constraint)){
                    //aranan değer listedeki değere yakınsa filtered listeye ekle
                    filteredModels.add(filterList[i])
                }
            }
            //filtered listenin değerlerini ve boyutunu döndür
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //boş veya null ise orijinal listeyi geri döndür
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        //filter değişikliklerini uygula
        adapterImgAdmin.foodArrayList = results!!.values as ArrayList<ModelTarif>

        adapterImgAdmin.notifyDataSetChanged()
    }
}