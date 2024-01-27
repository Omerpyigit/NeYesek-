package com.example.bugunneyesem.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bugunneyesem.filters.FilterFoodAdmin
import com.example.bugunneyesem.models.ModelTarif
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.activities.TarifDetayActivityAdmin
import com.example.bugunneyesem.databinding.SatirTarifAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class TarifAdapterAdmin :RecyclerView.Adapter<TarifAdapterAdmin.HolderTarifAdmin>, Filterable {
    private lateinit var firebaseAuth: FirebaseAuth
    private var context: Context
    //tarifleri tutmak için arraylist
    private lateinit var binding: SatirTarifAdminBinding
    public var foodArrayList: ArrayList<ModelTarif>
    private var filterList:ArrayList<ModelTarif>

    //filter
    private var filter: FilterFoodAdmin? = null

    constructor(context: Context, foodArrayList: ArrayList<ModelTarif>) : super() {
        this.context = context
        this.foodArrayList = foodArrayList
        this.filterList = foodArrayList
    }





    inner class HolderTarifAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgView = binding.imgView
        //val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val kategoriTv = binding.kategoriTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTarifAdmin {
        //binding ve inflate tarif satırları için
        binding = SatirTarifAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderTarifAdmin(binding.root)

    }

    override fun getItemCount(): Int {
        return foodArrayList.size
    }

    override fun onBindViewHolder(holder: HolderTarifAdmin, position: Int) {

        //veriyi alma
        val model = foodArrayList[position]
        val imgId = model.id
        val kategoriId = model.kategoriId
        val baslik = model.baslik
        val aciklama = model.aciklama
        val img = model.img
        val timestamp = model.timestamp
        val date = MyApplication.formatTimeStamp(timestamp)

        //veriyi aktarma
        holder.titleTv.text = baslik
        holder.descriptionTv.text = aciklama
        holder.dateTv.text = date

        MyApplication.loadKategori(kategoriId, holder.kategoriTv)
        //HATA VAR
        /*
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(img)
        val localfile = File.createTempFile("tempImage","jpg")
        ref.metadata
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                holder.imgView.setImageBitmap(bitmap)


            }
            .addOnFailureListener{e->

            }
        */
        Picasso.get()

            .load(foodArrayList[position].img)
            .into(holder.imgView)

        //tarife tıklandığında detaylar sayfasına gitmesi için
        holder.itemView.setOnClickListener{
            val intent = Intent(context, TarifDetayActivityAdmin::class.java)
            intent.putExtra("tarifId", imgId)
            context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterFoodAdmin(filterList, this)
        }

        return filter as FilterFoodAdmin
    }
}