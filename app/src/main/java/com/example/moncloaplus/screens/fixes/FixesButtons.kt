package com.example.moncloaplus.screens.fixes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.moncloaplus.R
import com.example.moncloaplus.model.FixState
import com.example.moncloaplus.model.FixViewModel
import com.example.moncloaplus.model.User
import kotlin.math.exp

@Composable
fun FixDialog(
    currentUser: User,
    viewModel: FixViewModel,
    onDismiss: () -> Unit
) {
    val description by viewModel.description.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val descriptionError = description.isEmpty()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.updateImageUri(it) }
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        val focusManager = LocalFocusManager.current

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(36.dp))
                Icon(
                    painter = painterResource(R.drawable.handyman_24px__1_),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.padding(bottom = 32.dp),
                    text = "Nuevo arreglo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(thickness = 2.dp)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Localización:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = currentUser.roomNumber,
                        fontSize = 16.sp
                    )
                }
                HorizontalDivider(thickness = 2.dp)

                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Descripción", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.fillMaxWidth().focusable(true).padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    maxLines = 3,
                    singleLine = false,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    isError = descriptionError,
                    supportingText = {
                        if (descriptionError) {
                            Text("La descripción es obligatoria", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Button(onClick = { imagePickerLauncher.launch("image/*") }, enabled = !isLoading) {
                    Text(text = if (imageUri == null) "Seleccionar imagen" else "Cambiar imagen")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar", fontSize = 16.sp) }
                    TextButton(
                        onClick = { viewModel.createFix() },
                        enabled = !isLoading && !descriptionError
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else Text("Guardar", fontSize = 16.sp)
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixesSegmentedButton(
    options: List<String>,
    onOptionClick: (Int) -> Unit
) {
    var selectedOption by remember { mutableIntStateOf(PENDING_INDEX) }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.scale(0.9f)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = selectedOption == index

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = MaterialTheme.shapes.extraLarge
                ),
                onClick = {
                    selectedOption = index
                    onOptionClick(index)
                },
                selected = isSelected,
                label = { Text(label) },
//                colors = SegmentedButtonDefaults.colors(
//                    activeContainerColor = FIXES_CONTAINER_COLORS[index],
//                    activeContentColor = MaterialTheme.colorScheme.scrim
//                )
            )
        }
    }
}

@Composable
fun NewFixButton(
    currentUser: User,
    viewModel: FixViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { showDialog = true }
    ) { Icon(Icons.Filled.Add, null) }

    if (showDialog) {
        FixDialog(
            currentUser = currentUser,
            viewModel = viewModel,
            onDismiss = {
                viewModel.resetValues()
                showDialog = false
            }
        )
    }

}

@Composable
fun EditFixDialog(
    currentUser: User,
    viewModel: FixViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val description by viewModel.description.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val descriptionError = description.isEmpty()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.updateImageUri(it) }
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        val focusManager = LocalFocusManager.current

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(36.dp))
                Icon(
                    painter = painterResource(R.drawable.handyman_24px__1_),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.padding(bottom = 32.dp),
                    text = "Editando arreglo...",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(thickness = 2.dp)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Localización:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = currentUser.roomNumber,
                        fontSize = 16.sp
                    )
                }
                HorizontalDivider(thickness = 2.dp)

                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Descripción", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.fillMaxWidth().focusable(true).padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                    maxLines = 3,
                    singleLine = false,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    isError = descriptionError,
                    supportingText = {
                        if (descriptionError) {
                            Text("La descripción es obligatoria", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Button(onClick = { imagePickerLauncher.launch("image/*") }, enabled = !isLoading) {
                    Text(text = if (imageUri == null) "Seleccionar imagen" else "Cambiar imagen")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar", fontSize = 16.sp) }
                    TextButton(
                        onClick = onConfirm,
                        enabled = !isLoading && !descriptionError
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else Text("Guardar cambios", fontSize = 16.sp)
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdown(
    currentState: FixState,
    onStateChange: (FixState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = FixState.entries
    val selectedLabel = currentState.name

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Estado") },
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach{ option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onStateChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
