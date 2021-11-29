package acedo.sergio.sqlitedemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter: RecyclerView.Adapter<TareaAdapter.StudentViewHolder>() {
    private var stdList:  ArrayList<TareaModel> = ArrayList()
    private var onClickItem:((TareaModel) -> Unit)? =  null
    private var onClickDeleteItem:((TareaModel) -> Unit)? =  null
    private var onClickStartItem:((TareaModel) -> Unit)? =  null

    fun addItems(items:ArrayList<TareaModel>){
        this.stdList =  items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (TareaModel)-> Unit){

        this.onClickItem = callback

    }
    fun setOnClickDeleteItem(callback: (TareaModel)-> Unit){

        this.onClickDeleteItem = callback

    }

    fun setOnClickStartItem(callback: (TareaModel)-> Unit){

        this.onClickStartItem = callback

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(
       LayoutInflater.from(parent.context).inflate(R.layout.card_layout_tarea, parent, false)


    )
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val std = stdList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener{ onClickItem?.invoke(std)}
        holder.view.findViewById<ImageView>(R.id.imgDlt).setOnClickListener{
            Log.e("Entro" , "${std}")
            onClickDeleteItem?.invoke(std) }

    }


    override fun getItemCount(): Int {
   return stdList.size
    }


    class StudentViewHolder(var view: View): RecyclerView.ViewHolder(view){
        private var id = view.findViewById<TextView>(R.id.tvId)
        private var name = view.findViewById<TextView>(R.id.tvName)
        private var email = view.findViewById<TextView>(R.id.tvEmail)
        private var estado = view.findViewById<TextView>(R.id.tvEstado)
        private var delete = view.findViewById<ImageView>(R.id.imgDlt)

        fun bindView(std : TareaModel){

            id.text= std.id.toString()
            name.text=std.name
            email.text=std.Descripcion
            estado.text=std.estado

        }
    }

}