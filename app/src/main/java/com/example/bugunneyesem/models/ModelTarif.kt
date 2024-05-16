package com.example.bugunneyesem.models

class ModelTarif {

    //değişkenler
    var uid:String = ""
    var id:String = ""
    var baslik:String = ""
    var aciklama:String = ""
    var malzemeler:String = ""
    var tarifDetay:String = ""
    var kategoriId:String = ""
    var img:String = ""
    var timestamp:Long = 0
    var viewsCount:Long = 0
    var favoriler:Long = 0
    var isFavorite = false

    //boş constructor firebase ici
    constructor()


    //paremetreli
    constructor(
        uid: String,
        id: String,
        baslik: String,
        aciklama: String,
        malzemeler: String,
        tarifDetay: String,
        kategoriId: String,
        img: String,
        timestamp: Long,
        viewsCount: Long,
        favoriler: Long,
        isFavorite: Boolean
    ) {
        this.uid = uid
        this.id = id
        this.baslik = baslik
        this.aciklama = aciklama
        this.malzemeler = malzemeler
        this.tarifDetay = tarifDetay
        this.kategoriId = kategoriId
        this.img = img
        this.timestamp = timestamp
        this.viewsCount = viewsCount
        this.favoriler = favoriler
        this.isFavorite = isFavorite
    }

    //constructor

}