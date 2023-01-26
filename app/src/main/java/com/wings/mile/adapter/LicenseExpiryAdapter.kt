package com.wings.mile.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.wings.mile.R
import com.wings.mile.databinding.SchemeLicenseExpiryBinding

import com.wings.mile.model.license_expiryItem
import kotlin.collections.ArrayList


class LicenseExpiryAdapter(var context: Context, var dataSource1: TextView, var dataSource: List<license_expiryItem>, var onItemClicked:OnItemClicked, value: Int) : RecyclerView.Adapter<LicenseExpiryAdapter.LicenseAdapterViewHolder>(), Filterable {
    var followModel: ArrayList<license_expiryItem> = ArrayList()
    var filterList: ArrayList<license_expiryItem>? = ArrayList()
    var id = value

    init {
        //followModel = dataSource as ArrayList<getdriver>
       // filterList = dataSource as ArrayList<getdriver>
        followModel.addAll(dataSource)
        filterList!!.addAll(dataSource)
    }

    interface OnItemClicked {
        fun onDriverdetails(driversItem: String)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseAdapterViewHolder {
        val binding: SchemeLicenseExpiryBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.scheme_license_expiry, parent, false)
        return LicenseAdapterViewHolder(binding,onItemClicked, id)
    }

    override fun onBindViewHolder(holder: LicenseAdapterViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(context, followModel, position)

    }

    override fun getItemCount(): Int {
        //Toast.makeText(context,followModel.size,Toast.LENGTH_LONG).show()
        Log.e("data--->",""+followModel.size)
        return followModel.size
    }

    class LicenseAdapterViewHolder(var binding: SchemeLicenseExpiryBinding, var onItemClicked: LicenseExpiryAdapter.OnItemClicked, var value: Int) : RecyclerView.ViewHolder(binding.root) {

        var id = value
        fun bind(context: Context, followModel: ArrayList<license_expiryItem>, position: Int) {

            followModel[position].let {
                if (followModel.size == 0) {
                    binding.textViewNoRecordFound2.visibility = View.VISIBLE
                    binding.rlayout.visibility = View.GONE
                    binding.rvLayout.visibility = View.GONE
                } else {
                    binding.textViewNoRecordFound2.visibility = View.GONE
                    binding.rlayout.visibility = View.VISIBLE
                    binding.rvLayout.visibility = View.VISIBLE
                    binding.textViewName.text =
                        followModel.get(position).first_name.toString() + " " + followModel.get(
                            position
                        ).last_name.toString()
                    binding.textViewApplicationNumber.text =
                        followModel.get(position).phone_num.toString()
                    binding.textViewTillLicensename.text =
                        followModel.get(position).license_plate_no.toString()
                    binding.textViewExpiryDate.text =
                        followModel.get(position).expiry_date.toString()
                    binding.comments.text = "Comments : " + followModel.get(position).msg
                    binding.call.setOnClickListener {

                        onItemClicked.onDriverdetails(followModel[position].phone_num)
                    }


                }
            }
        }
    }

    fun setData(data: ArrayList<license_expiryItem>?) {
        this.followModel.clear()
        data?.let { this.followModel.addAll(it) }
        data?.let { this.filterList?.addAll(it) }
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    followModel = filterList as ArrayList<license_expiryItem>
                } else {
                   // dataSource1.visibility=View.GONE
                    val resultList = ArrayList<license_expiryItem>()
                    resultList.clear()

                    for ((index, values) in filterList!!.withIndex()) {
                        if (values.license_plate_no?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
                            resultList.add(values)
                        } else if (values.first_name?.toLowerCase()!!.contains(
                                charSearch.toLowerCase()
                            )) {
                            resultList.add(values)
                        } else if (values.last_name.toString().contains(charSearch)) {
                            resultList.add(values)
                        } else if (values.phone_num?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
                            resultList.add(values)
                        }

                    }
                    followModel = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = followModel
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                followModel = results?.values as ArrayList<license_expiryItem>
                // onItemClicked.filterList(listTargetApproval!!.size)
                if (followModel.size == 0) {
                    dataSource1.visibility=View.VISIBLE

                 //  CustomToast.warningToast(context, context.getString(R.string.no_record_found))
                    //val toast = Toast.makeText(context, context.getString(R.string.no_record_found), Toast.LENGTH_SHORT)
                    //toast.setGravity(Gravity.CENTER, 0, 0)
                    //toast.show()
                }else{
                    dataSource1.visibility=View.GONE

                }
                notifyDataSetChanged()
            }
        }
    }

}
