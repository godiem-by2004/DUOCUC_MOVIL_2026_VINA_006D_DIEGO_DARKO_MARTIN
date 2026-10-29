package com.example.agrogestion.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FotoFoliarStorage {

    fun crearArchivoNuevo(context: Context): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        val sello = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(carpeta, "FOLIAR_$sello.jpg")
    }

    fun uriParaCamara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    fun descartar(ruta: String?) {
        if (ruta.isNullOrBlank()) return
        runCatching { File(ruta).takeIf { it.exists() }?.delete() }
    }
}
