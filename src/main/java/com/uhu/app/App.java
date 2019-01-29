package com.uhu.app;

import com.uhu.QPlayer;
import com.uhu.Player;

public class App {

    public static void main(String args[]) {

        Player jugador = new QPlayer(); /***  AQUI VA VUESTRO JUGADOR   ***/
//		Jug_Teclado jugador = new Jug_Teclado(); /***  JUGADOR DE TECLADO   ***/ 
        jugador.start();
        jugador.gameLoop();

    }

}
