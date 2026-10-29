package com.example.agrogestion.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agrogestion.ui.theme.AgroMintContainer
import com.example.agrogestion.ui.theme.AgroPrimary
import com.example.agrogestion.ui.theme.AgroSecondary
import com.example.agrogestion.ui.theme.AgroSurfaceVariant
import com.example.agrogestion.ui.theme.AgroTextPrimary
import com.example.agrogestion.ui.theme.AgroTextSecondary

@Composable
fun AgroTopBar(
    subtituloVista: String,
    pendientesRoom: Int,
    sincronizando: Boolean = false,
    onSyncClick: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var menuPerfilAbierto by remember { mutableStateOf(false) }
    val badgeBg by animateColorAsState(
        targetValue = if (pendientesRoom == 0) AgroMintContainer else AgroSurfaceVariant,
        label = "badgeBg"
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AgroPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Logo AgroGestión",
                        tint = AgroSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AgroGestión Terreno",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = AgroPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = " · $subtituloVista",
                            fontSize = 13.sp,
                            color = AgroTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(badgeBg)
                            .clickable { onSyncClick() }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (sincronizando) AgroSecondary else AgroPrimary)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (sincronizando) {
                                "Sincronizando con servidor..."
                            } else {
                                "Modo Offline - Base Local Room"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AgroTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Estado Room",
                            tint = AgroPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSyncClick) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sincronizar",
                        tint = AgroPrimary
                    )
                }
                Box {
                    IconButton(
                        onClick = { menuPerfilAbierto = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AgroMintContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil Agrónomo",
                            tint = AgroPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuPerfilAbierto,
                        onDismissRequest = { menuPerfilAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = AgroPrimary
                                )
                            },
                            onClick = {
                                menuPerfilAbierto = false
                                onCerrarSesion()
                            }
                        )
                    }
                }
            }
        }
    }
}
