package acedo.sergio.sqlitedemo

import acedo.sergio.sqlitedemo.databinding.ActivityTimeronServiceBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.lang.Exception
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
            binding.TVnombreTarea.setText(std.name)
            binding.TvDescripcion.setText(std.Descripcion)


            ///checked boxes

            binding.cbTerminada.setOnCheckedChangeListener { buttonView, isChecked ->

                    if (isChecked){
                        binding.cbProgreso.isChecked = false
                        std.estado = "Terminada"
                    }
                }
            binding.cbProgreso.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked){
                    binding.cbTerminada.isChecked = false
                    std.estado = "En progreso"
                }
            }


            changeColorTV(std)

            binding.tvEstado.setText(std.estado)

        }catch (e:Exception){

            Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnUpdate.setOnClickListener{ Updatestd(std) }
        binding.btnDelete.setOnClickListener{  Deletestd(std) }

        binding.StartButton.setOnClickListener{
            startStopTimer()
        }

        binding.resetButton.setOnClickListener{
            resetTimer()
        }

        serviceIntent= Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATE))

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
        
        val name = binding.TVnombreTarea.text.toString()
        val descripcion = binding.TvDescripcion.text.toString()
        val estadp = std.estado



        if (std== null){
            Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
            return
        }

        val std= TareaModel(id = std!!.id, name  = name , Descripcion = descripcion,estado = estadp)
        val status = sqliteHelper.updateStudent(std)

        if (status > -1){

            binding.TVnombreTarea.setText(std.name)
            binding.TvDescripcion.setText(std.Descripcion)
            changeColorTV(std)
            binding.tvEstado.setText(std.estado)

            Toast.makeText(this, "Se actualizo correctamente ", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No se actualizo correctamente ", Toast.LENGTH_SHORT).show()
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
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
        var intent: Intent = Intent(this,ConsultadeTareas::class.java)
        startActivity(intent)
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
        binding.StartButton.text= "Detener"
        binding.StartButton.icon = getDrawable(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.StartButton.text= "Empezar"
        binding.StartButton.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
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

    private fun pomodoro(min: Int) {

        if(min == 5) {
            val window = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.popup,null)
            window.contentView = view
            val imageView = view.findViewById<ImageView>(R.id.imgImage)
            imageView.setOnClickListener{
                window.dismiss()
            }
            window.showAsDropDown(binding.TvDescripcion)
            stopTimer()
            //  val intent: Intent = Intent(this,MainActivity::class.java)
            //   startActivity(intent)
        }
    }



    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d",hour,min,sec)


}