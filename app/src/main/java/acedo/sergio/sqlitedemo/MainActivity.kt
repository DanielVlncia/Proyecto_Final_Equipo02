package acedo.sergio.sqlitedemo

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var edName: EditText
    private lateinit var edEmail: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter :  TareaAdapter? = null

    private var std :  TareaModel? = null


    companion object{
        var TareasList:ArrayList<TareaModel> = ArrayList<TareaModel>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        InitRecyclerView()
        sqliteHelper = SQLiteHelper(this)
        TareasLists()

       getStudents()
        btnAdd.setOnClickListener{ addTarea()}
        btnView.setOnClickListener{
            var intent: Intent = Intent(this,ConsultadeTareas::class.java)
           // getStudents()
            startActivity(intent)
            }

        //Cuando se le presiona a la tarea
        adapter?.setOnClickItem {

            var intent: Intent = Intent(this,TimeronService::class.java)


        edName.setText(it.name)
        edEmail.setText(it.Descripcion)
        std = it
            if(std!!.estado.equals("Pendiente")){
                std?.estado = "En progreso"
                sqliteHelper.updateTarea(std!!)
            }
            intent.putExtra("Tarea", std as Serializable)
            startActivity(intent)
        }

        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }

        adapter?.setOnClickStartItem {


        }



        }

    private fun TareasLists() {
        TareasList = sqliteHelper.getAllStudents()
        TareasList.sort()
    }

    var context: Context? = null
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


        if(name == std?.name  &&  email == std?.Descripcion){
            Toast.makeText(this, "Tarea no actualizado", Toast.LENGTH_SHORT).show()
            return
        }

        if (std== null) return

        val std= TareaModel(id = std!!.id, name  = name , Descripcion = email)
        val status = sqliteHelper.updateTarea(std)

        if (status > -1){
            clearEditText()
            getStudents()
        }else{
            Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getStudents() {
        val stdList   = sqliteHelper.getAllStudents() // Todos los estudiantes
        Log.e("Lista" , "${stdList}" )
        var std :ArrayList<TareaModel> = ArrayList() //  lista auxiliar
        for (Tarea in stdList){

            if(Tarea.estado.equals("Pendiente")){

                std.add(Tarea)

            }
        }
        std.sort()
        adapter?.addItems(std)
    }



    private fun addTarea() {
        val name = edName.text.toString()
        val email = edEmail.text.toString()

        val std :ArrayList<TareaModel> = sqliteHelper.getAllStudents()
        for (Tarea in std){
            if(Tarea.name.equals(name,true)  || Tarea.Descripcion.equals(email,true) ){
                Toast.makeText(this,"Esta tarea ya esta registrada", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if(name.isEmpty() || email.isEmpty()){
            Toast.makeText(this,"Ingrese los datos necesarios", Toast.LENGTH_SHORT).show()
        }else{
            val std = TareaModel(name = name, Descripcion =  email)
            val status = sqliteHelper.insertStudent(std)

            //Check insert succes or not
            if(status >  -1){
             //   Toast.makeText(this,"Tarea anadida", Toast.LENGTH_SHORT).show()
              // Log.e("Que tal" , "${std.id}")
                //Log.e("Que tal" , "${std.posicion}"

                 var tarea = sqliteHelper.getStudentbyName(std.name)
                tarea.posicion = tarea.id
                sqliteHelper.updateTarea(tarea)
                std
                Log.e("Que tal" , "${tarea}")
                clearEditText()
                getStudents()
                TareasList = sqliteHelper.getAllStudents()
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
        adapter = TareaAdapter()
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView )


    }
    val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.RIGHT
    or ItemTouchHelper.END , O){

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
          val fromPosition = viewHolder.adapterPosition//start position
          val toPosition = target.adapterPosition // end position
            val stdList   = TareasList
            //var std :ArrayList<TareaModel> = ArrayList() //  lista auxiliar

            Log.e("Lista Original" , "${stdList}")
            Collections.swap(stdList,fromPosition, toPosition)
            Log.e("Lista Cambiada" , "${stdList}")
            for (tarea in stdList){
                tarea.posicion = stdList.indexOf(tarea)
                sqliteHelper.updateTarea(tarea)
            }


             adapter?.notifyItemMoved(fromPosition,toPosition)
            return false

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int ) {
        val position = viewHolder.adapterPosition
        val stdList   = sqliteHelper.getAllStudents()



        }

    }

    private fun initView() {
        edName = findViewById<EditText>(R.id.edName)
        edEmail = findViewById<EditText>(R.id.edEmail)
        btnAdd = findViewById<Button>(R.id.btnAdd)
        btnView = findViewById<Button>(R.id.btnView)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    }
}





