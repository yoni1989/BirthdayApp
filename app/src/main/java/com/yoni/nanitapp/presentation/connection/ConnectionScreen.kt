package com.yoni.nanitapp.presentation.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yoni.nanitapp.R
import com.yoni.nanitapp.ui.theme.NanitAppTheme

@Composable
fun ConnectionRoute(
    onNavigateToBirthday: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateToBirthday -> {
                    onNavigateToBirthday()
                }
            }
        }
    }

    ConnectionScreen(
        uiState = uiState,
        onIpAddressChange = viewModel::updateIpAddress,
        onPortChange = viewModel::updatePort,
        onConnect = viewModel::connect
    )
}

@Composable
private fun ConnectionScreen(
    uiState: ConnectionUiState,
    onIpAddressChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onConnect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.connect_to_birthday_server),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Enter the server IP address and port to connect",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = uiState.ipAddress,
                    onValueChange = onIpAddressChange,
                    label = { Text("IP Address") },
                    placeholder = { Text("192.168.1.100") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.connectionStatus != ConnectionStatus.CONNECTING,
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.port,
                    onValueChange = onPortChange,
                    label = { Text("Port") },
                    placeholder = { Text("8080") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.connectionStatus != ConnectionStatus.CONNECTING,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Text(
                    text = uiState.connectionStatusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = when {
                        uiState.receivedBirthdayData -> MaterialTheme.colorScheme.primary
                        uiState.connectionStatus == ConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.primary
                        uiState.errorMessage != null -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                if (uiState.receivedBirthdayData) {
                    Text(
                        text = "Birthday data received successfully",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Button(
                        onClick = onConnect,
                        enabled = uiState.connectionStatus != ConnectionStatus.CONNECTING && uiState.ipAddress.isNotBlank() && uiState.port.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (uiState.connectionStatus == ConnectionStatus.CONNECTING) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            when (uiState.connectionStatus) {
                                ConnectionStatus.CONNECTING -> "Connecting..."
                                ConnectionStatus.CONNECTED -> "Connected - Waiting for data"
                                ConnectionStatus.IDLE -> "Connect"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionScreenPreview() {
    NanitAppTheme {
        ConnectionScreen(
            uiState = ConnectionUiState(
                ipAddress = "192.168.1.100",
                port = "8080"
            ),
            onIpAddressChange = {},
            onPortChange = {},
            onConnect = {}
        )
    }
}