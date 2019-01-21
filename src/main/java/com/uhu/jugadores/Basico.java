package com.uhu.jugadores;

import java.util.Random;
import java.util.StringTokenizer;

import com.uhu.app.App;
import com.uhu.app.auxiliar.Jugador;
import com.uhu.app.auxiliar.Respuesta;

public class Basico extends Jugador {

	Random alea;
	
	public Basico() {
		super("usuario uhu", "nombre del jugador");
		alea = new Random(System.currentTimeMillis());
	}

	@Override
	public void inicializar() {
		
		// TODO Aquí lo que queráis antes de que empiecen a caer fichas 
		super.arrancar();
	}

	@Override
	public Respuesta pensar(String percepcion) {
		
		System.out.println(percepcion);
		
		StringTokenizer st = new StringTokenizer(percepcion, ";");
		String orden = st.nextToken().trim().toUpperCase();
		
		
		if (orden.equalsIgnoreCase("FIN")) {
			System.out.println("Se acabó...");
			inicializar();   // <-- este es el metodo de arrancar la partida
//			System.exit(1);
			
		} else if (orden.equalsIgnoreCase("MOV")) {
			//		int desplaza = alea.nextInt(10)-5;
			//		int rota     = alea.nextInt(3);
			int desplaza = 0;
			int rota     = 1;
			return new Respuesta(desplaza,rota);
			
		} else {
			System.out.println("No entiendo el mensaje");
			System.out.println(percepcion);
			return new Respuesta(0,0);
		}
		return null;
	}

		 
	 		 
}
