package com.example.remainderbday.ui.edit


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.remainderbday.R
import com.example.remainderbday.databinding.FragmentAddReminderBinding
import com.example.remainderbday.util.Destination
import com.example.remainderbday.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*


class ReminderAddFragment : Fragment() {
    private val args by navArgs<ReminderAddFragmentArgs>()
    private lateinit var binding: FragmentAddReminderBinding
    private lateinit var navController: NavController
    @SuppressLint("SimpleDateFormat")
    private var date:String= SimpleDateFormat("EEE, d MMM yyyy").format(Date())
    @SuppressLint("SimpleDateFormat")
    private var time:String = SimpleDateFormat("h:mm a").format(Date())
    private var interval:String = "1"
    private var unit:String = "Day"
    private var repeat:Boolean = true
    private var title:String = "Birthday"


    companion object {
        private const val TAG = "ReminderEditViewModel"
    }
    private lateinit var appBarConfig: AppBarConfiguration

    private val viewModel: ReminderAddViewModel by viewModel {
        parametersOf(args.reminderId)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        navController = findNavController()
        binding = FragmentAddReminderBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel

        setUpToolbar()
        setHasOptionsMenu(true)


        return binding.root
            }

    @KoinApiExtension
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            initializeViews()
        }




    private fun setUpToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            appBarConfig = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfig)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setHomeAsUpIndicator(R.drawable.ic_close)
                setTitle(R.string.app_name)
            }
        }
    }

    @KoinApiExtension
    private fun saveData() {
        viewModel.saveReminder(title,date,time,repeat,interval,unit)
        binding.apply {
            Log.d(TAG,reminderDateText.text.toString() + reminderTimeText.text.toString()
             + reminderRepeatIntervalText.text + reminderRepeatUnitText.text)
        }
    }

    @KoinApiExtension
    private fun initializeViews() {
        binding.apply {
            pickDate.setOnClickListener {
                showDateDialog()
                onDateSelected(date)
                reminderDateText.text = date
            }
            pickTime.setOnClickListener{
                showTimeDialog()
                onTimeSelected(time)
                reminderTimeText.text = time
            }
            reminderRepeatSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    pickRepeatInterval.visibility = View.VISIBLE
                    pickRepeatUnit.visibility = View.VISIBLE
                } else {
                    pickRepeatInterval.visibility = View.GONE
                    pickRepeatUnit.visibility = View.GONE
                 }
            }

            pickRepeatInterval.setOnClickListener {
                numberPickerCustom()
                onValueSelected(interval)
                reminderRepeatIntervalText.text = interval
            }

            pickRepeatUnit.setOnClickListener{
                unitDialog()
                onItemSelected(unit)
                reminderRepeatUnitText.text = unit
            }
            btnSave.setOnClickListener{
                title = reminderTitle.text.toString()
                saveData()
            }
        }
        viewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver { message ->
            if (!TextUtils.isEmpty(message)) {
                showSnackBar(message)
            }
        })
        viewModel.navigationEvent.observe(viewLifecycleOwner, EventObserver { destination ->
            when (destination) {
                Destination.UP -> {
                    navController.navigateUp()
                }
                else -> Log.d("Remainder ADD Fragment","Remainder")
            }
        })

        viewModel.showDatePickerEvent.observe(viewLifecycleOwner, EventObserver { date ->
            showDateDialog()
            onDateSelected(date)
            binding.reminderDateText.text = date
        })

        viewModel.showTimePickerEvent.observe(viewLifecycleOwner, EventObserver { time ->
            showTimeDialog()
            onTimeSelected(time)
            binding.reminderTimeText.text = time
        })

        viewModel.showEditDialogEvent.observe(viewLifecycleOwner, EventObserver { value ->
            numberPickerCustom()
            onValueSelected(interval)
            binding.reminderRepeatIntervalText.text = interval
        })

        viewModel.showListDialogEvent.observe(viewLifecycleOwner, EventObserver { unit ->
            unitDialog()
            onItemSelected(unit)
            binding.reminderRepeatUnitText.text = unit
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_reminder_optionmenu, menu)
        menu.findItem(R.id.edit_reminder_delete).isVisible = args.reminderId != null
    }


    @KoinApiExtension
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.edit_reminder_delete -> {
                //viewModel.showDeleteDialog()
                deleteAndReturn()
                true
            }

            R.id.edit_reminder_check -> {
                viewModel.saveReminder(title,date,time,repeat,interval,unit)
                true
            }

            R.id.home -> {
                viewModel.navigateUp()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

     private fun onDateSelected(date: String) {
        viewModel.updateDateTextView(date)
    }

     private fun onTimeSelected(time: String) {
        viewModel.updateTimeTextView(time)
    }

     private fun onItemSelected(item: String) {
        viewModel.updateReminderUnit(item)
    }

     private fun onValueSelected(valueInput: String) {
        viewModel.updateReminderValue(valueInput)
    }


    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            binding.reminderEditConstraintLayout,
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }


    @KoinApiExtension
    private fun deleteAndReturn() {
        // dialog to confirm the user wants to deleteItem the item
        val builder = context?.let { AlertDialog.Builder(it) }
        //set title for alert dialog
        builder?.setTitle(R.string.dialogTitle)
        //set message for alert dialog
        builder?.setMessage(R.string.dialogMessage)
        builder?.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder?.setPositiveButton("Yes"){ _, _ ->
            viewModel.deleteAndNavigateToList()
        }
        //performing cancel action
        builder?.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(context,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
        }
        //performing negative action
        builder?.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(context,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog? = builder?.create()
        // Set other dialog properties
        alertDialog?.setCancelable(false)
        alertDialog?.show()
    }



    @SuppressLint("SimpleDateFormat")
    private  fun  showDateDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        c.set(year, month, day)
        activity?.let {
                DatePickerDialog(it, { _, year, monthOfYear, dayOfMonth ->
                    val strDate: CharSequence
                    val chosenDate = Time()
                    chosenDate.set(dayOfMonth, monthOfYear, year)
                    val dtDob: Long = chosenDate.toMillis(true)
                    strDate = SimpleDateFormat("EEE, d MMM yyyy").format(dtDob)
                    Log.d("RemainderAddFragment",strDate)
                    date = strDate
                }, year, month, day).show()
            }
        }

    private fun unitDialog() {
        var selected : String = "Day"
        val d = context?.let { AlertDialog.Builder(it) }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.spinner_dialog, null)
        d?.setTitle(R.string.unitTitle)
        d?.setView(dialogView)
        val spinner = dialogView.findViewById<AppCompatSpinner>(R.id.spinner02)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selected =resources.getStringArray(R.array.entries)[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        d?.setPositiveButton("Done") { _, _ ->
            unit = selected
        }
        d?.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog = d?.create()
        alertDialog?.show()
    }




    private  fun  showTimeDialog() {
        val c = Calendar.getInstance()
        val h = c.get(Calendar.HOUR)
        val m = c.get(Calendar.MINUTE)

        activity?.let {
            TimePickerDialog(it, { _, hour, min ->
                //time =  ""+ hour + ":" + min
                updateTime(hour,min)

        }, h,m,false).show()

        }
    }

    private fun updateTime(hour: Int, min: Int) {
        var hours = hour
        val timeSet: String
        when {
            hours > 12 -> {
                hours -= 12
                timeSet = "PM"
            }
            hours == 0 -> {
                hours += 12
                timeSet = "AM"
            }
            hours == 12 -> timeSet = "PM"
            else -> timeSet = "AM"
        }
        val minutes: String = if (min < 10) "0$min" else min.toString()

        // Append in a StringBuilder
        val aTime = StringBuilder().append(hours).append(':')
            .append(minutes).append(" ").append(timeSet).toString()
        time = aTime
    }

    private fun numberPickerCustom() {
        val d = context?.let { AlertDialog.Builder(it) }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.number_picker_dialog, null)
        d?.setTitle("SET Interval")
        d?.setView(dialogView)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.dialog_number_picker)
        numberPicker.maxValue = 30
        numberPicker.minValue = 1
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { _, _, _ -> println("onValueChange: ")
            interval = numberPicker.value.toString()}
        d?.setPositiveButton("Done") { _, _ ->
        }
        d?.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog = d?.create()
        alertDialog?.show()
    }



}









