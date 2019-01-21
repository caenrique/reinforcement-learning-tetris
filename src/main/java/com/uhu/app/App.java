package com.uhu.app;

import com.uhu.app.auxiliar.Jugador;
import com.uhu.CesarPlayer;

public class App {

    public static void main(String args[]) {

        Jugador jugador = new CesarPlayer(); /***  AQUI VA VUESTRO JUGADOR   ***/
//		Jug_Teclado jugador = new Jug_Teclado(); /***  JUGADOR DE TECLADO   ***/ 
        jugador.arrancar();
        jugador.jugar();

    }

}
