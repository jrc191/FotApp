package com.example.fotapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.fotapp.ui.screens.AboutScreen
import com.example.fotapp.ui.theme.FotAppTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FotAppTheme {
                AboutScreen(
                    context = this,
                    onBackClick = { finish() },
                    onSendEmail = { sendEmail() }
                )
            }
        }
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.email_contact.toString()))
            putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject.toString())
            putExtra(Intent.EXTRA_TEXT, R.string.email_body.toString())
        }
        startActivity(Intent.createChooser(intent, R.string.send_email.toString()))
    }
}

