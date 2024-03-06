package com.example.tresenraya;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView turnoTextView;
    RadioButton facil;
    RadioButton dificil;
    Integer[] botones;
    int[] tablero = new int[]{
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
    };

    boolean juegoTerminado = false;
    boolean juegoIniciado = false;
    boolean turnoJugador1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        turnoTextView = findViewById(R.id.turnoTextView);
        facil = findViewById(R.id.facil);
        dificil = findViewById(R.id.dificil);

        botones = new Integer[] {
                R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6,
                R.id.btn7, R.id.btn8, R.id.btn9
        };

        facil.setChecked(true);
        RadioGroup niveles = findViewById(R.id.niveles);
        niveles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.dificil) {
                    dificil.setChecked(false);
                    facil.setChecked(true);
                    mostrarMensajeNoDisponible();
                }
            }
        });

    }

    private void mostrarMensajeNoDisponible() {
        Toast.makeText(MainActivity.this, "Modo difícil no disponible", Toast.LENGTH_SHORT).show();
    }

    public void startGame(View v) {
        if (!juegoIniciado) { // Verificar si el juego aún no ha iniciado
            // Marcar el juego como iniciado
            juegoIniciado = true;
            // Restablecer el estado del juego a no terminado
            juegoTerminado = false;

            // Desactivar el botón "start" para que no se pueda volver a presionar
            Button startButton = findViewById(R.id.btnStart);
            startButton.setEnabled(false);
            actualizarTurno();
        }
    }

    private void actualizarTurno() {
        if (turnoJugador1) {
            turnoTextView.setText("Turno de O"); // Si es el turno del jugador 1 (O)
        } else {
            turnoTextView.setText("Turno de X"); // Si es el turno del jugador 2 (X)
        }
    }


    public void restartGame(View v) {
        // Restablecer el estado del juego a no terminado
        juegoIniciado = true;
        juegoTerminado = false;

        // Restablecer el estado del juego y la interfaz aquí
        Button startButton = findViewById(R.id.btnStart);
        startButton.setEnabled(false); // Habilita nuevamente el botón Start


        // Restablecer el tablero lógico a un estado inicial
        for (int i = 0; i < tablero.length; i++) {
            tablero[i] = 0;
        }

        // Restablecer la interfaz de usuario del tablero de juego
        for (int id : botones) {
            ImageButton button = findViewById(id);
            button.setImageDrawable(null); // Esto quitará cualquier imagen del botón
        }

        // Restablecer el texto del indicador de estado del juego
        textView.setText(""); // O algún texto de inicio si es necesario
        turnoTextView.setText("");
    }


    public void ponerFicha(View v) {
        if (juegoIniciado && !juegoTerminado && Arrays.asList(botones).contains(v.getId())) { // Verifica si el juego ha comenzado y no ha terminado
            ImageButton button = (ImageButton) v;
            int numBoton = Arrays.asList(botones).indexOf(v.getId());
            if (tablero[numBoton] == 0) { // Solo actúa si la casilla está vacía
                button.setImageResource(R.drawable.circulonormal);
                turnoJugador1 = false;
                actualizarTurno();
                tablero[numBoton] = 1;
                procesarEstadoDelJuego();
            }
        }
    }


    private void procesarEstadoDelJuego() {
        final int[] resultado = {hayGanador()};
        if (resultado[0] != 0) {
            finalizarJuego(resultado[0]);
        } else if (tableroLleno()) {
            juegoTerminado = true;
            textView.setText("¡Empate!");
        } else {
            // Deshabilitar todos los botones mientras la IA está pensando
            deshabilitarBotones();

            // Retraso de un segundo antes de llamar al método ia()
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ia(); // Llama al método de la IA después de un segundo
                    resultado[0] = hayGanador();
                    if (resultado[0] != 0 || tableroLleno()) {
                        finalizarJuego(resultado[0]);
                    } else {
                        // Cambiar el turno después de que la IA haya hecho su movimiento
                        turnoJugador1 = !turnoJugador1;
                        // Habilitar los botones nuevamente después de que la IA haya jugado
                        habilitarBotones();
                    }
                }
            }, 1000);
        }
    }

    private void deshabilitarBotones() {
        for (int id : botones) {
            ImageButton button = findViewById(id);
            button.setEnabled(false);
        }
    }

    private void habilitarBotones() {
        for (int id : botones) {
            ImageButton button = findViewById(id);
            button.setEnabled(true);
        }
    }


    private void finalizarJuego(int resultado) {
        juegoTerminado = true;
        // Añadir esta línea para limpiar el turnoTextView
        turnoTextView.setText("");
        if (resultado == 1) {
            textView.setText("Han ganado las O");
        } else if (resultado == -1) {
            textView.setText("Han ganado las X");
        } else {
            textView.setText("¡Empate!");
        }
        if (resultado != 0) {
            cambiarImagenGanadora(resultado);
        }
    }



    public void ia(){
        Random ran = new Random();
        int pos = ran.nextInt(tablero.length);
        while(tablero[pos] != 0) {
            pos = ran.nextInt(tablero.length);
        }
        ImageButton b = findViewById(botones[pos]);
        b.setImageResource(R.drawable.cruz);
        tablero[pos] = -1;
        turnoJugador1 = true;
        actualizarTurno();
    }


    public int hayGanador() {
        // Comprobación de filas
        for (int i = 0; i < 9; i += 3) {
            if (tablero[i] != 0 && tablero[i] == tablero[i + 1] && tablero[i] == tablero[i + 2]) {
                return tablero[i];
            }
        }

        // Comprobación de columnas
        for (int i = 0; i < 3; i++) {
            if (tablero[i] != 0 && tablero[i] == tablero[i + 3] && tablero[i] == tablero[i + 6]) {
                return tablero[i];
            }
        }

        // Comprobación de diagonales
        if (tablero[0] != 0 && tablero[0] == tablero[4] && tablero[0] == tablero[8]) {
            return tablero[0];
        }
        if (tablero[2] != 0 && tablero[2] == tablero[4] && tablero[2] == tablero[6]) {
            return tablero[2];
        }

        // No hay ganador
        return 0;
    }

    public boolean tableroLleno() {
        for (int i = 0; i < tablero.length; i++) {
            if (tablero[i] == 0) {
                return false; // Todavía hay casillas vacías
            }
        }
        return true; // El tablero está lleno
    }

    private void cambiarImagenGanadora(int jugador) {
        int[][] combinacionesGanadoras = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // filas
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columnas
                {0, 4, 8}, {2, 4, 6}            // diagonales
        };

        for (int[] combinacion : combinacionesGanadoras) {
            if (tablero[combinacion[0]] == jugador &&
                    tablero[combinacion[1]] == jugador &&
                    tablero[combinacion[2]] == jugador) {
                int ganadorResId = jugador == 1 ? R.drawable.circulo : R.drawable.cruzganador;
                for (int pos : combinacion) {
                    ImageButton b = findViewById(botones[pos]);
                    b.setImageResource(ganadorResId);
                }
                break;
            }
        }
    }
}