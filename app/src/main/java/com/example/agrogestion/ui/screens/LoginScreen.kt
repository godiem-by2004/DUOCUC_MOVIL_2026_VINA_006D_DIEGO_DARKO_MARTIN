package com.example.agrogestion.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.ui.theme.AgroGestionTheme
import com.example.agrogestion.ui.theme.AgroMintLight
import com.example.agrogestion.ui.theme.AgroNeutralBg
import com.example.agrogestion.ui.theme.AgroPrimary
import com.example.agrogestion.ui.theme.AgroSecondary
import com.example.agrogestion.ui.theme.AgroTextPrimary
import com.example.agrogestion.ui.theme.AgroTextSecondary
import com.example.agrogestion.ui.theme.AgroWarningBg
import com.example.agrogestion.ui.theme.AgroWarningRed
import com.example.agrogestion.viewmodel.LoginUiState
import com.example.agrogestion.viewmodel.ValidacionCampo

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onCorreoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onIngresarClick: () -> Unit
) {
    var claveVisible by rememberSaveable { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroNeutralBg)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AgroPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Logo AgroGestión",
                    tint = AgroSecondary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AgroGestión Terreno",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AgroPrimary
            )
            Text(
                text = "Acceso de asesores en terreno",
                fontSize = 14.sp,
                color = AgroTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoLogin(
                        valor = uiState.correo,
                        onValueChange = onCorreoChange,
                        etiqueta = "Correo electrónico",
                        iconoInicial = Icons.Default.Email,
                        validacion = uiState.valCorreo,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    CampoLogin(
                        valor = uiState.clave,
                        onValueChange = onClaveChange,
                        etiqueta = "Contraseña",
                        iconoInicial = Icons.Default.Lock,
                        validacion = uiState.valClave,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { onIngresarClick() }),
                        visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        accionExtra = {
                            IconButton(onClick = { claveVisible = !claveVisible }) {
                                Icon(
                                    imageVector = if (claveVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = AgroTextSecondary
                                )
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = uiState.errorCredenciales != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            color = AgroWarningBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = AgroWarningRed
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.errorCredenciales.orEmpty(),
                                    color = AgroWarningRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onIngresarClick,
                        enabled = !uiState.cargando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgroPrimary,
                            disabledContainerColor = AgroPrimary.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (uiState.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Ingresar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = AgroMintLight.copy(alpha = 0.4f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Usuario de demostración: asesor@agrogestion.cl",
                    fontSize = 12.sp,
                    color = AgroTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun CampoLogin(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    iconoInicial: ImageVector,
    validacion: ValidacionCampo,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    accionExtra: (@Composable () -> Unit)? = null
) {
    val tieneError = !validacion.esValido
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(imageVector = iconoInicial, contentDescription = null, tint = AgroPrimary) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                accionExtra?.invoke()
                if (tieneError) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error de validación",
                        tint = AgroWarningRed,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                } else if (valor.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Campo válido",
                        tint = AgroSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        },
        isError = tieneError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        supportingText = {
            if (tieneError && validacion.mensajeError != null) {
                Text(text = validacion.mensajeError, color = AgroWarningRed, fontSize = 11.sp)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AgroGestionTheme {
        LoginScreen(
            uiState = LoginUiState(),
            onCorreoChange = {},
            onClaveChange = {},
            onIngresarClick = {}
        )
    }
}
