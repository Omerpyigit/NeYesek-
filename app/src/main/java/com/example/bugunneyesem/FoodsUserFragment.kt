package com.example.bugunneyesem

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.bugunneyesem.adapters.TarifAdapterUser
import com.example.bugunneyesem.databinding.FragmentFoodsUserBinding
import com.example.bugunneyesem.models.ModelTarif
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FoodsUserFragment : Fragment {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  binding: FragmentFoodsUserBinding

    public companion object{
        private const val  TAG = "TARIF_USER_TAG"

        public fun newInstance(kategoriId: String, kategori: String, uid: String): FoodsUserFragment{
            val fragment = FoodsUserFragment()

            val args = Bundle()
            args.putString("kategoriId", kategoriId)
            args.putString("kategori", kategori)
            args.putString("uid", uid)
            fragment.arguments = args

            return fragment
        }
    }

    private var kategoriId = ""
    private var kategori = ""
    private var uid = ""

    private lateinit var foodArrayList: ArrayList<ModelTarif>
    private lateinit var adapterFoodUser: TarifAdapterUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //firebaseAuth = FirebaseAuth.getInstance()
        val args = arguments
        if (args != null){
            kategoriId = args.getString("kategoriId")!!
            kategori = args.getString("kategori")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodsUserBinding.inflate(LayoutInflater.from(context), container, false)

        //tarifleri categorical göre yükle
        Log.d(TAG,"onCreateView: Kategori: $kategori")

        if (kategori=="Hepsi"){
            loadAllFoods()
        }
        else if(kategori=="En Çok Görüntülenen"){
            loadMostViewedFoods("viewsCount")

        }
        else{
            loadCategorizedFoods()
        }

        //search
        binding.searchEt.addTextChangedListener { object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try{
                    adapterFoodUser.filter.filter(s)
                }
                catch(e: Exception){
                    Log.d(TAG, "onTextChanged: ARAMA HATASI: ${e.message}")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        } }

        return binding.root
    }
    private fun loadAllFoods(){
        foodArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodArrayList.clear()
                for(ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelTarif::class.java)
                    //listeye ekle
                    foodArrayList.add(model!!)
                }
                //setup adapter
                adapterFoodUser = TarifAdapterUser(context!!, foodArrayList)
                //adapter'ı recyclerview'e bağla
                binding.tariflerRv.adapter = adapterFoodUser

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadMostViewedFoods(orderBy: String){
        foodArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.orderByChild(orderBy).limitToLast(10)// en çok izlenen 10 u alır
            .addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodArrayList.clear()
                for(ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelTarif::class.java)
                    //listeye ekle
                    foodArrayList.add(model!!)
                }
                //setup adapter
                foodArrayList.reverse()
                adapterFoodUser = TarifAdapterUser(context!!, foodArrayList)
                //adapter'ı recyclerview'e bağla
                binding.tariflerRv.adapter = adapterFoodUser

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun loadCategorizedFoods(){
        foodArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.orderByChild("kategoriId").equalTo(kategoriId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodArrayList.clear()
                    for(ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelTarif::class.java)
                        //listeye ekle
                        foodArrayList.add(model!!)
                    }
                    //setup adapter
                    adapterFoodUser = TarifAdapterUser(context!!, foodArrayList)
                    //adapter'ı recyclerview'e bağla
                    binding.tariflerRv.adapter = adapterFoodUser

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}