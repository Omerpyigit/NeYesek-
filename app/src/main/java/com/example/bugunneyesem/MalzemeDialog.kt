package com.example.bugunneyesem

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MalzemeDialog(
    private val _context: Context,
    private val data: Array<String?>

): DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertBuilder = AlertDialog.Builder(_context)
            var checkedIndex = ArrayList<Int>()

            alertBuilder.setTitle("Malzeme Seçiniz")
            alertBuilder.setMultiChoiceItems(data,null,DialogInterface.OnMultiChoiceClickListener{
                _, index, checked ->
                if (checked){
                    checkedIndex.add(index)
                } else if (checkedIndex.contains(index)){
                    checkedIndex.remove(index)
                }

            })
            alertBuilder.setPositiveButton("EKLE",DialogInterface.OnClickListener{
                dialog, id ->
            })

            alertBuilder.create()

        } ?: throw  IllegalStateException("NaN")
    }
}