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
import com.wings.mile.databinding.SchemeBankCellBinding
import com.wings.mile.databinding.SchemeLicenseExpiryBinding
import com.wings.mile.model.User_Accountitem


import kotlin.collections.ArrayList


class UserAccountAdapter(var context: Context, var dataSource1: TextView, var dataSource: List<User_Accountitem>, var onItemClicked:OnItemClicked, value: Int) : RecyclerView.Adapter<UserAccountAdapter.LicenseAdapterViewHolder>(), Filterable {
    var followModel: ArrayList<User_Accountitem> = ArrayList()
    var filterList: ArrayList<User_Accountitem>? = ArrayList()
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
        val binding: SchemeBankCellBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.scheme_bank_cell, parent, false)
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

    class LicenseAdapterViewHolder(var binding: SchemeBankCellBinding, var onItemClicked: UserAccountAdapter.OnItemClicked, var value: Int) : RecyclerView.ViewHolder(binding.root) {

        var id = value
        fun bind(context: Context, followModel: ArrayList<User_Accountitem>, position: Int) {

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
                        followModel.get(position).first_Name.toString() + " " + followModel.get(
                            position
                        ).last_Name.toString()
                    binding.textViewApplicationNumber.text =
                        followModel.get(position).phone_Number.toString()
                    binding.accountNumber.text =
                        followModel.get(position).account_Number.toString()
                    binding.ifscCode.text =
                        followModel.get(position).ifsc_Code.toString()
                    binding.bankName.text =
                        followModel.get(position).bank_Name.toString()
                    binding.branchName.text =
                        followModel.get(position).branch_Name.toString()
                   // binding.comments.text = "Comments : " + followModel.get(position).msg
                    binding.call.setOnClickListener {

                        onItemClicked.onDriverdetails(followModel[position].phone_Number)
                    }


                }
            }
        }
    }

    fun setData(data: ArrayList<User_Accountitem>?) {
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
                    followModel = filterList as ArrayList<User_Accountitem>
                } else {
                   // dataSource1.visibility=View.GONE
                    val resultList = ArrayList<User_Accountitem>()
                    resultList.clear()

                    for ((index, values) in filterList!!.withIndex()) {
                        if (values.account_Number?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
                            resultList.add(values)
                        } else if (values.first_Name?.toLowerCase()!!.contains(
                                charSearch.toLowerCase()
                            )) {
                            resultList.add(values)
                        } else if (values.last_Name.toString().contains(charSearch)) {
                            resultList.add(values)
                        } else if (values.phone_Number?.toLowerCase()!!.contains(charSearch.toLowerCase())) {
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
                followModel = results?.values as ArrayList<User_Accountitem>
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
