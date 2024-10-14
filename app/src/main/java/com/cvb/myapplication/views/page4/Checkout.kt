package com.cvb.myapplication.views.page4

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cvb.myapplication.application.ISApplication
import com.cvb.myapplication.views.page3.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

// Sealed classes
sealed class Actions(val name: String) {
    object Schedule : Actions("Schedule")
    object Email : Actions("Email")
    object MoreInformation : Actions("More Information")
    object Book : Actions("Book")
    object Inquire : Actions("Inquire")
    object Support : Actions("Support")
}

sealed class Services(val title: String, val actions: List<Actions>) {
    object VirtualConsultation : Services(
        title = "Virtual Consultation",
        actions = listOf(Actions.Schedule, Actions.Email, Actions.MoreInformation)
    )

    object Consultation : Services(
        title = "Consultation",
        actions = listOf(Actions.Book, Actions.Inquire)
    )

    object Evaluation : Services(
        title = "Evaluation",
        actions = listOf(Actions.Support)
    )
}

// ViewModel
class CheckoutViewModel : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CheckoutViewModel()
            }
        }
    }
    private val _selectedDate = MutableStateFlow<String>("")
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow<String>("")
    val selectedTime = _selectedTime.asStateFlow()

    private val _servicesList = MutableStateFlow<List<Services>>(
        listOf(
            Services.VirtualConsultation,
            Services.Consultation,
            Services.Evaluation
        )
    )
    val servicesList = _servicesList.asStateFlow()

    fun updateDate(date: String) {
        _selectedDate.value = date
    }

    fun updateTime(time: String) {
        _selectedTime.value = time
    }
}

// Composable functions
@Composable
fun CheckoutPage(viewModel: CheckoutViewModel = viewModel(factory = CheckoutViewModel.Factory)) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val services by viewModel.servicesList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DateTimePicker(
            selectedDate = selectedDate,
            selectedTime = selectedTime,
            onDateSelected = { viewModel.updateDate(it) },
            onTimeSelected = { viewModel.updateTime(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ServicesList(services = services)
    }
}

@Composable
fun DateTimePicker(
    selectedDate: String,
    selectedTime: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = "${month + 1}/$dayOfMonth/$year"
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val time = String.format("%02d:%02d %s", hour, minute, amPm)
            onTimeSelected(time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Column {
        Button(onClick = { datePickerDialog.show() }) {
            Text(text = "Select Date")
        }
        Text(text = if (selectedDate.isNotEmpty()) "Selected Date: $selectedDate" else "No Date Selected")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { timePickerDialog.show() }) {
            Text(text = "Select Time")
        }
        Text(text = if (selectedTime.isNotEmpty()) "Selected Time: $selectedTime" else "No Time Selected")
    }
}

@Composable
fun ServicesList(services: List<Services>) {
    LazyColumn {
        items(services) { service ->
            ServiceItem(service = service)
        }
    }
}

@Composable
fun ServiceItem(service: Services) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(text = service.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ActionsList(actions = service.actions)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActionsList(actions: List<Actions>) {
    FlowRow() {
        actions.forEach { action ->
            ActionButton(action = action)
        }
    }
}

@Composable
fun ActionButton(action: Actions) {
    Button(
        onClick = { /* Handle action click */ },
        modifier = Modifier.height(40.dp)
    ) {
        Text(text = action.name)
    }
}