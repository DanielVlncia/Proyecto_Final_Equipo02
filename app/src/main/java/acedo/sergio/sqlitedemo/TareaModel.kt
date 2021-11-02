package acedo.sergio.sqlitedemo

import java.util.*

data class TareaModel(
    var id: Int = getAutoId(),
    var name: String = "",
    var email: String = ""


) {
    companion object {
        var contador = 0
        fun getAutoId(): Int {
            contador= contador +1
            return  contador
        }
    }

}
