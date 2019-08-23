package com.uhu.cesar.tetris.app;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JFrame;

public final class Jug_Teclado extends JFrame {
	
	private int PUERTO_ENVIO = 4567;
	private int PUERTO_RECEP = 5678;
	private String LOGIN     = "";
	private String NOMBRE    = "";
	
	
	public Jug_Teclado() {
		LOGIN  = "usuario uhu";
		NOMBRE = "nombre del jugador";
		
	}
	
	public void start() {
		// Arrancar la partida
		enviar("start;" + LOGIN + ";" + NOMBRE + ";");
		
	}
	

	public void gameLoop() {
		setVisible(true);
		// Keyboard controls
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					enviar( "mov;0;1;");
					break;
				case KeyEvent.VK_DOWN:
					enviar( "CAER");
					break;
				case KeyEvent.VK_LEFT:
					enviar( "mov;-1;0;");
					break;
				case KeyEvent.VK_RIGHT:
					enviar( "mov;1;0;");
					break;
				case KeyEvent.VK_SPACE:
					break;
				} 
			}
			
			public void keyReleased(KeyEvent e) {
			}
		});
		
		while(true);
	}
	
	
	private void enviar(String mm) {
		try {
			DatagramSocket socketUDP = new DatagramSocket();
			DatagramPacket respuesta = new DatagramPacket(mm.getBytes(), mm.length(), InetAddress.getByName("localhost"), 4567);
			socketUDP.send(respuesta);
			socketUDP.close();
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	 
	 
	
	


	 		 
}
