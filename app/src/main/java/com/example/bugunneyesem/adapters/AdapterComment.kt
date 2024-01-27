package com.example.bugunneyesem.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bugunneyesem.MyApplication
import com.example.bugunneyesem.R
import com.example.bugunneyesem.databinding.SatirYorumBinding
import com.example.bugunneyesem.models.ModelComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterComment: RecyclerView.Adapter<AdapterComment.HolderComment>{
    val context: Context

    //arraylist to hold comments
    val commentArrayList: ArrayList<ModelComment>

    //view binding for satir_yorum.xml
    private lateinit var binding: SatirYorumBinding

    private lateinit var firebaseAuth: FirebaseAuth

    constructor(context: Context, commentArrayList: ArrayList<ModelComment>) {
        this.context = context
        this.commentArrayList = commentArrayList

        firebaseAuth = FirebaseAuth.getInstance()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        binding = SatirYorumBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderComment(binding.root)
    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {
        //----Get data,Set data ve tıklama işlemleri----
        //get data
        val model = commentArrayList[position]

        val id = model.id
        val tarifId = model.tarifId
        val comment = model.comment
        val uid = model.uid
        val timestamp = model.timestamp

        val date = MyApplication.formatTimeStamp(timestamp.toLong())

        holder.dateTv.text = date
        holder.commentTv.text = comment
        
        loadUserDetails(model, holder)

        holder.itemView.setOnClickListener {
            if(firebaseAuth.uid == uid){
                deleteCommentDialog(model, holder)
            }
        }

    }

    private fun deleteCommentDialog(model: ModelComment, holder: AdapterComment.HolderComment) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Yorumu Sil")
            .setMessage("Yorumu silmek istediğinden emin misin?")
            .setPositiveButton("SİL"){d,e->
                val tarifId = model.tarifId
                val commentId = model.id

                val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
                ref.child(tarifId).child("Yorumlar").child(commentId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Yorumunuz Silindi", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        Toast.makeText(context, "Yorum silme başarısız ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("İPTAL"){d,e->
                d.dismiss()
            }
            .show()
    }

    private fun loadUserDetails(model: ModelComment, holder: AdapterComment.HolderComment) {

        val uid = model.uid
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("kadi").value}"
                    val profilPhoto = "${snapshot.child("profilPhoto").value}"

                    holder.nameTv.text = name
                    try{
                        Glide.with(context)
                            .load(profilPhoto)
                            .placeholder(R.drawable.ic_profile_gray)
                            .into(holder.profilteTv)

                    }
                    catch(e:Exception){
                        holder.profilteTv.setImageResource(R.drawable.ic_profile_gray)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    //ViewHolder for satir_yorum.xml
    inner class HolderComment(itemView: View): RecyclerView.ViewHolder(itemView){
        //ui views init
        val profilteTv = binding.profileIv
        val nameTv = binding.nameTv
        val dateTv = binding.dateTv
        val commentTv = binding.commentTv

    }


}