package com.shelflife.instrument.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.FragmentNotificationBinding
import com.shelflife.instrument.databinding.FragmentOptionBinding
import com.shelflife.instrument.databinding.FragmentProductBinding
import com.shelflife.instrument.factory.RoomViewModelFactory
import com.shelflife.instrument.ui.BaseFragment
import com.shelflife.instrument.ui.adapter.AdapterNotification
import com.shelflife.instrument.viewmodel.RoomViewModel
import javax.inject.Inject

class NotificationFragment : BaseFragment() {

    init {
        appComponent.inject(this)
    }

    private var _binding: FragmentNotificationBinding?=null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterNotification

    @Inject
    lateinit var viewModelFactory: RoomViewModelFactory
    private val roomViewModel: RoomViewModel by viewModels{viewModelFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomViewModel.getListNotification.distinctUntilChanged().observe(viewLifecycleOwner){

            adapter = AdapterNotification(it)
            binding.rvNotification.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNotification.adapter = adapter
        }

        roomViewModel.getNotifications()
    }

}