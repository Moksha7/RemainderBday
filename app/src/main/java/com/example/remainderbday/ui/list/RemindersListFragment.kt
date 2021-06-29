package com.example.remainderbday.ui.list


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.remainderbday.R
import com.example.remainderbday.adapters.RemindersListAdapter
import com.example.remainderbday.data.Reminders
import com.example.remainderbday.databinding.FragmentRemindersListBinding
import com.example.remainderbday.util.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class RemindersListFragment : Fragment() , Callbacks {

    private val listViewModel:RemindersListViewModel by viewModel()

    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        reminderAdapter = RemindersListAdapter(onCheckChangedListener, onReminderClickListener)
        val binding = FragmentRemindersListBinding.inflate(layoutInflater, container, false)
        binding.viewModel = listViewModel
        binding.remindersList.apply {
            adapter = reminderAdapter
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        binding.lifecycleOwner = this
        setUpToolbar()
        return binding.root
    }

    private fun setUpToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(true)
                setTitle(R.string.app_name)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFabNavigation()
    }



    private fun setUpFabNavigation(){
        listViewModel.fabNavListenner.observe(viewLifecycleOwner, EventObserver{
             val action = RemindersListFragmentDirections.actionRemindersListFragmentToReminderAddFragment(null)
              findNavController().navigate(action)
        })
    }

     override fun onReminderClick(reminder: Reminders) {
        val action = RemindersListFragmentDirections.actionRemindersListFragmentToReminderAddFragment(reminder.reminderIndentifier)
        navController.navigate(action)
    }


    private val onCheckChangedListener: (Reminders) -> Unit = { reminder ->
        val newReminder = reminder.copy(isActive = !reminder.isActive)
        listViewModel.updateReminder(newReminder)
        listViewModel.updateAlarm(newReminder)
    }

    private val onReminderClickListener: (Reminders) -> Unit = { reminder ->
        onReminderClick(reminder)
    }


    private lateinit var reminderAdapter: RemindersListAdapter

}

