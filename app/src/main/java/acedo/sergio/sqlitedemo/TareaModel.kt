package acedo.sergio.sqlitedemo

import java.io.Serializable
import java.util.*

data class TareaModel (
    var id: Int = -1,
    var name: String = "",
    var Descripcion: String = "",
    var posicion: Int = -1,
    var estado: String = "Pendiente"
) :Serializable
