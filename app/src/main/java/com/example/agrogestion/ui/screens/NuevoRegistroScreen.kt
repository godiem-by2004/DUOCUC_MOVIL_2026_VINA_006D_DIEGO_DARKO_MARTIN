package com.example.agrogestion.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import coil.compose.AsyncImage
import com.example.agrogestion.model.CatalogoAgronomico
import com.example.agrogestion.util.FormatoCoordenada
import com.example.agrogestion.util.FotoFoliarStorage
import com.example.agrogestion.viewmodel.EstadoGps
import com.example.agrogestion.viewmodel.FormularioNutricionalValidator
import java.io.File
import java.util.Locale
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
import com.example.agrogestion.ui.theme.AgroWarningRed
import com.example.agrogestion.viewmodel.FormularioUiState
import com.example.agrogestion.viewmodel.ValidacionCampo

@Composable
fun NuevoRegistroScreen(
    formState: FormularioUiState,
    pendientesRoom: Int,
    onPredioChange: (String) -> Unit,
    onCuartelChange: (String) -> Unit,
    onCultivoChange: (String) -> Unit,
    onEtapaChange: (String) -> Unit,
    onPhChange: (String) -> Unit,
    onEcChange: (String) -> Unit,
    onNitrogenoChange: (String) -> Unit,
    onFosforoChange: (String) -> Unit,
    onPotasioChange: (String) -> Unit,
    onCalcioChange: (String) -> Unit,
    onMagnesioChange: (String) -> Unit,
    onObservacionesChange: (String) -> Unit,
    onGpsBuscando: () -> Unit,
    onGpsObtenido: (Double, Double, Float?) -> Unit,
    onGpsError: (String) -> Unit,
    onFotoCapturada: (String) -> Unit,
    onFotoQuitada: () -> Unit,
    onMensajeCamara: (String?) -> Unit,
    onCargarPresetOptimo: () -> Unit,
    onGuardarClick: () -> Unit,
    onSyncClick: () -> Unit,
    sincronizando: Boolean = false,
    mensajeSincronizacion: String? = null,
    onCerrarSesion: () -> Unit = {}
) {
    val context = LocalContext.current

    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val concedido = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            obtenerUbicacionNativa(context, onGpsBuscando, onGpsObtenido, onGpsError)
        } else {
            onGpsError("Permiso de ubicación denegado. Se usa la coordenada de referencia del predio.")
        }
    }

    var rutaFotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraTakePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { fotoTomada ->
        val ruta = rutaFotoPendiente
        if (fotoTomada && ruta != null) {
            onFotoCapturada(ruta)
        } else {
            FotoFoliarStorage.descartar(ruta)
        }
        rutaFotoPendiente = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val archivo = FotoFoliarStorage.crearArchivoNuevo(context)
            rutaFotoPendiente = archivo.absolutePath
            cameraTakePictureLauncher.launch(FotoFoliarStorage.uriParaCamara(context, archivo))
        } else {
            onMensajeCamara("Permiso de cámara denegado. Actívalo en Ajustes > Permisos para adjuntar fotos.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgroNeutralBg)
    ) {
        AgroTopBar(
            subtituloVista = "Nuevo Registro",
            pendientesRoom = pendientesRoom,
            sincronizando = sincronizando,
            onSyncClick = onSyncClick,
            onCerrarSesion = onCerrarSesion
        )

        BannerSincronizacion(mensajeSincronizacion)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = AgroMintLight,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "Muestra de terreno · Modo offline",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Toma de Muestra y Diagnóstico Nutricional",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgroTextPrimary,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Almacenamiento directo en SQLite / Room · Persistencia Offline Activa",
                                fontSize = 12.sp,
                                color = AgroTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AgroNeutralBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = AgroPrimary
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RECURSO NATIVO: SENSOR GNSS / GPS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }

                            Surface(
                                color = AgroMintLight,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = when (formState.gpsEstado) {
                                        EstadoGps.REFERENCIA_PREDIO -> "● Referencia del predio"
                                        EstadoGps.BUSCANDO -> "● Buscando señal..."
                                        EstadoGps.SENSOR -> "● GPS del dispositivo"
                                        EstadoGps.ERROR -> "● Sin señal GPS"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFB7E4C7),
                                            Color(0xFF74C69D),
                                            Color(0xFF1B4332)
                                        )
                                    )
                                )
                                val path1 = Path().apply {
                                    moveTo(0f, size.height * 0.35f)
                                    cubicTo(
                                        size.width * 0.3f, size.height * 0.15f,
                                        size.width * 0.6f, size.height * 0.55f,
                                        size.width, size.height * 0.25f
                                    )
                                }
                                drawPath(path1, color = Color(0x55FFFFFF), style = Stroke(width = 3f))

                                val path2 = Path().apply {
                                    moveTo(0f, size.height * 0.65f)
                                    cubicTo(
                                        size.width * 0.4f, size.height * 0.45f,
                                        size.width * 0.7f, size.height * 0.85f,
                                        size.width, size.height * 0.55f
                                    )
                                }
                                drawPath(path2, color = Color(0x44FFFFFF), style = Stroke(width = 2.5f))

                                drawCircle(
                                    color = Color(0xFF1E5128),
                                    radius = 22f,
                                    center = Offset(size.width * 0.58f, size.height * 0.45f)
                                )
                                drawCircle(
                                    color = Color(0xFFA3F7BF),
                                    radius = 9f,
                                    center = Offset(size.width * 0.58f, size.height * 0.45f)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = AgroMintLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = FormatoCoordenada.texto(formState.latitud, formState.longitud),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    color = Color(0x991E5128),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = formState.precisionMetros
                                            ?.let { "±${String.format(Locale.US, "%.0f", it)} m" }
                                            ?: "Sin precisión",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = AgroNeutralBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Park,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = when (formState.gpsEstado) {
                                            EstadoGps.REFERENCIA_PREDIO -> "Coordenada de referencia del predio (no medida)"
                                            EstadoGps.BUSCANDO -> "Obteniendo ubicación del dispositivo..."
                                            EstadoGps.SENSOR -> "Coordenadas leídas del sensor del dispositivo"
                                            EstadoGps.ERROR -> "No se pudo leer el GPS; se usa la referencia del predio"
                                        },
                                        fontSize = 11.sp,
                                        color = AgroTextSecondary
                                    )
                                    Text(
                                        text = formState.cuartel,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                gpsPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            enabled = formState.gpsEstado != EstadoGps.BUSCANDO,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (formState.gpsEstado == EstadoGps.BUSCANDO) "Buscando señal..." else "Actualizar GPS del Dispositivo",
                                fontWeight = FontWeight.Bold,
                                color = AgroPrimary
                            )
                        }

                        AnimatedVisibility(visible = formState.mensajeGps != null) {
                            Text(
                                text = formState.mensajeGps.orEmpty(),
                                fontSize = 12.sp,
                                color = AgroWarningRed,
                                modifier = Modifier.padding(top = 8.dp)
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = AgroPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RECURSO NATIVO: CÁMARA FOLIAR",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }

                            Surface(
                                color = AgroNeutralBg,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (formState.fotoUri != null) "Foto adjunta" else "Sin foto",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF283618))
                        ) {
                            if (formState.fotoUri != null) {
                                AsyncImage(
                                    model = File(formState.fotoUri),
                                    contentDescription = "Fotografía foliar capturada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawRect(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color(0xFF52796F), Color(0xFF1B4332))
                                        )
                                    )
                                    val leafPath = Path().apply {
                                        moveTo(size.width * 0.12f, size.height * 0.72f)
                                        cubicTo(
                                            size.width * 0.30f, size.height * 0.12f,
                                            size.width * 0.75f, size.height * 0.15f,
                                            size.width * 0.90f, size.height * 0.45f
                                        )
                                        cubicTo(
                                            size.width * 0.72f, size.height * 0.88f,
                                            size.width * 0.35f, size.height * 0.85f,
                                            size.width * 0.12f, size.height * 0.72f
                                        )
                                    }
                                    drawPath(leafPath, color = Color(0xFF95D5B2))
                                    drawPath(leafPath, color = Color(0xFF40916C), style = Stroke(width = 6f))
                                    drawLine(
                                        color = Color(0xFFD8F3DC),
                                        start = Offset(size.width * 0.12f, size.height * 0.72f),
                                        end = Offset(size.width * 0.88f, size.height * 0.45f),
                                        strokeWidth = 4f
                                    )
                                }
                            }

                            if (formState.fotoUri != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AgroPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Foto adjunta",
                                        tint = AgroMintLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = formState.fotoUri?.let { "📎 ${File(it).name}" }
                                ?: "Aún no hay fotografía adjunta",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgroPrimary
                        )
                        Text(
                            text = formState.fotoUri
                                ?.let { "Guardada en el almacenamiento interno de la app (${File(it).length() / 1024} KB)" }
                                ?: "Toque el botón para abrir la cámara",
                            fontSize = 11.sp,
                            color = AgroTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = AgroPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (formState.fotoUri != null) "Repetir Fotografía" else "Capturar Fotografía Foliar con Cámara",
                                fontWeight = FontWeight.Bold,
                                color = AgroPrimary
                            )
                        }

                        AnimatedVisibility(visible = formState.fotoUri != null) {
                            TextButton(
                                onClick = onFotoQuitada,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = AgroWarningRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quitar fotografía",
                                    fontWeight = FontWeight.Bold,
                                    color = AgroWarningRed
                                )
                            }
                        }

                        AnimatedVisibility(visible = formState.mensajeCamara != null) {
                            Text(
                                text = formState.mensajeCamara.orEmpty(),
                                fontSize = 12.sp,
                                color = AgroWarningRed,
                                modifier = Modifier.padding(top = 8.dp)
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = AgroPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PARÁMETROS FISICOQUÍMICOS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgroTextPrimary
                                )
                            }

                            OutlinedButton(
                                onClick = onCargarPresetOptimo,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Valores Óptimos", fontSize = 11.sp)
                            }
                        }

                        SelectorAgro(
                            etiqueta = "Predio Agrícola",
                            valor = formState.predio,
                            opciones = CatalogoAgronomico.prediosDisponibles.map { it.nombre },
                            onSeleccion = onPredioChange
                        )

                        CampoValidadoAgro(
                            valor = formState.cuartel,
                            onValueChange = onCuartelChange,
                            etiqueta = "Cuartel / Sector de Riego",
                            validacion = formState.valCuartel,
                            teclado = KeyboardType.Text
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                SelectorAgro(
                                    etiqueta = "Cultivo",
                                    valor = formState.cultivo,
                                    opciones = CatalogoAgronomico.cultivosDisponibles,
                                    onSeleccion = onCultivoChange
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                SelectorAgro(
                                    etiqueta = "Etapa Fenológica",
                                    valor = formState.etapa,
                                    opciones = CatalogoAgronomico.etapasFenologicas,
                                    onSeleccion = onEtapaChange
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.ph,
                                    onValueChange = onPhChange,
                                    // Rango tomado de las constantes del validador (no se desactualiza)
                                    etiqueta = "pH Suelo (${FormularioNutricionalValidator.PH_MIN} - ${FormularioNutricionalValidator.PH_MAX})",
                                    validacion = formState.valPh
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.ec,
                                    onValueChange = onEcChange,
                                    etiqueta = "C.E. Agua (dS/m)",
                                    validacion = formState.valEc
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.nitrogeno,
                                    onValueChange = onNitrogenoChange,
                                    etiqueta = "N (kg/ha)",
                                    validacion = formState.valNitrogeno
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.fosforo,
                                    onValueChange = onFosforoChange,
                                    etiqueta = "P₂O₅ (kg/ha)",
                                    validacion = formState.valFosforo
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.potasio,
                                    onValueChange = onPotasioChange,
                                    etiqueta = "K₂O (kg/ha)",
                                    validacion = formState.valPotasio
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.calcio,
                                    onValueChange = onCalcioChange,
                                    etiqueta = "Calcio CaO (kg/ha)",
                                    validacion = formState.valCalcio
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoValidadoAgro(
                                    valor = formState.magnesio,
                                    onValueChange = onMagnesioChange,
                                    etiqueta = "Magnesio MgO (kg/ha)",
                                    validacion = formState.valMagnesio
                                )
                            }
                        }

                        CampoValidadoAgro(
                            valor = formState.observacionesFoliares,
                            onValueChange = onObservacionesChange,
                            etiqueta = "Diagnóstico / Sintomatología Foliar",
                            validacion = formState.valObservaciones,
                            teclado = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )

                        AnimatedVisibility(
                            visible = formState.guardadoExitoso,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                color = AgroMintContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AgroPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "¡Muestra guardada en SQLite/Room! Receta actualizada en Fertirriego.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgroPrimary
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onGuardarClick,
                            enabled = !formState.guardando,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AgroPrimary,
                                disabledContainerColor = AgroPrimary.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            if (formState.guardando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = AgroMintLight
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (formState.guardando) "Guardando muestra..." else "Guardar Muestra en Base Local (Room)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CampoValidadoAgro(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    validacion: ValidacionCampo,
    teclado: KeyboardType = KeyboardType.Decimal,
    imeAction: ImeAction = ImeAction.Next
) {
    val tieneError = !validacion.esValido
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta, fontSize = 12.sp) },
        isError = tieneError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = teclado, imeAction = imeAction),
        trailingIcon = when {
            tieneError -> {
                {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error de validación",
                        tint = AgroWarningRed
                    )
                }
            }
            valor.isNotBlank() -> {
                {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Campo válido",
                        tint = AgroSecondary
                    )
                }
            }
            else -> null
        },
        supportingText = {
            if (tieneError && validacion.mensajeError != null) {
                Text(
                    text = validacion.mensajeError,
                    color = AgroWarningRed,
                    fontSize = 11.sp
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorAgro(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(etiqueta, fontSize = 12.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun obtenerUbicacionNativa(
    context: Context,
    onBuscando: () -> Unit,
    onObtenido: (Double, Double, Float?) -> Unit,
    onError: (String) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val tieneFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    val proveedor = when {
        tieneFine && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> {
            onError("La ubicación del dispositivo está desactivada. Actívala e intenta de nuevo.")
            return
        }
    }

    onBuscando()
    val cancelacion = CancellationSignal()
    val temporizador = Handler(Looper.getMainLooper())
    val cortarPorTiempo = Runnable { cancelacion.cancel() }
    temporizador.postDelayed(cortarPorTiempo, 15_000L)

    try {
        LocationManagerCompat.getCurrentLocation(
            locationManager,
            proveedor,
            cancelacion,
            ContextCompat.getMainExecutor(context)
        ) { ubicacion ->
            temporizador.removeCallbacks(cortarPorTiempo)
            if (ubicacion != null) {
                onObtenido(ubicacion.latitude, ubicacion.longitude, if (ubicacion.hasAccuracy()) ubicacion.accuracy else null)
            } else {
                onError("No se obtuvo señal de ubicación. Sal a cielo abierto e intenta de nuevo.")
            }
        }
    } catch (_: SecurityException) {
        temporizador.removeCallbacks(cortarPorTiempo)
        onError("Sin permiso para acceder a la ubicación.")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoRegistroScreenPreview() {
    AgroGestionTheme {
        NuevoRegistroScreen(
            formState = FormularioUiState(),
            pendientesRoom = 4,
            onPredioChange = {},
            onCuartelChange = {},
            onCultivoChange = {},
            onEtapaChange = {},
            onPhChange = {},
            onEcChange = {},
            onNitrogenoChange = {},
            onFosforoChange = {},
            onPotasioChange = {},
            onCalcioChange = {},
            onMagnesioChange = {},
            onObservacionesChange = {},
            onGpsBuscando = {},
            onGpsObtenido = { _, _, _ -> },
            onGpsError = {},
            onFotoCapturada = {},
            onFotoQuitada = {},
            onMensajeCamara = {},
            onCargarPresetOptimo = {},
            onGuardarClick = {},
            onSyncClick = {}
        )
    }
}
