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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.moncloaplus.R
import com.example.moncloaplus.model.FixState
import com.example.moncloaplus.model.FixViewModel
import com.example.moncloaplus.model.User

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
                        onClick = {
                            viewModel.createFix()
                            onDismiss()
                        },
                        enabled = !isLoading && !descriptionError
                    ) {
                        Text("Guardar", fontSize = 16.sp)
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
    var selectedOption by remember { mutableIntStateOf(0) }

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
            onDismiss = { showDialog = false }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StateDropdown(
    currentState: FixState,
    onStateChange: (FixState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = FIXES_STATES_NAMES
    val selectedLabel = options.getOrNull(currentState.ordinal) ?: currentState.name
    val focusManager = LocalFocusManager.current

    val stateBorderColor = when (currentState) {
        FixState.PENDING -> FixesColors.pendingContainer
        FixState.IN_PROGRESS -> FixesColors.inProgressContainer
        else -> FixesColors.fixedContainer
    }

    val isFixed = currentState == FixState.FIXED

    ExposedDropdownMenuBox(
        modifier = Modifier.scale(0.9f),
        expanded = expanded,
        onExpandedChange = {
            if (!isFixed) {
                expanded = it
                if (!it) focusManager.clearFocus()
            }
        }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = !isFixed,
            label = { Text("Estado", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.scrim
            ),
            modifier = Modifier
                .height(58.dp)
                .width(130.dp)
                .menuAnchor()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        expanded = false
                    }
                },
            trailingIcon = {
                if (!isFixed) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = stateBorderColor,
                focusedBorderColor = stateBorderColor,
                disabledBorderColor = stateBorderColor,
                disabledTextColor = MaterialTheme.colorScheme.scrim
            )
        )
        if (!isFixed) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    focusManager.clearFocus()
                }
            ) {
                options.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onStateChange(FixState.entries[index])
                            expanded = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}
