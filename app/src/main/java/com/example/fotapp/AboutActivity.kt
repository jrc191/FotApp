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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    context: android.content.Context,
    onSendEmail: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_futconnect),
            contentDescription = stringResource(R.string.logo_desc, stringResource(R.string.app_name)),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.app_name),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.splash_tagline), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.version, "1.0.0"), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSendEmail,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Icon(
                Icons.Default.Email,
                contentDescription = "Enviar correo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.contact))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(stringResource(R.string.back))
        }
    }
}