package com.example.agrogestion.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.model.PredioInfo
import com.example.agrogestion.ui.components.AgroTopBar
import com.example.agrogestion.ui.components.BannerSincronizacion
import com.example.agrogestion.ui.theme.AgroGestionTheme
import com.example.agrogestion.ui.theme.AgroMintContainer
import com.example.agrogestion.ui.theme.AgroMintLight
import com.example.agrogestion.ui.theme.AgroNeutralBg
import com.example.agrogestion.ui.theme.AgroPrimary
import com.example.agrogestion.ui.theme.AgroSecondary
import com.example.agrogestion.ui.theme.AgroSurfaceVariant
import com.example.agrogestion.ui.theme.AgroTertiary
import com.example.agrogestion.ui.theme.AgroTextPrimary
import com.example.agrogestion.ui.theme.AgroTextSecondary
import com.example.agrogestion.util.FormatoFecha
import com.example.agrogestion.viewmodel.AgroDashboardUiState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrediosScreen(
    uiState: AgroDashboardUiState,
    onPredioSelected: (PredioInfo) -> Unit,
    onNavigateNuevoRegistro: () -> Unit,
    onNavigateFertirriego: () -> Unit,
    onSyncClick: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    var menuPrediosAbierto by remember { mutableStateOf(false) }
    val predio = uiState.predioSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroNeutralBg)
    ) {
        AgroTopBar(
            subtituloVista = "Predios",
            pendientesRoom = uiState.pendientesSincronizar,
            sincronizando = uiState.sincronizando,
            onSyncClick = onSyncClick,
            onCerrarSesion = onCerrarSesion
        )

        BannerSincronizacion(uiState.mensajeSincronizacion)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PREDIO SELECCIONADO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AgroTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = AgroMintContainer,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SatelliteAlt,
                                        contentDescription = null,
                                        tint = AgroPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Predio georreferenciado",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroPrimary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box {
                            Surface(
                                color = AgroNeutralBg,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { menuPrediosAbierto = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = predio.nombre,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AgroTextPrimary
                                        )
                                        Text(
                                            text = predio.ubicacion,
                                            fontSize = 13.sp,
                                            color = AgroTextSecondary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Cambiar predio",
                                            tint = AgroTextPrimary
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = menuPrediosAbierto,
                                onDismissRequest = { menuPrediosAbierto = false }
                            ) {
                                CatalogoAgronomico.prediosDisponibles.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(opcion.nombre, fontWeight = FontWeight.Bold)
                                                Text(
                                                    "${opcion.cultivo} · ${opcion.ubicacion}",
                                                    fontSize = 12.sp,
                                                    color = AgroTextSecondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            onPredioSelected(opcion)
                                            menuPrediosAbierto = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = AgroPrimary,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Park,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${predio.cultivo} · ${predio.cuartelPrincipal} (${predio.superficieHa} ha)",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Surface(
                                color = AgroNeutralBg,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = AgroPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = predio.sistemaRiego,
                                        color = AgroTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AgroPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AgroTertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = AgroMintLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Base Local Room Activa",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                )

                                Surface(
                                    color = AgroTertiary,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "${uiState.pendientesSincronizar} pendientes",
                                        color = AgroMintLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${uiState.registros.size} registros guardados localmente · ${uiState.pendientesSincronizar} por sincronizar. La sincronización con el servidor se hará al reconectar Wi-Fi/4G.",
                                color = Color(0xFFD8F3DC),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TELEMETRÍA Y ESTADO DE CUARTEL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AgroTextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AgroSecondary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.ultimoRegistroPredio
                                ?.let { "Última muestra: ${FormatoFecha.relativa(it.fechaMillis)}" }
                                ?: "Sin muestras",
                            color = AgroTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tarjeta pH
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "pH Suelo Promedio",
                                    fontSize = 12.sp,
                                    color = AgroTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = AgroSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = uiState.phActual?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AgroTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "pH",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgroTextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = AgroTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        uiState.phActual == null -> "Sin muestras"
                                        uiState.phActual!! in 5.8..7.2 -> "Rango Óptimo"
                                        else -> "Ajustar Tampón"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTertiary
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "C.E. Agua Riego",
                                    fontSize = 12.sp,
                                    color = AgroTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = AgroSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = uiState.ceActual?.let { String.format(Locale.US, "%.1f", it) } ?: "—",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AgroTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "dS/m",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgroTextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = AgroTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        uiState.ceActual == null -> "Sin muestras"
                                        uiState.ceActual!! <= 2.0 -> "Rango normal"
                                        else -> "Revisar salinidad"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AgroTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AgroMintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grain,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FENOLOGÍA ACTUAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextSecondary
                            )
                            Text(
                                text = predio.etapaActual,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextPrimary
                            )
                            Text(
                                text = predio.subEtapaDetalle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AgroTertiary
                            )
                        }

                        Surface(
                            color = AgroMintLight,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = predio.vigorEstado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateFertirriego() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AgroPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PRÓXIMO FERTIRRIEGO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextSecondary
                            )
                            Text(
                                text = predio.proximoFertirriegoHora,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextPrimary
                            )
                            Text(
                                text = predio.proximoFertirriegoSector,
                                fontSize = 12.sp,
                                color = AgroTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AgroSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Ver receta",
                                tint = AgroTextPrimary
                            )
                        }
                    }
                }
            }

            // 7. ACCIONES DE CAMPO
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACCIONES DE CAMPO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AgroTextSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Operador en Turno",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = AgroTertiary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onNavigateNuevoRegistro,
                    colors = ButtonDefaults.buttonColors(containerColor = AgroPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = AgroMintLight
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nueva Toma de Muestra en Terreno",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrediosScreenPreview() {
    AgroGestionTheme {
        PrediosScreen(
            uiState = AgroDashboardUiState(),
            onPredioSelected = {},
            onNavigateNuevoRegistro = {},
            onNavigateFertirriego = {},
            onSyncClick = {}
        )
    }
}
