package com.example.agrogestion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.ui.theme.AgroMintContainer
import com.example.agrogestion.ui.theme.AgroPrimary

/**
 * Aviso animado del progreso de sincronización. Se usa bajo AgroTopBar en las 4 pantallas
 * para que el usuario vea la respuesta sin importar desde dónde pulsó sincronizar.
 */
@Composable
fun BannerSincronizacion(mensaje: String?) {
    AnimatedVisibility(visible = mensaje != null) {
        Surface(
            color = AgroMintContainer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = mensaje.orEmpty(),
                color = AgroPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
