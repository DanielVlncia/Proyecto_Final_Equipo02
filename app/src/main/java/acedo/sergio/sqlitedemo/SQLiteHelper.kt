package acedo.sergio.sqlitedemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import androidx.core.database.getIntOrNull
import java.lang.Exception


class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATEBASE_NAME, null, DATASE_VERSION) {
    companion object {
        private const val DATASE_VERSION = 1
        private const val DATEBASE_NAME = "student.db"

        private const val TBL_STUDENT = "tbl_student"
        private const val ID = "id"
        private const val NAME = "name"
        private const val EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = ("CREATE TABLE " + TBL_STUDENT + "("
                + ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
                + EMAIL + " TEXT" + ")")

        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_STUDENT")
        onCreate(db)

    }

    fun updateStudent(std: StudentModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(EMAIL, std.email)


        val succes = db.update(TBL_STUDENT, contentValues, "id=" + std.id, null)
        db.close()

        return succes
    }


    fun insertStudent(std: StudentModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(EMAIL, std.email)

        val success = db.insert(TBL_STUDENT, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllStudents(): ArrayList<StudentModel> {

        val stdList: ArrayList<StudentModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_STUDENT"
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
        var email: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                email = cursor.getString(cursor.getColumnIndex("email"))

                val std = StudentModel(id = id, name = name, email = email)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        return stdList
    }


    fun deleteStudentbyId(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, id)

        val succes = db.delete(TBL_STUDENT, "id=" + id, null)
        db.close()
        return succes
    }
}