package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.model.Notifications
import java.text.SimpleDateFormat
import java.util.Calendar


class NotificationAdapter(
    var context: Context, val lifecycleOwner: LifecycleOwner, val handleListenr: HandleClicks
) : PagingDataAdapter<Notifications, NotificationAdapter.NotificationAdapterVH>(ProductDifferntiator) {

    val selectedItems = ObservableArrayList<Int>()
    var isMultiSelectMode: MutableLiveData<Boolean> = MutableLiveData(false)
    var selectedNotifications = listOf<Notifications>()

    //    private var listData: MutableList<Property> = data as MutableList<Property>
    override fun onBindViewHolder(holder: NotificationAdapterVH, position: Int) {

        holder.bindTo(this, getItem(position))

        /*val model = getItem(position)!!
        val isSelected = selectedItems.contains(position)



        holder.itemView.setOnLongClickListener{

            if (isSelected) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
            isLongClickEnabled = true

            markSelectedItem(position)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            deselectItem(position)
            val dataModel = model.data
            if (isSelected) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            notifyItemChanged(position)
           *//* *//*
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapterVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItemBinding = layoutInflater.inflate(R.layout.notification_row, parent, false)
        return NotificationAdapterVH(listItemBinding)
    }


    class NotificationAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val productName = itemView.findViewById<TextView>(R.id.productName)
        val message = itemView.findViewById<TextView>(R.id.message)
        val rootlayout = itemView.findViewById<CardView>(R.id.rootlayout)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox2)

        var photo: Notifications? = null
        private lateinit var adapter: NotificationAdapter

        /**
         * Binds the parent adapter and the photo to the ViewHolder.
         */
        fun bindTo(adapter: NotificationAdapter, model: Notifications?) {
            this.adapter = adapter

            if (!model?.deleted!!) {
                rootlayout.setBackgroundColor(
                    ContextCompat.getColor(
                        adapter.context,
                        R.color.bg_color
                    )
                )
            } else {
                rootlayout.setBackgroundColor(
                    ContextCompat.getColor(
                        adapter.context,
                        R.color.white
                    )
                )
            }

            userName.text = model.body
            productName.text = model.title

            val formatter = SimpleDateFormat("MMM dd,yyyy HH:mm")

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = model.timestamp?.Seconds?.toLong()!!
            message.text =
                DateFormat.format("MMM dd,yyyy HH:mm", model.timestamp?.Seconds?.toLong()!! *1000).toString()

            formatter.format(calendar.time)

            rootlayout.setOnClickListener {
                if (adapter.isMultiSelectMode.value!!) {
                    // If the item clicked is the last selected item
                    if (adapter.isLastSelectedItem(layoutPosition)) {
                        adapter.disableSelection()
                        return@setOnClickListener
                    }
                    // Set checked if not already checked
                    setItemChecked(!adapter.isItemSelected(layoutPosition))
                    if (adapter.getAllSelected().isEmpty()) {
                        adapter.disableSelection()
                    }
                } else {
                    Log.d("TAG", "bindTo: clicked ${photo?.module}")
                    model.deleted = true
                    adapter.notifyItemChanged(layoutPosition)
                    adapter.viewNotification(layoutPosition, model)
                }
            }

            rootlayout.setOnLongClickListener {
                if (!adapter.isMultiSelectMode.value!!) {
                    adapter.enableSelection()
                    setItemChecked(true)
                    adapter.handleListenr.enableOptions()
                }
                true
            }

            adapter.isMultiSelectMode.observe(adapter.lifecycleOwner) {
                if (it) { // When selection gets enabled, show the checkbox
                    checkBox.visibility = View.VISIBLE
                } else {
                    checkBox.visibility = View.GONE
                }
            }

            adapter.selectedItems.addOnListChangedCallback(onSelectedItemsChanged)

            listChanged()
//            loadThumbnail()
        }

        /**
         * Listener for changes in selected images.
         * Calls [listChanged] whatever happens.
         */
        private val onSelectedItemsChanged =
            object : ObservableList.OnListChangedCallback<ObservableList<Int>>() {

                override fun onChanged(sender: ObservableList<Int>?) {
                    listChanged()
                }

                override fun onItemRangeChanged(
                    sender: ObservableList<Int>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    listChanged()
                }

                override fun onItemRangeInserted(
                    sender: ObservableList<Int>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    listChanged()
                }

                override fun onItemRangeMoved(
                    sender: ObservableList<Int>?,
                    fromPosition: Int,
                    toPosition: Int,
                    itemCount: Int
                ) {
                    listChanged()
                }

                override fun onItemRangeRemoved(
                    sender: ObservableList<Int>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    listChanged()
                }

            }

        private fun listChanged() {
            val isSelected = adapter.isItemSelected(layoutPosition)

            checkBox.isChecked = isSelected
        }

        private fun setItemChecked(checked: Boolean) {
            layoutPosition.let {
                if (checked) {
                    adapter.addItemToSelection(it)
                } else {
                    adapter.removeItemFromSelection(it)
                }
            }
        }

        /**
         * Load the thumbnail for the [photo].
         */
    }

    private fun viewNotification(ayoutPosition: Int, model: Notifications?) {
        handleListenr.onItemClick(model!!)
    }


    companion object ProductDifferntiator : DiffUtil.ItemCallback<Notifications>() {

        override fun areItemsTheSame(oldItem: Notifications, newItem: Notifications): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notifications, newItem: Notifications): Boolean {
            return oldItem == newItem
        }
    }

    fun disableSelection() {
        selectedItems.clear()
        isMultiSelectMode.postValue(false)
    }

    fun enableSelection() {
        isMultiSelectMode.postValue(true)
    }

    /**
     * Add an item it the selection.
     */
    fun addItemToSelection(position: Int): Boolean = selectedItems.add(position)

    /**
     * Remove an item to the selection.
     */
    fun removeItemFromSelection(position: Int) = selectedItems.remove(position)

    /**
     * Indicate if an item is already selected.
     */
    fun isItemSelected(position: Int) = selectedItems.contains(position)

    /**
     * Indicate if an item is the last selected.
     */
    fun isLastSelectedItem(position: Int) = isItemSelected(position) && selectedItems.size == 1

    /**
     * Select all items.
     */
    fun selectAll() {
        for (i in 0 until itemCount) {
            if (!isItemSelected(i)) {
                addItemToSelection(i)
            }
        }
        enableSelection()

        notifyDataSetChanged()
    }

    /**
     * Get all items that are selected.
     */
    fun getAllSelected(): ArrayList<String> {
        val items = arrayListOf<String>()
        for (position in selectedItems) {
            val photo = getItem(position)
            if (photo != null) {
                items.add(photo.id!!)
            }
        }
        return items
    }

    interface HandleClicks {
        fun enableOptions()

        fun onItemClick(notification: Notifications)

    }

}