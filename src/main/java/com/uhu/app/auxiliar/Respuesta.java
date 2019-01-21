package com.uhu.app.auxiliar;

public class Respuesta {

	private int DESPLAZAR;
	private int ROTAR;
	
	public Respuesta(int _desp, int _rot) {
		DESPLAZAR = _desp;
		ROTAR     = _rot;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "mov;" + DESPLAZAR + ";" + ROTAR + ";";
	}
	
	
	
	
}
