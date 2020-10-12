package com.example.kbrs

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.OnClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kotlinx.android.synthetic.main.list_item.view.*

class FileAdapter(val callback: (String)->Unit) :
ListAdapter<FileModel, FileAdapter.FileViewModel>(FileModelDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  FileViewModel(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))

    override fun onBindViewHolder(holder: FileViewModel, position: Int)
            = holder.bind(getItem(position)){
                callback(it)
            }


    inner class FileViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: FileModel, callback: (String)->Unit) = with(itemView) {
            textView.text = item.nameOfFile
            setOnClickListener {
                callback(item.nameOfFile)
            }
        }
    }


    private class FileModelDC : DiffUtil.ItemCallback<FileModel>() {
        override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem == newItem
        }
    }
}