package com.alfrosoft.fastchatapp.modelclasses



class ChatList {

    private var id:String?=""

    constructor()

    constructor(id: String?) {
        this.id = id
    }

    fun getID():String?{
        return  id
    }

    fun setID(id:String?)
    {
        this.id=id!!
    }

}