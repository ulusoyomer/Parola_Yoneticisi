package net.omerulusoy.sifreyoneticisi.SQLiteOperations.SQLiteCreator.Tables

class Account(var name: String,var password: String){
    var id :Int = 0

    constructor(id:Int,name: String,password: String) : this(name,password){
        this.id = id
    }
}