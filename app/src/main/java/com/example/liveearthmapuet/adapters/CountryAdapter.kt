package com.example.liveearthmapuet.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.blongho.country_data.Country
import com.example.liveearthmapuet.R

import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.interfaces.CountryListInterface
import kotlin.collections.ArrayList

@SuppressLint("StaticFieldLeak")
class CountryAdapter(
    private var countries: MutableList<Country>,
    private val activity: Activity,
    private val countryListInterface: CountryListInterface
) :
    RecyclerView.Adapter<CountryAdapter.LanguageHolder>(), Filterable {

    var tempCountries = countries

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.country_list_item, parent, false)
        return LanguageHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
        holder.countryCountCountryList.text = "${position + 1}."
        holder.flagCountryList.setImageResource(countries[position].flagResource)
        holder.countryNameCountryList.text = countries[position].name

        holder.clCountryList.setOnClickListener {
            countryListInterface.onCountryClick(countries[position])
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    open class LanguageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagCountryList: ImageView = itemView.findViewById(R.id.flagCountryList)
        val clCountryList: ConstraintLayout = itemView.findViewById(R.id.clCountryList)
        val countryNameCountryList: TextView = itemView.findViewById(R.id.countryNameCountryList)
        val countryCountCountryList: TextView = itemView.findViewById(R.id.countryCountCountryList)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            @SuppressLint("LogNotTimber")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val result = FilterResults()
                val founded: MutableList<Country> = ArrayList()
                countries = tempCountries

                for (item in countries) {
                    if (constraint?.let { item.name.toLowerCase().contains(it) } == true) {
                        founded.add(item)
                        Log.d(Misc.logKey, item.toString())
                    }
                }
                result.values = founded;
                result.count = founded.size;

                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countries = tempCountries
                countries = results!!.values as ArrayList<Country>
                notifyDataSetChanged()

            }

        }
    }

}