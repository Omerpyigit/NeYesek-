package com.example.bugunneyesem.models

class ModelKategori {

    var id:String = ""
    var kategori:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()
    constructor(id: String, kategori: String, timestamp: Long, uid: String) {
        this.id = id
        this.kategori = kategori
        this.timestamp = timestamp
        this.uid = uid
    }


}