package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables

class Account(var name: String,var password: String,var explanation: String = ""){
    var id :Int = 0

    constructor(id:Int,name: String,password: String,explanation: String = "") : this(name,password,explanation){
        this.id = id
    }
}