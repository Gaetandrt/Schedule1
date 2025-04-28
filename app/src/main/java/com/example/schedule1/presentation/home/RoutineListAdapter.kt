package com.example.scheduleapp.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule1.databinding.ItemRoutineBinding
import com.example.schedule1.domain.model.Routine

class RoutineListAdapter :
    ListAdapter<Routine, RoutineListAdapter.RoutineViewHolder>(RoutineDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine: Routine = getItem(position)
        holder.bind(routine)
    }

    inner class RoutineViewHolder(private val binding: ItemRoutineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(routine: Routine): Unit {
            binding.textViewRoutineName.text = routine.name
            binding.textViewRoutineTime.text = routine.scheduleTime
            if (routine.description.isNullOrBlank()) {
                binding.textViewRoutineDescription.visibility = View.GONE
            } else {
                binding.textViewRoutineDescription.text = routine.description
                binding.textViewRoutineDescription.visibility = View.VISIBLE
            }
            // TODO: Add click listener if needed for edit/delete
        }
    }
}

class RoutineDiffCallback : DiffUtil.ItemCallback<Routine>() {
    override fun areItemsTheSame(oldItem: Routine, newItem: Routine): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Routine, newItem: Routine): Boolean {
        return oldItem == newItem
    }
}