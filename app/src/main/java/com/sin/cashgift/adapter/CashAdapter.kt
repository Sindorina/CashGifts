package com.sin.cashgift.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sin.cashgift.data.CashBean
import com.sin.cashgift.databinding.CashItemBinding

class CashAdapter :ListAdapter<CashBean,CashAdapter.ViewHolder>(diffCallback){
    class ViewHolder(val binding:CashItemBinding)
        :RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CashItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cashBean = getItem(position)
        holder.binding.executePendingBindings()
    }
}
val diffCallback = object: DiffUtil.ItemCallback<CashBean>(){
    override fun areItemsTheSame(oldItem: CashBean, newItem: CashBean): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CashBean, newItem: CashBean): Boolean {
        return oldItem.id == newItem.id
    }

}