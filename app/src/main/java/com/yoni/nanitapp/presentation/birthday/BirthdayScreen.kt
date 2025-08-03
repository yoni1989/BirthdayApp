package com.yoni.nanitapp.presentation.birthday

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.yoni.nanitapp.R
import com.yoni.nanitapp.domain.AgeInfo
import com.yoni.nanitapp.domain.AgeUnit
import com.yoni.nanitapp.domain.BirthdayTheme
import com.yoni.nanitapp.ui.theme.NanitAppTheme
import java.io.File

@Composable
fun BirthdayRoute(viewModel: BirthdayViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showPhotoDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updatePhotoUri(it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let {
                viewModel.updatePhotoUri(it.toString())
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file =
                File(context.externalCacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
            photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            photoUri?.let {
                cameraLauncher.launch(it)
            } ?: run {
                // TODO: Handle failed to create photo URI
            }
        }
    }

    if (showPhotoDialog) {
        PhotoSelectionDialog(
            onGalleryClick = {
                showPhotoDialog = false
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                showPhotoDialog = false

                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                        val file = File(
                            context.externalCacheDir,
                            "temp_photo_${System.currentTimeMillis()}.jpg"
                        )
                        photoUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        photoUri?.let {
                            cameraLauncher.launch(it)
                        }
                    }

                    else -> {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            onDismiss = {
                showPhotoDialog = false
            }
        )
    }

    BirthdayScreen(
        uiState = uiState,
        onPhotoClick = { showPhotoDialog = true }
    )
}

@Composable
fun BirthdayScreen(
    uiState: BirthdayScreenUiState,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (val currentUiState = uiState) {
        is BirthdayScreenUiState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is BirthdayScreenUiState.Error -> {
            ErrorScreen(
                message = currentUiState.message
            )
        }

        is BirthdayScreenUiState.Success -> {
            BabyMilestoneScreen(
                uiState = currentUiState,
                photoUri = uiState.photoUri,
                onPhotoClick = onPhotoClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BabyMilestoneScreen(
    uiState: BirthdayScreenUiState.Success,
    photoUri: String?,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = uiState.themeResources.backgroundDrawable),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (titleText, ageRow, ageTextRef, iconGroup) = createRefs()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(iconGroup) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onPhotoClick() }
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = uiState.themeResources.borderedIcon),
                            tint = Color.Unspecified,
                            contentDescription = "baby icon"
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera_blue),
                        contentDescription = "Camera",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 70.dp, y = (-70).dp)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_nanit_logo),
                    tint = Color.Unspecified,
                    contentDescription = "Nanit logo"
                )
            }

            Text(
                text = "TODAY ${uiState.name.uppercase()} IS",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top, margin = 14.dp)
                    start.linkTo(iconGroup.start)
                    end.linkTo(iconGroup.end)
                    width = Dimension.fillToConstraints
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                modifier = Modifier.constrainAs(ageRow) {
                    top.linkTo(titleText.bottom, margin = 13.dp)
                    centerHorizontallyTo(parent)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_wind_left),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = uiState.numberIconResource),
                    tint = Color.Unspecified,
                    contentDescription = "Age number: ${uiState.ageInfo.value}"
                )

                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_wind_right),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )
            }

            Text(
                text = uiState.ageText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(ageTextRef) {
                    top.linkTo(ageRow.bottom, margin = 14.dp)
                    centerHorizontallyTo(parent)
                }
            )
        }
    }
}


@Composable
fun PhotoSelectionDialog(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Photo")
        },
        text = {
            Text("Choose how you'd like to add a photo")
        },
        confirmButton = {
            TextButton(onClick = onGalleryClick) {
                Text("Gallery")
            }
        },
        dismissButton = {
            TextButton(onClick = onCameraClick) {
                Text("Camera")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun BirthdayScreenPreview() {
    NanitAppTheme {
        BabyMilestoneScreen(
            uiState = BirthdayScreenUiState.Success(
                name = "Baby",
                ageInfo = AgeInfo(value = 1, unit = AgeUnit.MONTHS),
                themeResources = BirthdayTheme.PELICAN.toThemeResources(),
                numberIconResource = 1.toNumberIconResource(),
                ageText = AgeInfo(value = 1, unit = AgeUnit.MONTHS).toDisplayText(),
            ),
            photoUri = null,
            onPhotoClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    NanitAppTheme {
        ErrorScreen(
            message = "Failed to connect to server. Please check your connection."
        )
    }
}