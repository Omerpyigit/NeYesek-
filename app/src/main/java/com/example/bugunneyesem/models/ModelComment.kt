package com.example.bugunneyesem.models

class ModelComment {
    var id = ""
    var tarifId = ""
    var timestamp = ""
    var comment = ""
    var uid = ""

    //firebase için boş constructor
    constructor()
    constructor(id: String, tarifId: String, timestamp: String, comment: String, uid: String) {
        this.id = id
        this.tarifId = tarifId
        this.timestamp = timestamp
        this.comment = comment
        this.uid = uid
    }


}