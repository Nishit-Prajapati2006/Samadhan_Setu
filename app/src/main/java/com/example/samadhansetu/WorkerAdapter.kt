package com.example.samadhansetu

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.samadhansetu.databinding.ItemWorkerDetailsBinding

class WorkerAdapter(
    private val workerList: MutableList<Worker>,
    private val onEditClick: (Worker) -> Unit,
    private val onDeleteClick: (Worker) -> Unit
) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>(), SpinnerAdapter {

    inner class WorkerViewHolder(private val binding: ItemWorkerDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(worker: Worker) {
            binding.tvWorkerName.text = worker.name
            binding.tvWorkerProfession.text = worker.profession
            binding.tvWorkerPhone.text = worker.phone

            // Set up the popup menu for edit and delete
            binding.ivPopupMenu.setOnClickListener { view ->
                showPopupMenu(view, worker)
            }
        }

        private fun showPopupMenu(view: View, worker: Worker) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_worker_options)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit_worker -> {
                        onEditClick(worker)
                        true
                    }
                    R.id.action_delete_worker -> {
                        onDeleteClick(worker)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val binding = ItemWorkerDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.bind(workerList[position])
    }

    override fun getItemCount() = workerList.size
    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getDropDownView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("Not yet implemented")
    }
}