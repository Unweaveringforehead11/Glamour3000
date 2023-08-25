package com.example.ourapplicationglamour3000.models

class ModellClass {

    var id:String = ""
    var itemName:String = ""
    var itemDescription:String = ""
    var itemCategory:String = ""
    var itemCategoryId:String = ""
    var itemNotes:String = ""
    var itemDate:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()

    constructor(
        id: String,
        itemName: String,
        itemDescription: String,
        itemCategory: String,
        itemCategoryId:String ,
        itemNotes: String,
        itemDate: String,
        timestamp: Long,
        uid: String
    ) {
        this.id = id
        this.itemName = itemName
        this.itemDescription = itemDescription
        this.itemCategory = itemCategory
        this.itemCategoryId = itemCategoryId
        this.itemNotes = itemNotes
        this.itemDate = itemDate
        this.timestamp = timestamp
        this.uid = uid
    }
}