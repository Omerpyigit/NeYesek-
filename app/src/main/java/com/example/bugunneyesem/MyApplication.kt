package com.example.bugunneyesem

import android.app.Application
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.bugunneyesem.databinding.SatirTarifAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.TimeZone


class MyApplication: Application(){
    private lateinit var binding: SatirTarifAdminBinding
    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        //timestamp'i kullanışlı biçime çevirmek için static bir method açtık

        fun formatTimeStamp(timestamp: Long) : String{
            val timeZone: TimeZone = TimeZone.getTimeZone("GMT+03")
            val cal = Calendar.getInstance(timeZone)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun loadKategori(kategoriId: String, kategoriTv: TextView){
            //firebase den kategori id ile kategori bilgilerini alma
            val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
            ref.child(kategoriId)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val kategori:String  = "${snapshot.child("kategori").value}"
                        kategoriTv.text = kategori
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                } )

        }

        /*
        fun loadImg(imgUrl: String, imgTitle: String, imgView: ImageView) : Bitmap{
            val TAG = "IMG_UPLOAD_TAG"
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl)
            val localfile = File.createTempFile("tempImage","jpg")
            ref.metadata
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)


                }
                .addOnFailureListener{e->

                }

        } */

        fun incrementTarifViewCount(tarifId: String){
            val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
            ref.child(tarifId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //mevcut görüntüleme sayısını al
                        var viewsCount = "${snapshot.child("viewsCount").value}"

                        if (viewsCount== "" || viewsCount =="null"){
                            viewsCount = "0";
                        }
                        //görüntülemeyi 1 arttır
                        val newViewsCount = viewsCount.toLong() + 1

                        //güncellemeyi veritabanına gönder
                        val hashMap = HashMap<String, Any>()
                        hashMap["viewsCount"] = newViewsCount

                        val dbRef = FirebaseDatabase.getInstance().getReference("Tarifler")
                        dbRef.child(tarifId)
                            .updateChildren(hashMap)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }

        public fun removeFavorite(context: Context, tarifId: String){
            val TAG = "FAVORIDEN_SIL_TAG"
            Log.d(TAG, "removeFavorite: Favoriden siliniyor")

            val firebaseAuth = FirebaseAuth.getInstance()

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Favoriler").child(tarifId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(TAG, "removeFavorite: Favoriden silindi")
                    Toast.makeText(context,"Tarif Favorilerden Kaldırıldı", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {e->
                    Log.d(TAG, "removeFavorite: Favoriden silinemedi ${e.message} ")
                    Toast.makeText(context,"Favori silme başarısız${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }



}