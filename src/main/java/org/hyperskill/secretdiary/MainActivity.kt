package org.hyperskill.secretdiary
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var newEntryInput: EditText
    private lateinit var saveNewEntryButton: Button
    private lateinit var undoLastEntryButton: Button
    private lateinit var entry: TextView

    private var newEntryText = ""
    private var entries = mutableListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLayout()

        /*
            Tests for android can not guarantee the correctness of solutions that make use of
            mutation on "static" variables to keep state. You should avoid using those.
            Consider "static" as being anything on kotlin that is transpiled to java
            into a static variable. That includes global variables and variables inside
            singletons declared with keyword object, including companion object.
            This limitation is related to the use of JUnit on tests. JUnit re-instantiate all
            instance variable for each test method, but it does not re-instantiate static variables.
            The use of static variable to hold state can lead to state from one test to spill over
            to another test and cause unexpected results.
            Using mutation on static variables to keep state
            is considered a bad practice anyway and no measure
            attempting to give support to that pattern will be made.
         */
    }

    private fun initLayout() {
        newEntryInput = findViewById(R.id.etNewWriting)
        saveNewEntryButton = findViewById(R.id.btnSave)
        undoLastEntryButton = findViewById(R.id.btnUndo)
        entry = findViewById(R.id.tvDiary)

        newEntryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                newEntryText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        saveNewEntryButton.setOnClickListener { handleSaveNewEntryClicked() }
        undoLastEntryButton.setOnClickListener { handleUndoLastEntryClicked() }
    }

    private fun renderEntries() {
        entry.text = entries.map { "${it.dateTime}\n${it.text}" }.joinToString("\n\n")
    }

    private fun handleSaveNewEntryClicked() {
        if (newEntryText.isBlank()) {
            return Toast.makeText(this, "Empty or blank input cannot be saved" ,Toast.LENGTH_SHORT).show()
        }

        entries.add(0, Entry(newEntryText, getFormattedDateTime()))

        renderEntries()

        newEntryInput.setText("")
        newEntryInput.clearFocus()
    }

    private fun handleUndoLastEntryClicked() {
        AlertDialog.Builder(this)
            .setTitle("Remove last note")
            .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
            .setPositiveButton("Yes") { _, _ ->
                if (entries.isNotEmpty()) {
                    entries.removeFirst()
                    renderEntries()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun getFormattedDateTime(): String {
        val currentInstant = Clock.System.now()
        val zonedDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        var formattedDateTime = zonedDateTime.year.toString().padStart(2, '0')
        formattedDateTime += "-"
        formattedDateTime += zonedDateTime.monthNumber.toString().padStart(2, '0')
        formattedDateTime += "-"
        formattedDateTime += zonedDateTime.dayOfMonth.toString().padStart(2, '0')
        formattedDateTime += ' '
        formattedDateTime += zonedDateTime.hour.toString().padStart(2, '0')
        formattedDateTime += ":"
        formattedDateTime += zonedDateTime.minute.toString().padStart(2, '0')
        formattedDateTime += ":"
        formattedDateTime += zonedDateTime.second.toString().padStart(2, '0')

        return formattedDateTime
    }

    data class Entry(val text: String, val dateTime: String)
}