package acedo.sergio.sqlitedemo

import android.content.Intent
import android.os.Build
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

class ConsultadeTareas : AppCompatActivity() {
    private var adapter :  TareaAdapter? = null
    private lateinit var edName: EditText
    private lateinit var edEmail: EditText

    private var std :  TareaModel? = null
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnConsultarTodas: Button
    private lateinit var btnConsultarPendientes: Button
    private lateinit var btnConsultarTerminadas: Button
    private lateinit var btnConsultarEnProgreso: Button
    private lateinit var btnAtras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultade_tareas)
        sqliteHelper = SQLiteHelper(this)
        initView()
        InitRecyclerView()
        try {

            var Valor:String = intent.extras?.get("progreso") as String
            if (Valor == "progreso"){

              //  Toast.makeText(this,"Funciono",Toast.LENGTH_SHORT).show()
                this.getTareasEnProgreso()
            }else{
                getStudents()
            }
        }catch (e: Exception){
            getStudents()
           // Toast.makeText(this,"Trono",Toast.LENGTH_SHORT).show()
        }


        btnAtras.setOnClickListener {
            var intent: Intent = Intent(this,MainActivity::class.java)
            // getStudents()
            startActivity(intent)
        }

        btnConsultarTodas.setOnClickListener{
            getStudents()
        }


        btnConsultarPendientes.setOnClickListener{
            getTareasPendientes()
        }
        btnConsultarTerminadas.setOnClickListener{
            getTareasTerminadas()
        }
       btnConsultarEnProgreso.setOnClickListener{
           getTareasEnProgreso()
       }
        adapter?.setOnClickItem {

            var intent: Intent = Intent(this,TimeronService::class.java)

            std = it
            if(std!!.estado.equals("Pendiente")){
                std?.estado = "En progreso"
                sqliteHelper.updateTarea(std!!)
            }
            intent.putExtra("Tarea", std as Serializable)
            startActivity(intent)
        }
        adapter?.setOnClickDeleteItem {
            deleteTarea(it.id)
        }
    }

    private fun deleteTarea(id: Int) {


            if(id==null) return
            val tar =  sqliteHelper.getTareabyId(id)

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Seguro que desea eliminar la tarea con nombre: ${tar.name}")
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

    private fun getStudents() {
        val stdList   = sqliteHelper.getAllStudents()
        Log.e("pppp" , "${stdList}")
        stdList.sort()
        adapter?.addItems(stdList)
    }
    private fun getTareasPendientes(){
        val stdList   = sqliteHelper.getAllStudents()
        val std :ArrayList<TareaModel> = ArrayList()
        for (Tarea in stdList){
            if(Tarea.estado.equals("Pendiente")){
                std.add(Tarea)
                Log.e("pppp" , "${std.size}")
            }
        }
        std.sort()
        adapter?.addItems(std)
    }

    private fun getTareasTerminadas(){
        val stdList   = sqliteHelper.getAllStudents()
        val std :ArrayList<TareaModel> = ArrayList()
        for (Tarea in stdList){
            if(Tarea.estado.equals("Terminada")){


                std.add(Tarea)

                Log.e("pppp" , "${std.size}")
            }
        }




        std.sort()
        adapter?.addItems(std)
    }



    private fun getTareasEnProgreso(){
        val stdList   = sqliteHelper.getAllStudents()
        val std :ArrayList<TareaModel> = ArrayList()
        for (Tarea in stdList){
            if(Tarea.estado.equals("En progreso")){
                std.add(Tarea)
                Log.e("pppp" , "${std.size}")
            }
        }
        std.sort()
        adapter?.addItems(std)
    }
    val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.RIGHT
            or ItemTouchHelper.END , Build.VERSION_CODES.O
    ){

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition//start position
            val toPosition = target.adapterPosition // end position
            val stdList   = sqliteHelper.getAllStudents()
            Log.e("Lista Original" , "${stdList}")
            Collections.swap(stdList,fromPosition, toPosition)
            Log.e("Lista Cambiada" , "${stdList}")
            for (tarea in stdList){
                tarea.posicion = stdList.indexOf(tarea)
                sqliteHelper.updateTarea(tarea)
            }

        //   val from = stdList[fromPosition]
        //   val to = stdList[toPosition]
        //
        //
        //   from.posicion = fromPosition
        //   to.posicion = toPosition
        //   sqliteHelper.updateTarea(from)
        //   sqliteHelper.updateTarea(to)
        //
            adapter?.notifyItemMoved(fromPosition,toPosition)

            return false

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int ) {
            val position = viewHolder.adapterPosition
            val stdList   = sqliteHelper.getAllStudents()



        }

    }

    private fun InitRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapter()
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView )


    }

    private fun initView() {
        btnAtras = findViewById<Button>(R.id.btnAtras)
        btnConsultarTodas = findViewById<Button>(R.id.btnConsultarTodas)
        btnConsultarPendientes= findViewById<Button>(R.id.btnConsultarPendientes)
        btnConsultarTerminadas= findViewById<Button>(R.id.btnConsultarTerminadas)
        btnConsultarEnProgreso= findViewById<Button>(R.id.btnConsultarEnProgreso)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    }
}