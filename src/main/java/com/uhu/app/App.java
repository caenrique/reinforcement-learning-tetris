package com.uhu.app;

import com.uhu.CesarPlayer;
import com.uhu.Player;

public class App {

    public static void main(String args[]) {

        Player jugador = new CesarPlayer(); /***  AQUI VA VUESTRO JUGADOR   ***/
//		Jug_Teclado jugador = new Jug_Teclado(); /***  JUGADOR DE TECLADO   ***/ 
        jugador.start();
        jugador.gameLoop();

    }

}
