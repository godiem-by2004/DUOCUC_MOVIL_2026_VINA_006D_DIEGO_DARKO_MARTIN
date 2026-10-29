package com.example.agrogestion.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.model.BalanceElemento
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
import com.example.agrogestion.ui.theme.AgroWarningBg
import com.example.agrogestion.ui.theme.AgroWarningRed
import com.example.agrogestion.viewmodel.AgroDashboardUiState
import java.util.Locale

@Composable
fun FertirriegoScreen(
    uiState: AgroDashboardUiState,
    onSyncClick: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    val predio = uiState.predioSeleccionado
    val req = uiState.requerimientoActual
    val volumenTotalM3 = uiState.volumenRiegoTotalM3
    val ultimaMuestra = uiState.ultimoRegistroPredio
    val idRegistroRoom = ultimaMuestra?.id

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroNeutralBg)
    ) {
        AgroTopBar(
            subtituloVista = "Fertirriego",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = AgroMintLight,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MOTOR AGRO-CALC (OFFLINE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AgroPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Surface(
                        color = AgroSurfaceVariant,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (idRegistroRoom != null) "Muestra base #$idRegistroRoom" else "Sin muestras del predio",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AgroTextPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PRESCRIPCIÓN FENOLÓGICA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTertiary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Receta Nutricional de Fertirriego",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextPrimary,
                                    lineHeight = 25.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AgroMintContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = AgroNeutralBg,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Eco,
                                            contentDescription = null,
                                            tint = AgroTertiary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Cuartel / Variedad:",
                                            fontSize = 12.sp,
                                            color = AgroTextSecondary
                                        )
                                    }
                                    Text(
                                        text = "${predio.cultivo} — ${predio.cuartelPrincipal}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTextPrimary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Straighten,
                                            contentDescription = null,
                                            tint = AgroTertiary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Superficie Activa:",
                                            fontSize = 12.sp,
                                            color = AgroTextSecondary
                                        )
                                    }
                                    Text(
                                        text = "${predio.superficieHa} Hectáreas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTextPrimary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = null,
                                            tint = AgroTertiary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Lámina de Riego:",
                                            fontSize = 12.sp,
                                            color = AgroTextSecondary
                                        )
                                    }
                                    Text(
                                        text = "${predio.laminaRiegoM3Ha.toInt()} m³/ha (${String.format(Locale.US, "%.1f", volumenTotalM3)} m³ Totales)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTertiary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AgroPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PH PROYECTADO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroMintLight
                                )
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = AgroMintLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(Locale.US, "%.1f", req.phOptimoRiego),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Óptimo Riego",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgroMintLight,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Disponibilidad máxima de microelementos.",
                                fontSize = 11.sp,
                                color = Color(0xFFD8F3DC),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AgroPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "C.E. PROYECTADA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroMintLight
                                )
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = AgroMintLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(Locale.US, "%.2f", req.ceProyectada),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "dS/m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AgroMintLight,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Seguro para raíces sensibles de cultivo.",
                                fontSize = 11.sp,
                                color = Color(0xFFD8F3DC),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                    imageVector = Icons.Default.Balance,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Balance Nutricional",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextPrimary
                                )
                            }

                            Surface(
                                color = AgroNeutralBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Teórico vs Real",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Aporte directo del cálculo estequiométrico por hectárea:",
                            fontSize = 12.sp,
                            color = AgroTextSecondary
                        )

                        if (ultimaMuestra != null) {
                            Surface(
                                color = AgroMintContainer,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = AgroPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "PRESCRIPCIÓN CALCULADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AgroTertiary
                                        )
                                        Text(
                                            text = ultimaMuestra.recomendacion,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AgroPrimary,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (uiState.balancesNutricionales.isEmpty()) {
                            Text(
                                text = "Este predio aún no tiene muestras. Registra una en \"Nuevo Registro\" para ver el balance.",
                                fontSize = 13.sp,
                                color = AgroTextSecondary
                            )
                        }
                        uiState.balancesNutricionales.forEach { itemBalance ->
                            ItemBalanceNutricionalCard(itemBalance)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ItemBalanceNutricionalCard(item: BalanceElemento) {
    val proporcionObjetivo = (item.aporteRealKgHa / item.requeridoKgHa).toFloat().coerceIn(0f, 1f)
    val progresoAnimado by animateFloatAsState(
        targetValue = proporcionObjetivo,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progreso_${item.simbolo}"
    )

    val colorIcono = if (item.requiereAjuste) AgroWarningRed else AgroPrimary
    val colorBadgeBg = if (item.requiereAjuste) AgroWarningBg else AgroMintLight
    val colorBadgeText = if (item.requiereAjuste) AgroWarningRed else AgroPrimary

    Surface(
        color = AgroNeutralBg,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorIcono),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.simbolo,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AgroTextPrimary
                        )
                        Text(
                            text = "${item.aporteRealKgHa.toInt()} kg/ha",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = AgroTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Requerido: ${item.requeridoKgHa.toInt()} kg/ha",
                            fontSize = 12.sp,
                            color = AgroTextSecondary
                        )

                        Surface(
                            color = colorBadgeBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (item.requiereAjuste) Icons.Default.WarningAmber else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colorBadgeText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = item.estadoTexto,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorBadgeText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progresoAnimado },
                color = colorIcono,
                trackColor = Color(0xFFD8DFD5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FertirriegoScreenPreview() {
    AgroGestionTheme {
        FertirriegoScreen(
            uiState = AgroDashboardUiState(),
            onSyncClick = {}
        )
    }
}
