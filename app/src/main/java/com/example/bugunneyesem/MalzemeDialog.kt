package com.example.bugunneyesem

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


interface MalzemeDialogListener {
    fun onMalzemelerSecildi(selectedMalzemeler: List<String>)
}
class MalzemeDialog(
    private val _context: Context,
    private val data: Array<String?>


): DialogFragment() {
    private var listener: MalzemeDialogListener? = null
    companion object {
        val selectedMalzemeler = ArrayList<String>()
    }


    fun setMalzemeDialogListener(listener: MalzemeDialogListener) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertBuilder = AlertDialog.Builder(_context)
            var checkedIndex = ArrayList<Int>()

            alertBuilder.setTitle("Malzeme Seçiniz")
            alertBuilder.setMultiChoiceItems(data,null,DialogInterface.OnMultiChoiceClickListener{
                _, index, checked ->
                if (checked){
                    checkedIndex.add(index)
                    selectedMalzemeler.add(data[index]!!)
                } else if (checkedIndex.contains(index)){
                    checkedIndex.remove(index)
                    selectedMalzemeler.remove(data[index]!!)
                }

            })
            alertBuilder.setPositiveButton("EKLE",DialogInterface.OnClickListener{
                dialog, id ->
                listener?.onMalzemelerSecildi(selectedMalzemeler)
            })

            alertBuilder.create()

        } ?: throw  IllegalStateException("NaN")
    }
    fun getSelectedMalzemeler(): List<String> {
        return selectedMalzemeler
    }
}