package acedo.sergio.sqlitedemo

import java.io.Serializable
import java.util.*

data class TareaModel (
    var id: Int = -1,
    var name: String = "",
    var Descripcion: String = "",
    var posicion: Int = -1,
    var estado: String = "Pendiente"
) :Serializable, Comparable<TareaModel> {



    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: TareaModel): Int {
       return  this.posicion - other.posicion
    }
}
