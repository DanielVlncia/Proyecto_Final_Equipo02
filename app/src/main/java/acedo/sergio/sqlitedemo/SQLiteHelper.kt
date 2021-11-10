package acedo.sergio.sqlitedemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception


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
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = ("CREATE TABLE " + TAREAS_POMODORO + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + DESCRIPCION + " TEXT," + ESTADO + " TEXT" + ")")

        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TAREAS_POMODORO")
        onCreate(db)

    }

    fun updateStudent(std: TareaModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, std.name)
        contentValues.put(DESCRIPCION, std.Descripcion)
        contentValues.put(ESTADO, std.estado)


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

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                Descripcion = cursor.getString(cursor.getColumnIndex("email"))
                estado = cursor.getString(cursor.getColumnIndex("estado"))

                val std = TareaModel(id = id, name = name, Descripcion = Descripcion, estado = estado)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        return stdList
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