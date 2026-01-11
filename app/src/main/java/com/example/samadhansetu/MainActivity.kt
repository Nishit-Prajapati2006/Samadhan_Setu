package com.example.samadhansetu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.samadhansetu.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var generativeModel: GenerativeModel
    lateinit var name: String
    lateinit var roomNo: String
    lateinit var regNo: String
    lateinit var email: String
    lateinit var phone: String
//    val apiKey = "AIzaSyAbSgjn1g-8-LH5bGwLaS_tDAbJ2lnsbX0" // Replace with your key
      val apiKey = BuildConfig.GEMINI_API_KEY
    // --- NEW: Speech Recognizer ---
    private var speechRecognizer: SpeechRecognizer? = null

    // --- NEW: Permission Launcher ---
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Start listening.
                startSpeechToText()
            } else {
                // Permission is denied. Show a toast.
                Toast.makeText(this, "Microphone permission is required to use this feature.", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView5.text="samadhanSetu — from complaint to solution."

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        initializeGemini()

        if (user == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        fetchStudentData(user.uid)

        database = FirebaseDatabase.getInstance().getReference("Complaints")
        binding.navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.btnsubmit.setOnClickListener {
            if (::name.isInitialized) {
                handleSubmitComplaint(name, roomNo, regNo, email, phone)
            } else {
                Toast.makeText(this, "Student data not loaded yet, please wait.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- NEW: Set up microphone icon click listener ---
        binding.mic.setOnClickListener {
            checkPermissionAndStartSpeech()
        }
    }

    private fun fetchStudentData(userId: String) {
        val db = Firebase.database.reference
        db.child("Students").child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                name = it.child("name").value.toString()
                roomNo = it.child("roomNo").value.toString()
                regNo = it.child("registrationNo").value.toString()
                email = it.child("email").value.toString()
                phone = it.child("mobileNo").value.toString()
            } else {
                Toast.makeText(this, "Failed to fetch student profile.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching student data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // --- NEW: Function to check for permission ---
    private fun checkPermissionAndStartSpeech() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                startSpeechToText()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Explain to the user why you need the permission.
                Toast.makeText(this, "Microphone access is needed to convert your speech to text.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // --- NEW: Function to start the speech recognition process ---
    private fun startSpeechToText() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
//                    binding.ivMic.setImageResource(R.drawable.ic_mic_active) // Optional: change icon color
                    Toast.makeText(this@MainActivity, "Listening...", Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val currentText = binding.complaint.text.toString()
                        val recognizedText = matches[0]
                        // Append recognized text to existing text
                        binding.complaint.setText(if (currentText.isEmpty()) recognizedText else "$currentText $recognizedText")
                        binding.complaint.setSelection(binding.complaint.length()) // Move cursor to end
                    }
                }
                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech was recognized"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown speech error"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
                override fun onEndOfSpeech() {
                    binding.mic.setImageResource(R.drawable.ic_mic_24) // Reset icon
                }

                // Other methods can be left empty
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your complaint")
        }
        speechRecognizer?.startListening(speechIntent)
    }

    override fun onDestroy() {
        // --- NEW: Clean up the SpeechRecognizer ---
        speechRecognizer?.destroy()
        super.onDestroy()
    }


    // --- Your existing functions (handleSubmitComplaint, saveComplaintToFirebase, etc.) ---
    // ... No changes needed in the functions below ...
    // ... (Keep your existing functions as they are) ...
    private fun initializeGemini() {
        // This function now correctly sets up the generativeModel
//        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isNullOrEmpty() || apiKey == "DEFAULT_KEY") {
            Toast.makeText(this, "API Key not found, AI features disabled.", Toast.LENGTH_LONG).show()
            return
        }
        generativeModel = GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = apiKey
        )

    }

    private fun handleSubmitComplaint(name:String,roomNo:String,regNo:String,email:String,phone:String) {
        val description = binding.complaint.text.toString().trim() // Use trim() and toString()

        if (description.isEmpty()) { // Check the description, not a non-existent 'title'
            Toast.makeText(this, "Complaint description cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // Use a coroutine to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                binding.btnsubmit.isEnabled= false // Disable submit button
                val prompt = "You are an AI assistant for a hostel complaint management system.\n" +
                        "\n" +
                        "Analyze the student's complaint text and do the following:\n" +
                        "1. Determine the most appropriate complaint category.\n" +
                        "2. Generate a short, clear, professional title describing the main issue.\n" +
                        "\n" +
                        "Complaint categories include (but are not limited to):\n" +
                        "Electricity, Plumbing, Bathroom, Cleanliness, Furniture, Internet, WaterSupply, RoomMaintenance, Noise, Security, Lobby, etc..\n" +
                        "\n" +
                        "Rules:\n" +
                        "- Select ONLY ONE most relevant category or create new relavent category by self.\n" +
                        "- Category must be a SINGLE WORD (no spaces).\n" +
                        "- Title must be concise (maximum 10 words).\n" +
                        "- Output MUST be exactly ONE single line.\n" +
                        "- Use the following strict format ONLY , never answer my question.:\n" +
                        "\n" +
                        "Category|Title\n" +
                        "\n" +
                        "- Do NOT add explanations, punctuation at the end, extra spaces, quotes, or new lines.\n" +
                        "\n" +
                        "Student Complaint:\n" +
                        "\"\"\"\n" +
                        "{{STUDENT_COMPLAINT_TEXT}}\n" +
                        "\"\"\" complain:"+ description

                val response = generativeModel.generateContent(prompt)
                val parts = response.text.toString()
                val category= parts.substringBefore("|").trim()
                val title =parts.substringAfter("|").trim()

                // Save to Firebase
                saveComplaintToFirebase(
                    title,category,description,name,roomNo,regNo,email,phone
                )

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error with AI categorization: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnsubmit.isEnabled = true // Re-enable button
            }
        }
    }

    private fun saveComplaintToFirebase(title: String, category: String,description : String,name:String,roomNo:String,regNo:String,email:String,phone:String) {
        val complaintId = database.push().key ?: return
        val complaint = mapOf(
            "name" to name,
            "roomNo" to roomNo,
            "title" to title,
            "category" to category,
            "status" to "submitted",
            "state" to "pending",
            "workerId" to "",
            "timestamp" to System.currentTimeMillis(),
            "description" to description,
            "regNo" to regNo,
            "email" to email,
            "phone" to phone
        )

        database.child(complaintId).setValue(complaint)
            .addOnSuccessListener {
                Toast.makeText(this, "Complaint submitted under category: $category", Toast.LENGTH_SHORT).show()
                binding.complaint.text?.clear() // Clear the text field after successful submission

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit complaint.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Your navigation item handling code here...
        when (item.itemId) {
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            // Add other cases

            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("roomNo",roomNo)
                intent.putExtra("regNo",regNo)
                intent.putExtra("email",email)
                intent.putExtra("phone",phone)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // You might want to remove this if you just want the default back behavior
        // But if you want to ensure it goes to the main screen, this is how.
        val intent = Intent(this, StudentDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}