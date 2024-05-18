package com.example.bugunneyesem.activities

import FetchYemeklerTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.MalzemeDialog
import com.example.bugunneyesem.MalzemeDialogListener
import com.example.bugunneyesem.databinding.ActivityNePisirsemBinding
import com.example.bugunneyesem.models.ModelMalzeme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NePisirsem : AppCompatActivity(), MalzemeDialogListener {
    private lateinit var binding: ActivityNePisirsemBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var malzemelerArrayList: ArrayList<ModelMalzeme>

    private val API_URL = "http://127.0.0.1:5000/oneri-yemekler"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNePisirsemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        loadMalzemeler()



        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.malzemelerTv.setOnClickListener {
            malzemePickDialog()
        }
        binding.kaydetBut1.setOnClickListener {
            val fetchYemeklerTask = FetchYemeklerTask(this, binding.sonucTV)
            val url = "http://192.168.1.102:5000/oneri-yemekler"
            val malzemeler = listOf("çilek", "yumurta", "şeker","süt")
            fetchYemeklerTask.execute(url, malzemeler)
        }
    }
    private var id = ""
    private var name = ""

    private fun loadMalzemeler(){
        malzemelerArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Malzemeler")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                malzemelerArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelMalzeme::class.java)

                    malzemelerArrayList.add(model!!)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        } )
    }
    private var selectedMalzemeId = ""
    private var selectedMalzemeName = ""
    /*
         val malzemelerArray = arrayOfNulls<String>(malzemelerArrayList.size)
        for(i in malzemelerArrayList.indices){
            malzemelerArray[i] = malzemelerArrayList[i].name
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kategori Seç")
            .setItems(malzemelerArray){dialog, which->

                selectedMalzemeName = malzemelerArrayList[which].name
                selectedMalzemeId = malzemelerArrayList[which].id
                //textview de göster
                binding.malzemelerTv.text = selectedMalzemeName


            }
            .show()
     */
    private fun malzemePickDialog() {
        val malzemelerArray = arrayOfNulls<String>(malzemelerArrayList.size)

        for(i in malzemelerArrayList.indices){
            malzemelerArray[i] = malzemelerArrayList[i].name
        }
        var data = malzemelerArray


        val builder = MalzemeDialog(this,data)
        builder.setMalzemeDialogListener(this)
        builder.show(supportFragmentManager,"")
        builder.isCancelable = false
        val selectedMalzemeler = MalzemeDialog.selectedMalzemeler

    }
    override fun onMalzemelerSecildi(selectedMalzemeler: List<String>) {
        // Seçilen malzemeleri burada kullanabilirsiniz
        // Örneğin, bir listede görüntülemek veya işlemek için
        // Seçilen malzemeleri bir listeye atayarak kullanabilirsiniz
        val selectedMalzemeList = ArrayList<String>()
        selectedMalzemeList.addAll(selectedMalzemeler)
    }









    /*
    val malzemeBinding = DialogMalzemeBinding.inflate(LayoutInflater.from(this))


    val malzemelerArray = arrayOfNulls<String>(malzemelerArrayList.size)
    for(i in malzemelerArrayList.indices){
        malzemelerArray[i] = malzemelerArrayList[i].name
    }

    val builder = AlertDialog.Builder(this,R.style.CustomDialog)
    var checkedIndex = ArrayList<Int>()
    builder.setView(malzemeBinding.root)
    builder.setMultiChoiceItems(R.array.com_google_android_gms_fonts_certs, null,DialogInterface.OnMultiChoiceClickListener{
        _,index,checked ->
        if(checked){
            checkedIndex.add(index)
        } else if(checkedIndex.contains(index)){
            checkedIndex.remove(index)
        }
    })
    builder.create()
    */
}