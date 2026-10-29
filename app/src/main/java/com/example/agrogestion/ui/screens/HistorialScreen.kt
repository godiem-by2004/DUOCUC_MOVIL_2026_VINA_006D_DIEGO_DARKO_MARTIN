package com.example.agrogestion.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.agrogestion.ui.components.BannerSincronizacion
import com.example.agrogestion.util.FormatoCoordenada
import com.example.agrogestion.util.FormatoFecha
import java.io.File
import java.util.Locale
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.data.local.RegistroEntity
import com.example.agrogestion.ui.components.AgroTopBar
import com.example.agrogestion.ui.theme.AgroGestionTheme
import com.example.agrogestion.ui.theme.AgroMintContainer
import com.example.agrogestion.ui.theme.AgroMintLight
import com.example.agrogestion.ui.theme.AgroNeutralBg
import com.example.agrogestion.ui.theme.AgroPrimary
import com.example.agrogestion.ui.theme.AgroSurfaceVariant
import com.example.agrogestion.ui.theme.AgroTertiary
import com.example.agrogestion.ui.theme.AgroTextPrimary
import com.example.agrogestion.ui.theme.AgroTextSecondary
import com.example.agrogestion.ui.theme.AgroWarningRed
import com.example.agrogestion.viewmodel.AgroDashboardUiState

@Composable
fun HistorialScreen(
    uiState: AgroDashboardUiState,
    onFiltroChange: (String) -> Unit,
    onNombreAsesorChange: (String) -> Unit,
    onEliminarRegistro: (Long) -> Unit,
    onActualizarObservaciones: (Long, String) -> Unit,
    onSyncClick: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    var editandoAsesor by remember { mutableStateOf(false) }
    var textoAsesor by remember(uiState.nombreAsesor) { mutableStateOf(uiState.nombreAsesor) }
    val nombreAsesorValido = textoAsesor.trim().length >= 3

    val registrosFiltrados = uiState.registrosFiltrados

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroNeutralBg)
    ) {
        AgroTopBar(
            subtituloVista = "Historial",
            pendientesRoom = uiState.pendientesSincronizar,
            sincronizando = uiState.sincronizando,
            onSyncClick = onSyncClick,
            onCerrarSesion = onCerrarSesion
        )

        BannerSincronizacion(uiState.mensajeSincronizacion)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = AgroPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "OPERADOR ACTIVO (DataStore)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTextSecondary
                                    )
                                    Text(
                                        text = uiState.nombreAsesor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTextPrimary
                                    )
                                }
                            }

                            Surface(
                                color = AgroNeutralBg,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.clickable { editandoAsesor = !editandoAsesor }
                            ) {
                                Text(
                                    text = if (editandoAsesor) "Cerrar" else "Editar Perfil",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = editandoAsesor) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = textoAsesor,
                                    onValueChange = { textoAsesor = it },
                                    label = { Text("Nombre del Asesor Agronómico") },
                                    singleLine = true,
                                    isError = !nombreAsesorValido,
                                    supportingText = {
                                        if (!nombreAsesorValido) Text("Mínimo 3 caracteres", color = AgroWarningRed)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    enabled = nombreAsesorValido,
                                    onClick = {
                                        onNombreAsesorChange(textoAsesor)
                                        editandoAsesor = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AgroPrimary)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onSyncClick,
                            colors = ButtonDefaults.buttonColors(containerColor = AgroPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = AgroMintLight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.pendientesSincronizar > 0) {
                                    "Sincronizar ${uiState.pendientesSincronizar} Registros Locales con API"
                                } else {
                                    "Base de Datos Sincronizada al 100%"
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.filtroHistorial,
                    onValueChange = onFiltroChange,
                    label = { Text("Filtrar historial por predio, cuartel o cultivo...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(registrosFiltrados, key = { it.id }) { registro ->
                TarjetaRegistroHistorial(
                    registro = registro,
                    onEliminar = { onEliminarRegistro(registro.id) },
                    onActualizarObservaciones = { texto -> onActualizarObservaciones(registro.id, texto) }
                )
            }

            if (registrosFiltrados.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = AgroTextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.filtroHistorial.isBlank()) {
                                    "Aún no hay muestras registradas. Crea una en \"Nuevo Registro\"."
                                } else {
                                    "No se encontraron muestras para el filtro ingresado."
                                },
                                color = AgroTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaRegistroHistorial(
    registro: RegistroEntity,
    onEliminar: () -> Unit,
    onActualizarObservaciones: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var textoEditado by remember(registro.observacionesFoliares) { mutableStateOf(registro.observacionesFoliares) }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar muestra?") },
            text = { Text("Se borrará el registro de ${registro.predio} · ${registro.cuartel} y su fotografía. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; onEliminar() }) {
                    Text("Eliminar", color = AgroWarningRed)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }

    if (editando) {
        AlertDialog(
            onDismissRequest = { editando = false },
            title = { Text("Editar diagnóstico foliar") },
            text = {
                OutlinedTextField(
                    value = textoEditado,
                    onValueChange = { textoEditado = it },
                    isError = textoEditado.trim().length < 5,
                    supportingText = { if (textoEditado.trim().length < 5) Text("Mínimo 5 caracteres") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    enabled = textoEditado.trim().length >= 5,
                    onClick = { editando = false; onActualizarObservaciones(textoEditado) }
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editando = false }) { Text("Cancelar") } }
        )
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${registro.predio} · ${registro.cuartel}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = AgroTextPrimary
                        )
                    }
                    Text(
                        text = "${registro.cultivo} (${registro.etapa}) · ${FormatoFecha.relativa(registro.fechaMillis)}",
                        fontSize = 12.sp,
                        color = AgroTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = if (registro.sincronizado) AgroMintContainer else AgroSurfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (registro.sincronizado) "Sincronizado" else "Room Local",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChipParametro("pH ${registro.ph}")
                ChipParametro("CE ${registro.ec} dS/m")
                ChipParametro("N ${registro.nitrogeno.toInt()}")
                ChipParametro("P ${registro.fosforo.toInt()}")
                ChipParametro("K ${registro.potasio.toInt()}")
            }

            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AgroNeutralBg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AgroPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "GPS: ${FormatoCoordenada.texto(registro.latitud, registro.longitud)} " + when {
                                registro.gpsDesdeSensor && registro.precisionMetros != null ->
                                    "(sensor, ±${String.format(Locale.US, "%.0f", registro.precisionMetros)} m)"
                                registro.gpsDesdeSensor -> "(sensor)"
                                else -> "(referencia del predio)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AgroTextPrimary
                        )
                    }

                    if (registro.fotoUri != null) {
                        AsyncImage(
                            model = File(registro.fotoUri),
                            contentDescription = "Fotografía foliar de la muestra",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Text(
                        text = "📎 Diagnóstico foliar: ${registro.observacionesFoliares}",
                        fontSize = 12.sp,
                        color = AgroTextSecondary
                    )

                    Text(
                        text = "🧪 Prescripción: ${registro.recomendacion}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AgroTertiary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { editando = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar diagnóstico foliar",
                                tint = AgroPrimary
                            )
                        }
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Eliminar muestra",
                                tint = AgroWarningRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipParametro(texto: String) {
    Surface(
        color = AgroNeutralBg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AgroPrimary,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistorialScreenPreview() {
    AgroGestionTheme {
        HistorialScreen(
            uiState = AgroDashboardUiState(),
            onFiltroChange = {},
            onNombreAsesorChange = {},
            onEliminarRegistro = {},
            onActualizarObservaciones = { _, _ -> },
            onSyncClick = {}
        )
    }
}
