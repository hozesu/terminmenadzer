package com.example.terminmenadzer.utils

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import android.widget.LinearLayout
import android.app.AlertDialog
import java.security.MessageDigest

/**
 * Ekstenzija za SHA-256 hashiranje stringa
 */
fun String.sha256(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray())
        .joinToString("") { "%02x".format(it) }
}

/**
 * Sačuvaj master password (čuva se hash, NE običan tekst)
 */
fun sacuvajMasterPassword(context: Context, password: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("master_password", password.sha256()).apply()
}

/**
 * Provera da li je uneti password ispravan
 */
fun proveriMasterPassword(context: Context, password: String): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val savedHash = prefs.getString("master_password", null)
    return savedHash == password.sha256()
}

/**
 * Da li je master password već postavljen
 */
fun jeMasterPasswordPostavljen(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("master_password", null) != null
}

/**
 * Dijalog za postavljanje master šifre (prvi put)
 */
fun prikaziDijalogZaPostavljanjeSifre(context: Context, onPasswordSet: () -> Unit) {
    val input1 = EditText(context)
    input1.hint = "Unesite šifru"
    val input2 = EditText(context)
    input2.hint = "Ponovite šifru"

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.setPadding(50, 20, 50, 10)
    layout.addView(input1)
    layout.addView(input2)

    AlertDialog.Builder(context)
        .setTitle("Postavite master šifru")
        .setView(layout)
        .setCancelable(false)
        .setPositiveButton("Sačuvaj") { _, _ ->
            val pass1 = input1.text.toString()
            val pass2 = input2.text.toString()
            if (pass1.isEmpty() || pass1 != pass2) {
                Toast.makeText(context, "Šifre se ne poklapaju ili su prazne!", Toast.LENGTH_SHORT).show()
                prikaziDijalogZaPostavljanjeSifre(context, onPasswordSet)
            } else {
                sacuvajMasterPassword(context, pass1)
                Toast.makeText(context, "Šifra postavljena!", Toast.LENGTH_SHORT).show()
                onPasswordSet()
            }
        }
        .show()
}

/**
 * Dijalog za unos master šifre prilikom pristupa podešavanjima
 */
fun prikaziDijalogZaUnosSifre(context: Context, onPasswordCorrect: () -> Unit) {
    val input = EditText(context)
    input.hint = "Unesite master šifru"

    val layout = LinearLayout(context)
    layout.orientation = LinearLayout.VERTICAL
    layout.setPadding(50, 20, 50, 10)
    layout.addView(input)

    AlertDialog.Builder(context)
        .setTitle("Zaštita podešavanja")
        .setView(layout)
        .setCancelable(false)
        .setPositiveButton("Potvrdi") { _, _ ->
            val pass = input.text.toString()
            if (proveriMasterPassword(context, pass)) {
                onPasswordCorrect()
            } else {
                Toast.makeText(context, "Pogrešna šifra!", Toast.LENGTH_SHORT).show()
                prikaziDijalogZaUnosSifre(context, onPasswordCorrect)
            }
        }
        .setNegativeButton("Otkaži") { dialog, _ -> dialog.dismiss() }
        .show()
}