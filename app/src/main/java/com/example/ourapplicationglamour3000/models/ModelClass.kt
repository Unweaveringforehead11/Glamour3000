package com.example.ourapplicationglamour3000.models

class ModelClass {

    var id:String = ""
    var categoryTitle:String = ""
    var categoryGoal:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()
    constructor(
        id: String,
        categoryTitle: String,
        categoryGoal: String,
        timestamp: Long,
        uid: String
    ) {
        this.id = id
        this.categoryTitle = categoryTitle
        this.categoryGoal = categoryGoal
        this.timestamp = timestamp
        this.uid = uid
    }


}