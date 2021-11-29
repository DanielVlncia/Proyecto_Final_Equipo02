package acedo.sergio.sqlitedemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.util.rangeTo
import java.lang.Exception
import java.sql.Date
import java.sql.Struct


class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATEBASE_NAME, null, DATASE_VERSION) {
    companion object {
        private const val DATASE_VERSION = 1
        private const val DATEBASE_NAME = "tarea.db"

        private const val TAREAS_POMODORO = "tbl_pomodoro"
        private const val ID = "id"
        private const val NAME = "name"
        private const val ESTADO = "estado"
        private const val DESCRIPCION = "email"
        private const val POSICION = "posicion"
        private const val FECHATERMINADA = "fechaTerminada"
        private const val HORATERMINADA = "horaTerminada"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = ("CREATE TABLE " + TAREAS_POMODORO + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + DESCRIPCION + " TEXT," + ESTADO + " INTEGER," + POSICION + " TEXT,"+ FECHATERMINADA + " TEXT,"+ HORATERMINADA +")")

        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TAREAS_POMODORO")
        onCreate(db)
    }

    fun updateTarea(std: TareaModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, std.name)
        contentValues.put(DESCRIPCION, std.Descripcion)
        contentValues.put(ESTADO, std.estado)
        contentValues.put(POSICION, std.posicion)

        contentValues.put(FECHATERMINADA, std.fechaTerminada)

        contentValues.put(HORATERMINADA, std.horaTerminada)

        val succes = db.update(TAREAS_POMODORO, contentValues, "id=" + std.id, null)
        db.close()

        return succes
    }


    fun insertStudent(std: TareaModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, std.name)
        contentValues.put(DESCRIPCION, std.Descripcion)
        contentValues.put(ESTADO, std.estado)
        var tareas = ArrayList<TareaModel>()
        tareas = getAllStudents()


        val success = db.insert(TAREAS_POMODORO, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getTareabyId(id: Int):TareaModel{
        val selectQuery = "SELECT * FROM $TAREAS_POMODORO WHERE $ID = $id"
        val db = this.readableDatabase
        val cursor: Cursor?


        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)

            Log.e("Un catch" , "Pues trono")
            return TareaModel()
        }

        var id: Int
        var name    : String
        var Descripcion: String
        var estado :String
        var posicion :Int
        var fechaTerminada :String
        var horaTerminada :String
        var std:TareaModel = TareaModel()

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                Descripcion = cursor.getString(cursor.getColumnIndex("email"))
                estado = cursor.getString(cursor.getColumnIndex("estado"))
                posicion = cursor.getInt(cursor.getColumnIndex("posicion"))
                fechaTerminada = cursor.getString(cursor.getColumnIndex("fechaTerminada"))
                horaTerminada = cursor.getString(cursor.getColumnIndex("horaTerminada"))
                std = TareaModel(id, name, Descripcion, posicion, estado, fechaTerminada, horaTerminada)
                break

            } while (cursor.moveToNext())
        }

        return std
    }

    @SuppressLint("Range")
    fun getAllStudents(): ArrayList<TareaModel> {

        val stdList: ArrayList<TareaModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TAREAS_POMODORO"
        val db = this.readableDatabase



        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var Descripcion: String
        var estado :String
        var posicion :Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                Descripcion = cursor.getString(cursor.getColumnIndex("email"))
                estado = cursor.getString(cursor.getColumnIndex("estado"))
                posicion = cursor.getInt(cursor.getColumnIndex("posicion"))
                val std = TareaModel(id = id,
                    name = name,
                    Descripcion = Descripcion,
                    estado = estado,posicion = posicion)
                stdList.add(std)


            } while (cursor.moveToNext())
        }
        return stdList
    }

    fun getStudentbyName(Name: String): TareaModel {
      val Tareas =  this.getAllStudents()
        for (tarea in Tareas){
            if (tarea.name.equals(Name)){
                return tarea
            }
        }
       return null!!
    }

    fun deleteStudentbyId(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, id)

        val succes = db.delete(TAREAS_POMODORO, "id=" + id, null)
        db.close()
        return succes
    }
}