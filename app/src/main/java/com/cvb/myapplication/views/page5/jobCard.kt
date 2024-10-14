package com.cvb.myapplication.views.page5
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

sealed class Users(val name: String) {
    class Employee(val id: String, val employee: String) : Users(employee)
    class Customer(val id: String, val customer: String) : Users(customer)
    class Manager(val id: String, val manager: String) : Users(manager)
}

data class JobData(
    val jobId: String,
    val date: String,
    val time: String,
    val location: String,
    val lat: Double,
    val long: Double,
    val description: String,
    val userList: List<Users.Customer>,
    val providerList: List<Users>,
    val hoursEstimation: Int,
    val hoursSpent: Int,
    val costEstimate: Double,
    val costActual: Double,
    val isCostFinal: Boolean,
    val isCostPaid: Boolean,
    val jobStatus: JobStatus,
    val createdDate: Long,
    val updatedDate: Long,
    val priority: JobPriority,
    val notes: String? = null,
    val attachments: List<String>? = null
)

enum class JobStatus {
    Scheduled, InProgress, Completed, Cancelled
}

enum class JobPriority {
    High, Medium, Low
}


@Preview(device = "id:pixel_8")
@Composable
fun JobCardPage(viewModel: JobCardViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth()) {
            Text(text = viewModel.jobCardTitle().collectAsState("").value,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.19f))
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalLine(thickness = .75.dp)
            Spacer(modifier = Modifier.height(16.dp))

            underlinedTitle("Employee","Asignee")
            underlinedTitle("Customer", "customer")
            Spacer(modifier = Modifier.height(16.dp))
            underlinedTitle("", "Description")
            DescriptionCard(viewModel.descriptor())
            Spacer(modifier = Modifier.height(16.dp))

            underlinedTitle("Tasks","")

            for (state in viewModel.collectStates()) {
                CustomCheckBox(
                    state.title,
                    state.state.collectAsState(false),
                    state.onFirstCheckBoxClicked
                )
            }


            SubmitButton(
                viewModel.isSubmitEnabled().collectAsState(false),
                { viewModel.onSubmitClicked() })


        }

    }
}

@Composable
fun underlinedTitle(type: String, titleParam: String) {
    val color = MaterialTheme.colorScheme.onSurface
    Text(
        text = buildAnnotatedString {
            if (type.isNotEmpty()) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)) {
                    append("$type: ")
                }
            }
            if (titleParam.isNotEmpty()) {
                withStyle(style = SpanStyle(color = color)) {
                    append(titleParam)
                }
            }
        },
        modifier = Modifier
            .padding(16.dp, 8.dp, 8.dp, 8.dp)
            .fillMaxWidth()
            .drawBehind {
                val lineY = size.height + 5f // Adjust the '5f' for desired spacing below text
                drawLine(
                    color = color, // Or your desired line color
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2f // Adjust for desired line thickness
                )
            }
    )
}

const val SAMPLE_TEXT = "" +
        "Nullam vitae elit libero, a pharetra augue. Donec sed odio dui. Maecenas faucibus mollis nisl, id tincidunt sapien. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Vestibulum id ligula porta felis euismod semper. Nulla vitae elit libero, a pharetra augue.\n" +
        "Our expert plumbers are available 24/7 to provide a wide range of services, including:\n" +
        "Faucet repair and replacement\n" +
        "Toilet repair and installation\n" +
        "Drain cleaning and unclogging\n" +
        "Water heater repair and replacement\n" +
        "Leak detection and repair\n" +
        "Sewer line repair and replacement\n" +
        "Pipe installation and repair\n" +
        "And more!\n" +
        "We use only the highest quality materials and equipment to ensure that your plumbing system is in top condition. We also offer a satisfaction guarantee on all of our services.\n" +
        "Contact us today to schedule a free consultation!"


@Composable
fun DescriptionCard(description: String) {
    val backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.13f)

    Card(
        modifier = Modifier.padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SubmitButton(isEnabled: State<Boolean>, onSubmitClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onSubmitClicked,
            enabled = isEnabled.value,
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun CustomCheckBox(
    title: String,
    state: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state.value,
            onCheckedChange = { isChecked -> onCheckedChange(isChecked) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}

@Composable
fun HorizontalLine(color: Color = MaterialTheme.colorScheme.surface, thickness: Dp = 1.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

class JobCardViewModel: ViewModel() {
    private val _title = MutableStateFlow("Ticket Number: 12-2AGIF" + Math.random().toString().substring(0, 6))
    private val _dataFlow = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList())
    private val _descriptor = MutableStateFlow(SAMPLE_TEXT)
    fun initJob() {

    }

    fun collectStates(): List<StateCallback> {
        _dataFlow.value =
            listOf(
                Pair("First Task Description of task component", true),
                Pair("Second Task Description of task component", false),
                Pair("Third Task Description of task component", false),
                Pair("Fourth Task Description of task component", false)
            )
        val listToReturn = mutableListOf<StateCallback>()
        _dataFlow.value.forEachIndexed { index, s ->
            listToReturn.add(StateCallback(_dataFlow.asStateFlow().map { it[index] }
                .distinctUntilChanged().map { it.second }, s.first, { callUpdate(index, it) }))
        }
        return listToReturn
    }

    private fun callUpdate(index: Int, it: Boolean) {
        val newList = _dataFlow.value.toMutableList()
        newList[index] = Pair(newList[index].first, it)
        _dataFlow.value = newList
    }

    fun runUpdate() {

    }

    fun runUpdate(index: String) {

    }

    fun descriptor(): String {
        return _descriptor.value
    }

    fun isSubmitEnabled(): Flow<Boolean> {
        return _dataFlow.flatMapConcat {
            it.asFlow().map { it.second }.runningFold(true) {
                acc, value -> acc && value
            }
        }
    }

    fun onSubmitClicked() {

    }

    fun jobCardTitle(): Flow<String> {
        return _title.asStateFlow()
    }


}

data class StateCallback(
    val state: Flow<Boolean>,
    val title: String,
    val onFirstCheckBoxClicked: (Boolean) -> Unit
)


