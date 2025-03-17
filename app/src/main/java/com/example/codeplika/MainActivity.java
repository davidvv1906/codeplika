package com.example.codeplika;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declaración del WebView para cargar contenido web
    private WebView webView;

    // Constante para almacenar el PIN que se usará para salir del modo kiosco
    private static final String EXIT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Establece el diseño de la actividad

        // Configuración del WebView
        webView = findViewById(R.id.webview); // Conecta el WebView definido en el layout
        webView.setWebViewClient(new WebViewClient()); // Permite la navegación dentro del WebView
        webView.getSettings().setJavaScriptEnabled(true); // Habilita JavaScript si es necesario
        webView.loadUrl("https://desarrollo.codiplika.com"); // Carga la URL de tu aplicación web

        // Inicia el modo kiosco tan pronto como se abra la actividad
        startKioskMode();
    }

    /**
     * Inicia el modo kiosco, que evita que el usuario salga de la aplicación
     */
    private void startKioskMode() {
        // Verifica que la versión de Android sea compatible con el modo kiosco
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Obtiene el estado del modo kiosco actual
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                try {
                    startLockTask(); // Activa el modo kiosco
                } catch (IllegalStateException e) {
                    // Maneja errores en caso de que el modo kiosco no pueda ser iniciado
                    Toast.makeText(this, "No se pudo iniciar el modo kiosco", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Finaliza el modo kiosco para permitir al usuario salir de la aplicación
     */
    private void stopKioskMode() {
        // Verifica que la versión de Android sea compatible
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stopLockTask(); // Detiene el modo kiosco
        }
    }

    /**
     * Sobrescribe el comportamiento del botón "Atrás"
     * Si el WebView tiene historial, permite navegar hacia atrás.
     * Si no hay historial, solicita un PIN para salir del modo kiosco.
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Navega hacia atrás en el historial del WebView
        } else {
            // Muestra un cuadro de diálogo para solicitar el PIN
            showExitPinDialog();
        }
    }

    /**
     * Muestra un cuadro de diálogo para solicitar al usuario un PIN que permita salir del modo kiosco
     */
    private void showExitPinDialog() {
        // Construye el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introducir PIN para salir"); // Título del cuadro de diálogo

        // Campo de texto para ingresar el PIN
        final EditText pinInput = new EditText(this);
        pinInput.setHint("PIN"); // Mensaje que se muestra dentro del campo
        builder.setView(pinInput); // Agrega el campo al cuadro de diálogo

        // Botón "Aceptar"
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPin = pinInput.getText().toString(); // Obtiene el PIN ingresado
                if (EXIT_PIN.equals(enteredPin)) { // Verifica si el PIN es correcto
                    stopKioskMode(); // Finaliza el modo kiosco
                    finish(); // Cierra la actividad
                } else {
                    // Muestra un mensaje si el PIN es incorrecto
                    Toast.makeText(MainActivity.this, "PIN incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cierra el cuadro de diálogo sin realizar acciones
            }
        });

        // Configura el cuadro de diálogo para que no pueda cerrarse sin interactuar
        builder.setCancelable(false);
        builder.show(); // Muestra el cuadro de diálogo
    }
}
