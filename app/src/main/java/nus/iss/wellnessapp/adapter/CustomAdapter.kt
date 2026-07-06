package nus.iss.wellnessapp.adapter
// author : Tan Pang Wee
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import nus.iss.wellnessapp.R
import nus.iss.wellnessapp.model.WellnessRecordResponse

class CustomAdapter(private val context: Context,
                    private val records: List<WellnessRecordResponse>

) : ArrayAdapter<Any?> (
    context, R.layout.row
){
    init {
        addAll(*arrayOfNulls<Any>(records.size))
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup): View {
        var _view = view

        if (_view == null) {
            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            _view = inflater.inflate(R.layout.row, parent, false)
        }

        // set the image for ImageView
        val imageView = _view!!.findViewById<ImageView>(R.id.imageView)
        // set the text for TextView
        val textViewCategory = _view.findViewById<TextView>(R.id.tvCategory)
        val textViewRecordDate = _view.findViewById<TextView>(R.id.tvDateRecord)
        val textViewValue = _view.findViewById<TextView>(R.id.tvValue)
//        textView?.text = records[pos]
        val record = records[pos]
        when (record.category.lowercase()) {
            "sleep" -> imageView.setImageResource(R.drawable.app_img_sleep)
            "water" -> imageView.setImageResource(R.drawable.app_img_water)
            "steps" -> imageView.setImageResource(R.drawable.app_img_steps)
            "exercise" -> imageView.setImageResource(R.drawable.app_img_exercise)
            "mood" -> imageView.setImageResource(R.drawable.app_img_mood)
            else -> imageView.setImageResource(R.drawable.app_img_wellness)
        }
        textViewCategory.text =record.category.uppercase()
        // work around due to record date backend is a List
        var rawDate = "${record.recordDate[0]}-${record.recordDate[1]}-${record.recordDate[2]}"
        textViewRecordDate.text = formatDate(rawDate)
        textViewValue.text = "${record.value} ${record.unit}"
        return _view
    }

    private fun formatDate(rawDate: String): String {

        val formattedDate = rawDate
            .split("-")
            .let {
                "%04d-%02d-%02d".format(
                    it[0].toInt(),
                    it[1].toInt(),
                    it[2].toInt()
                )
            }
        return formattedDate
    }
}
