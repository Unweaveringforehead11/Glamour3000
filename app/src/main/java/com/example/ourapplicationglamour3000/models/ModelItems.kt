package com.example.ourapplicationglamour3000.models

import com.example.ourapplicationglamour3000.adapters.AdapterItemFavorite

class ModelItems {

    var id:String= ""
    var itemImage:String = ""
    var itemName:String = ""
    var itemDescription:String = ""
    var itemCategory:String = ""
    var itemCategoryId:String = ""
    var itemNotes:String = ""
    var itemDate:String = ""
    var itemCount: Int = 0
    var timestamp:Long = 0
    var uid:String = ""
    var isFavorite = false


    constructor(
        id: String,
        itemImage: String,
        itemName: String,
        itemDescription: String,
        itemCategory: String,
        itemCategoryId: String,
        itemNotes: String,
        itemDate: String,
        itemCount: Int,
        timestamp: Long,
        uid: String,
        isFavorite: Boolean
    ) {
        this.id = id
        this.itemImage = itemImage
        this.itemName = itemName
        this.itemDescription = itemDescription
        this.itemCategory = itemCategory
        this.itemCategoryId = itemCategoryId
        this.itemNotes = itemNotes
        this.itemDate = itemDate
        this.itemCount = itemCount
        this.timestamp = timestamp
        this.uid = uid
        this.isFavorite = isFavorite
    }

    constructor()
}