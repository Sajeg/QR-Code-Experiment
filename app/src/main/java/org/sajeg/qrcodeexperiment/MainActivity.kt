package org.sajeg.qrcodeexperiment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import org.sajeg.qrcodeexperiment.ui.theme.QRCodeExperimentTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRCodeExperimentTheme {
                Main()
            }
        }
    }
}

@Composable
private fun Main() {
    var bigText by remember {
        mutableStateOf(
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et e"
        )
    }
    bigText = bigText.trim().replace("\n", "")
    val chunkText = splitString(bigText)
    var text by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            for (smallText in chunkText) {
                text =
                    "[${chunkText.indexOf(smallText)};${chunkText.lastIndex}]" + smallText
                delay(1370)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = rememberQrCodePainter(
                    data = text,
                    colors = QrColors(QrBrush.solid(Color.White)),
                ),
                contentDescription = "QR code referring to the example.com website",
                alignment = Alignment.Center,
            )
            val context = LocalContext.current
            var result:MutableList<String> = mutableListOf()
            Button(onClick = {
                result = scanQrCodes(context)
            }) {
                Text(text = "Scan QR Code")
            }
            Text(text = result.toString())
        }
    }
}

fun splitString(text: String, chunkSize: Int = 100): List<String> {
    val chunks = mutableListOf<String>()
    var startIndex = 0
    while (startIndex < text.length) {
        val endIndex = minOf(startIndex + chunkSize, text.length)
        chunks.add(text.substring(startIndex, endIndex))
        startIndex = endIndex
    }
    return chunks
}

fun scanQrCodes(
    context: Context,
    inputList: MutableList<String> = mutableListOf()
): MutableList<String> {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        )
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)
    var result = inputList
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            val rawData: String = barcode.rawValue.toString()
            val metaData: List<String> = rawData.split("[", "]")[1].split(";")
            val index: Int = metaData[0].toInt()
            val total: Int = metaData[1].toInt()
            val scanResult: String = rawData.split("[", "]")[2]
            while (result.size <= total) {
                result.add(result.size, "")
            }
            if (!result.contains(scanResult)) {
                result[index] = scanResult
            }
            Log.d("ScanResult", result.toString())
            if (result.contains("")) {
                result = scanQrCodes(context, result)
            }
        }
    return result
}