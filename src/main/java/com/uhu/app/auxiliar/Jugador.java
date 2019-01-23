package com.uhu.app.auxiliar;

import com.uhu.EndMessage;
import com.uhu.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class Jugador implements Message.MessageParser {
	
	private int PUERTO_ENVIO = 4567;
	private int PUERTO_RECEP = 5678;
	private String LOGIN     = "";
	private String NOMBRE    = "";
	
	
	public Jugador(String _login, String _nombre) {
		LOGIN = _login;
		NOMBRE = _nombre;
	}

	public abstract void inicializar();
	
	public void arrancar() {
		enviar("start;" + LOGIN + ";" + NOMBRE + ";");
	}
	
	public void jugar() {
		DatagramSocket socketUDP = null;
		try {
			socketUDP = new DatagramSocket(PUERTO_RECEP);
			byte[] bufer = new byte[1000];

			while (true) {
				// PERCIBIR
				DatagramPacket peticion = new DatagramPacket(bufer, bufer.length);
				socketUDP.receive(peticion);
				String percepcion = new String(peticion.getData(), 0, peticion.getLength()); 
			 
				// PENSAR
				Message msg = parseMessage(percepcion);
				System.out.println(msg.getClass());
				if (msg.getClass() == EndMessage.class) {
					System.out.println("final de la partida. Arrancando de nuevo");
					arrancar();
				}
				else {
                    Respuesta accion = pensar(msg);

                    // ACTUAR
                    if (accion != null){
                    	enviar(accion.toString());
						enviar("CAER");
					}
				}
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		socketUDP.close();
	}
	
	public abstract Respuesta pensar(Message percepcion);

	private void enviar(String mm) {
		try {
			DatagramSocket socketUDP = new DatagramSocket();
			DatagramPacket respuesta = new DatagramPacket(mm.getBytes(), mm.length(), InetAddress.getByName("localhost"), PUERTO_ENVIO);
			socketUDP.send(respuesta);
			socketUDP.close();
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	 
}
