package com.example.halamantiket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFF0F5) // Soft Lavender Blush Background
                ) {
                    TiketParentScreen()
                }
            }
        }
    }
}

// ==================== PARENT COMPOSABLE (STATE HOLDER) ====================
@Composable
fun TiketParentScreen() {
    // State dikelola oleh Parent
    var namaPembeli by rememberSaveable { mutableStateOf("") }
    var jumlahTiket by rememberSaveable { mutableStateOf(1) }
    val hargaTiketPerTiket = 50000
    val totalHarga = jumlahTiket * hargaTiketPerTiket

    var isProcessing by remember { mutableStateOf(false) }
    var triggerProcessSignal by remember { mutableStateOf(0) }
    var statusPesan by remember { mutableStateOf("Silakan pesan tiket") }
    var statusType by remember { mutableStateOf("normal") }

    LaunchedEffect(triggerProcessSignal) {
        if (triggerProcessSignal > 0) {
            isProcessing = true
            statusType = "loading"
            statusPesan = "Memproses pesanan..."

            delay(2000)

            isProcessing = false
            statusType = "success"
            statusPesan = "Tiket berhasil dipesan!"
        }
    }

    fun handlePesanClick() {
        if (namaPembeli.trim().isEmpty()) {
            statusType = "error"
            statusPesan = "Nama harus diisi"
        } else {
            triggerProcessSignal++
        }
    }

    TiketContent(
        namaPembeli = namaPembeli,
        onNamaChange = { namaPembeli = it },
        jumlahTiket = jumlahTiket,
        onTambahTiket = { jumlahTiket++ },
        onKurangTiket = { if (jumlahTiket > 1) jumlahTiket-- },
        totalHarga = totalHarga,
        statusPesan = statusPesan,
        statusType = statusType,
        isProcessing = isProcessing,
        onPesanClick = { handlePesanClick() }
    )
}

// ==================== CHILD COMPOSABLE (STATELESS UI) ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiketContent(
    namaPembeli: String,
    onNamaChange: (String) -> Unit,
    jumlahTiket: Int,
    onTambahTiket: () -> Unit,
    onKurangTiket: () -> Unit,
    totalHarga: Int,
    statusPesan: String,
    statusType: String,
    isProcessing: Boolean,
    onPesanClick: () -> Unit
) {
    // Definisi Warna Tema Pink 🌸
    val primaryPink = Color(0xFFFAA2C2)      // Main Pink
    val lightPink = Color(0xFFFCE4EC)        // Light Pink Button / Container
    val disabledPink = Color(0xFFF8BBD0)     // Disabled Button Pink

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar Header
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Pemesanan Tiket",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = primaryPink
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Nama Pembeli
            Text(text = "Nama", fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
            OutlinedTextField(
                value = namaPembeli,
                onValueChange = onNamaChange,
                placeholder = { Text("Masukkan nama Anda") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryPink,
                    unfocusedBorderColor = Color(0xFFF48FB1)
                )
            )

            // Input Jumlah Tiket (- 1 +)
            Text(text = "Jumlah Tiket", fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onKurangTiket,
                    enabled = !isProcessing && jumlahTiket > 1,
                    modifier = Modifier.size(56.dp, 40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lightPink)
                ) {
                    Text("-", color = primaryPink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "$jumlahTiket",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF880E4F)
                )

                Button(
                    onClick = onTambahTiket,
                    enabled = !isProcessing,
                    modifier = Modifier.size(56.dp, 40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lightPink)
                ) {
                    Text("+", color = primaryPink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Pesan Tiket
            Button(
                onClick = onPesanClick,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryPink,
                    disabledContainerColor = disabledPink
                )
            ) {
                Text(
                    text = "Pesan Tiket",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Box Card Status
            val (bgColor, textColor, iconText) = when (statusType) {
                "loading" -> Triple(lightPink, primaryPink, "⏳ ")
                "success" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "✅ ")
                "error" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "❗ ")
                else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), "")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = bgColor, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = primaryPink,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else if (iconText.isNotEmpty()) {
                        Text(text = iconText)
                    }
                    Text(
                        text = "Status: $statusPesan",
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}