package acedo.sergio.sqlitedemo

import acedo.sergio.sqlitedemo.databinding.ActivityTimeronServiceBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt

class TimeronService : AppCompatActivity() {
    private lateinit var binding: ActivityTimeronServiceBinding
    private var  timerStarted =  false
    private lateinit var serviceIntent: Intent
    private lateinit var sqliteHelper: SQLiteHelper
    private var time = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeronServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sqliteHelper = SQLiteHelper(this)

        //Objeto tarea
        var std:TareaModel = intent.extras?.get("Tarea") as TareaModel

        //Asignar los nombres editables
        try {
            binding.TVPomodorosRestantes.setText("4")
            binding.TVnombreTarea.setText(std.name)
            binding.TvDescripcion.setText(std.Descripcion)

        VerificarEstado("Terminada", std)

            ///checked boxes

            binding.cbTerminada.setOnCheckedChangeListener { buttonView, isChecked ->

                    if (isChecked){
                        binding.cbPendiente.isChecked = false
                        std.estado = "Terminada"
                    }
                }
            binding.cbPendiente.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked){
                    binding.cbTerminada.isChecked = false
                    std.estado = "Pendiente"
                }
            }


            changeColorTV(std)

            binding.tvEstado.setText(std.estado)

        }catch (e:Exception){

            Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnUpdate.setOnClickListener{ Updatestd(std) }
        binding.btnDelete.setOnClickListener{  Deletestd(std) }
        binding.cancelButton.setOnClickListener{   Cancel() }
        binding.StartButton.setOnClickListener{
            startStopTimer()
        }

        binding.resetButton.setOnClickListener{
            resetTimer()
        }

        serviceIntent= Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATE))

    }

    private fun VerificarEstado(s: String ,std: TareaModel) {
        if(std.estado.equals("Terminada")){
            binding.tvTimeTerminado.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
            val std2= sqliteHelper.getTareabyId(std.id)
            binding.tvTime.setText(std2.fechaTerminada +" "+std2.horaTerminada)
        }else{
            binding.tvTime.visibility = View.INVISIBLE
            binding.tvTimeTerminado.visibility = View.INVISIBLE
        }
    }


    private fun Deletestd(std: TareaModel) {
        val id = std.id
        if(id==null) return


        val builder = AlertDialog.Builder(this)
        builder.setMessage("Seguro que desea eliminar esta Tarea")
        builder.setCancelable(true)
        builder.setPositiveButton("Si"){dialog, _ ->
            sqliteHelper.deleteStudentbyId(id)
            dialog.dismiss()
            var intent: Intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No"){dialog, _ ->

            dialog.dismiss()

        }

        val alert = builder.create()
        alert.show()

    }


    private fun Updatestd(std:TareaModel) {

        val std3 :ArrayList<TareaModel> = sqliteHelper.getAllStudents()

        val etName  = findViewById<EditText>(R.id.TVnombreTarea)
        val etDescripcion = findViewById<EditText>(R.id.TvDescripcion)

        for (Tarea in std3){
            if(Tarea.name.equals(etName.text.toString(),true)  || Tarea.Descripcion.equals(etDescripcion.text.toString(),true) ){
                Toast.makeText(this,"Esta tarea ya esta registrada", Toast.LENGTH_SHORT).show()
                return
            }else{
                Log.e("Fecha" , "${etName.text.toString()   }")
                Log.e("Fecha" , "${Tarea.name}")
            }
        }


        val name = binding.TVnombreTarea.text.toString()
        val descripcion = binding.TvDescripcion.text.toString()
        val estadp = std.estado
        var fecha = ""
        var hora = ""



        if (std== null) return


       // Log.e("Que tal" , "${Confirmacion}")
        if(estadp.equals("Terminada")){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Seguro que desea marcar como terminada esta Tarea?")
            builder.setCancelable(true)
            builder.setPositiveButton("Si"){dialog, _ ->

                val  cal = Calendar.getInstance()
                fecha = MainActivity.fechaf.format(cal.time)
                hora = MainActivity.horaf.format(cal.time)

                Log.e("Fecha" , "${fecha}")

                Log.e("Hora" , "${hora}")

                val std= TareaModel(id = std!!.id, name  = name , Descripcion = descripcion,estado = estadp,fechaTerminada = fecha,horaTerminada = hora)

                val status = sqliteHelper.updateTarea(std)

                if (status > -1){





                    binding.TVnombreTarea.setText(std.name)
                    binding.TvDescripcion.setText(std.Descripcion)
                    changeColorTV(std)
                    binding.tvEstado.setText(std.estado)
                    binding.tvTime.setText(std.fechaTerminada + " " + std.horaTerminada)
                    VerificarEstado("Terminada",std)

                    Toast.makeText(this, "Se marco como terminada correctamente", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("No"){dialog, _ ->

                dialog.dismiss()
            }

            val alert = builder.create()
            alert.show()
        }else{
            val std= TareaModel(id = std!!.id, name  = name , Descripcion = descripcion,estado = estadp)

            val status = sqliteHelper.updateTarea(std)

            if (status > -1){

                binding.TVnombreTarea.setText(std.name)
                binding.TvDescripcion.setText(std.Descripcion)
                changeColorTV(std)
                binding.tvEstado.setText(std.estado)
                VerificarEstado("Terminada",std)
                Toast.makeText(this, "Se actualizo correctamente", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
            }
        }



    }


    private fun changeColorTV(std: TareaModel) {
        if (std.estado == "En espera"){
            binding.tvEstado.setTextColor(Color.parseColor("#ff0000"))
        }else if (std.estado == "Pendiente"){

            binding.tvEstado.setTextColor(Color.parseColor("#ffff00"))
        }else if(std.estado == "Terminada"){

            binding.tvEstado.setTextColor(Color.parseColor("#228B22"))
        }
        binding.tvEstado.setText(std.estado)
    }

    private fun resetTimer() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Seguro que desea restablecer el temporizador?")
        builder.setCancelable(true)
        builder.setPositiveButton("Si"){dialog, _ ->
            stopTimer()
            time = 0.0
            binding.TVPomodorosRestantes.setText("4")
            binding.timeTV.text = getTimeStringFromDouble(time)

            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog, _ ->

            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()


    }
    private fun Cancel() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Seguro que desea cancelar esta tarea")
        builder.setCancelable(true)
        builder.setPositiveButton("Si"){dialog, _ ->
            stopTimer()
            time = 0.0
            binding.timeTV.text = getTimeStringFromDouble(time)
            var intent: Intent = Intent(this,ConsultadeTareas::class.java)
            intent.putExtra("progreso","progreso")
            startActivity(intent)

            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog, _ ->

            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }


    private fun startStopTimer() {
       if (timerStarted){
           stopTimer()
       }else{
           startTimer()
       }
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA,time)
        startService(serviceIntent)
        binding.StartButton.text= "Pausar"
        binding.StartButton.icon = getDrawable(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.StartButton.text = "Empezar"
        binding.StartButton.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
        Toast.makeText(this, "Se detuvo el temporizador", Toast.LENGTH_SHORT).show()
    }

    private val updateTime:BroadcastReceiver= object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)

            var Pomodoro:Double = 10.0
            if (time  == Pomodoro){
            }

            binding.timeTV.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60
        Log.e("seconds" , "${seconds}")

        pomodoro(seconds)

        return makeTimeString(hours,minutes,seconds)
    }

private var Omitir :Int = -1
    private fun pomodoro(min: Int) {

        var numero =  min
        var multiplo = 5
        var numPomodorosRestantes: Int = Integer.parseInt(binding.TVPomodorosRestantes.text.toString())

        if(numero % multiplo == 0 && numero > 0 ) {


            var rwq =  1
            numPomodorosRestantes = numPomodorosRestantes -1
            if(numPomodorosRestantes == 3){
                 rwq =  1
            }else if(numPomodorosRestantes == 2){
                 rwq =  2
            }else if(numPomodorosRestantes == 1){
                 rwq =  3
            }else if(numPomodorosRestantes == 0){
                 rwq =  4

            }



            binding.TVPomodorosRestantes.setText(numPomodorosRestantes.toString())
            Log.e("numPomodorosRestantes" , "${numPomodorosRestantes}")
            val window = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.popup,null)
            window.contentView = view
            val Boton = view.findViewById<Button>(R.id.btnContinuar)
            val btnReiniciar = view.findViewById<Button>(R.id.btnReiniciar)

            val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
            val BotonOmitirSigueintePomodoro = view.findViewById<Button>(R.id.btnOmitirPomodoro)
            if(rwq == 4){
                view.findViewById<TextView>(R.id.descripcionTvPopup).setText("Se alcanzo el ultimo pomodoro desea repetir la tecnica pomodoro?")
                Boton.visibility = View.INVISIBLE
                BotonOmitirSigueintePomodoro.visibility = View.INVISIBLE
                btnReiniciar.visibility = View.VISIBLE
                btnCancelar.visibility = View.VISIBLE
            }else {
                view.findViewById<TextView>(R.id.pomodoroTv).setText("$rwq pomodoro alcanzado")
            }
            btnReiniciar.setOnClickListener {
                this.resetTimer()
                window.dismiss()
            }
            btnCancelar.setOnClickListener {
                this.Cancel()
            }
            Boton.setOnClickListener{
                this.startTimer()
                window.dismiss()
            }
            BotonOmitirSigueintePomodoro.setOnClickListener {

                val builder = AlertDialog.Builder(this)
                builder.setMessage("Seguro que desea omitir este descanso?")
                builder.setCancelable(true)
                builder.setPositiveButton("Si"){dialog, _ ->
                    this.startTimer()
                    window.dismiss()
                    dialog.dismiss()
                }
                builder.setNegativeButton("No"){dialog, _ ->

                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }

            window.showAsDropDown(binding.TvDescripcion)
            stopTimer()
            //  val intent: Intent = Intent(this,MainActivity::class.java)
            //   startActivity(intent)

        }

    }



    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d",hour,min,sec)


}