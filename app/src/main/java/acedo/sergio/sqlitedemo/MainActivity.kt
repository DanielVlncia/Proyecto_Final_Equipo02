package acedo.sergio.sqlitedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var edName: EditText
    private lateinit var edEmail: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button

    private lateinit var btnUpdate: Button

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter :  StudentAdapter? = null

    private var std :  StudentModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        InitRecyclerView()
        sqliteHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener{ addStudent()}
        btnView.setOnClickListener{ getStudents() }
        btnUpdate.setOnClickListener{ UpdateStudent() }
        adapter?.setOnClickItem { Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()

        edName.setText(it.name)
        edEmail.setText(it.email)
        std = it
        }

        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }
        }

    private fun deleteStudent(id: Int) {
        if(id==null) return


        val builder = AlertDialog.Builder(this)
        builder.setMessage("Seguro que desea eliminar esta Tarea")
        builder.setCancelable(true)
        builder.setPositiveButton("Si"){dialog, _ ->
            sqliteHelper.deleteStudentbyId(id)
            getStudents()
            dialog.dismiss()

        }
        builder.setNegativeButton("No"){dialog, _ ->

            dialog.dismiss()

        }

        val alert = builder.create()
        alert.show()

    }

    private fun UpdateStudent() {
        val name = edName.text.toString()
        val email = edEmail.text.toString()


        if(name == std?.name  &&  email == std?.email){
            Toast.makeText(this, "Tarea no actualizado", Toast.LENGTH_SHORT).show()
            return
        }

        if (std== null) return

        val std= StudentModel(id = std!!.id, name  = name , email = email)
        val status = sqliteHelper.updateStudent(std)

        if (status > -1){
            clearEditText()
            getStudents()
        }else{
            Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getStudents() {
        val stdList   = sqliteHelper.getAllStudents()
        Log.e("pppp" , "${stdList.size}")

        adapter?.addItems(stdList)
    }



    private fun addStudent() {
        val name = edName.text.toString()

        val email = edEmail.text.toString()

        if(name.isEmpty() || email.isEmpty()){
            Toast.makeText(this,"Ingrese los datos necesarios", Toast.LENGTH_SHORT).show()
        }else{
            val std = StudentModel(name = name, email =  email)
            val status = sqliteHelper.insertStudent(std)
            //Check insert succes or not
            if(status >  -1){
                Toast.makeText(this,"Tarea anadida", Toast.LENGTH_SHORT).show()
                clearEditText()
                getStudents()


            }else{
                Toast.makeText(this,"Tarea no anadida", Toast.LENGTH_SHORT).show()
            }
        }

        }

    private fun clearEditText() {
        edEmail.setText("")
        edName.setText("")
        edName.requestFocus()
    }

    private fun InitRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter()
        recyclerView.adapter = adapter
    }
    private fun initView() {
        edName = findViewById<EditText>(R.id.edName)
        edEmail = findViewById<EditText>(R.id.edEmail)
        btnAdd = findViewById<Button>(R.id.btnAdd)
        btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnView = findViewById<Button>(R.id.btnView)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    }
}



