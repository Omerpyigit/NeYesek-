package com.example.bugunneyesem.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.activities.TarifDetayActivityAdmin
import com.example.bugunneyesem.databinding.SatirTarifUserBinding
import com.example.bugunneyesem.filters.FilterFoodAdmin
import com.example.bugunneyesem.filters.FilterFoodUser
import com.example.bugunneyesem.models.ModelTarif
import com.squareup.picasso.Picasso

class TarifAdapterUser : RecyclerView.Adapter<TarifAdapterUser.HolderTarifUser>, Filterable {

    private var context: Context
    //tarifleri tutmak için array list
    public var foodArrayList: ArrayList<ModelTarif>
    //filtrelenmiş tarifleri tutmak için arraylist
    public var filterList: ArrayList<ModelTarif>
    private lateinit var binding: SatirTarifUserBinding

    private var filter: FilterFoodUser? = null

    constructor(context: Context, foodArrayList: ArrayList<ModelTarif>) : super() {
        this.context = context
        this.foodArrayList = foodArrayList
        this.filterList = foodArrayList
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTarifUser {
        binding = SatirTarifUserBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderTarifUser(binding.root)
    }



    override fun onBindViewHolder(holder: HolderTarifUser, position: Int) {
        //get data set data ve handle click(button)
        val model = foodArrayList[position]
        val imgId = model.id
        val kategoriId = model.kategoriId
        val baslik = model.baslik
        val aciklama = model.aciklama
        val img = model.img
        val timestamp = model.timestamp
        val uid = model.uid
        val date = MyApplication.formatTimeStamp(timestamp)
        val viewsCount = model.viewsCount

        holder.titleTv.text = baslik
        holder.aciklamaTv.text = aciklama
        holder.dateTv.text = date
        holder.goruntulemeTv.text = viewsCount.toString()

        MyApplication.loadKategori(kategoriId, holder.kategoriTv)

        Picasso.get()
            .load(foodArrayList[position].img)
            .into(holder.imgView)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, TarifDetayActivityAdmin::class.java)
            intent.putExtra("tarifId", imgId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return foodArrayList.size
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterFoodUser(filterList, this)
        }

        return filter as FilterFoodAdmin
    }


    //satir_tarif_user için ViewHolder
    inner class HolderTarifUser(itemView: View): RecyclerView.ViewHolder(itemView){
        //ui değişkenleri

        var imgView: ImageView = binding.imgView
        //var progressBar = binding.progressBar
        var titleTv: TextView = binding.titleTv
        var aciklamaTv: TextView = binding.aciklamaTv
        var kategoriTv: TextView = binding.kategoriTv
        var dateTv: TextView = binding.dateTv
        var goruntulemeTv: TextView = binding.goruntulemeTv
    }
}