package lya;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AnalisisSemantico {
	ArrayList<String> linea = new ArrayList<String>();
	private HashMap<String, TablaSimbolos> tablaSimbolos = new HashMap<String, TablaSimbolos>();
	private ArrayList<String> listaErroresSemanticos = new ArrayList<String>();
	private ArrayList<String> operadoreslogicos = new ArrayList<>(), operadoresaritmeticos = new ArrayList<>();
	/*
	 * private ArrayList<String> listaErroresSemanticos; private ArrayList<String>

	 */
	public AnalisisSemantico(String URL) {
		String codigo = LeeArchivo(URL);
		recorreCodigo(codigo);
		LlenaTabla();
		for(String error: listaErroresSemanticos) {
			System.out.println(error);
		}
	}
	public static String LeeArchivo(String URL) {
		String parrafo = "", texto = "";
		try {
			FileReader archivo = new FileReader(URL); 
			BufferedReader leeLinea = new BufferedReader(archivo); 
			parrafo = leeLinea.readLine(); 
			while (parrafo != null) { 
				texto += parrafo + "\n";
				parrafo = leeLinea.readLine();
			}
		} catch (FileNotFoundException exception) { 
			System.out.println(exception);
		} catch (Exception exception) {
			System.out.println(exception);
		}
		return texto;
	}
	public void recorreCodigo(String codigo) {
		String parrafo = "";
		for (int i = 0; i < codigo.length(); i++) {
			if (codigo.charAt(i) == '{' || codigo.charAt(i) == '}' || codigo.charAt(i) == ';') {
				linea.add(parrafo);
				parrafo = "";
			}
			else
				parrafo += Character.toString(codigo.charAt(i));
		}
	}
	public void LlenaTabla() {
		String parrafo;
		CharSequence sInt = "int", sIgual = "=";
		CharSequence sString = "String";
		CharSequence sDouble = "double";
		CharSequence sBool = "boolean";
		for (int i = 0; i < linea.size(); i++) {
			parrafo = linea.get(i);
			if (parrafo.contains(sInt) || parrafo.contains(sDouble) || parrafo.contains(sBool) || parrafo.contains(sString)) {
				AgregaVariable(parrafo, sInt, i + 1);
				AgregaVariable(parrafo, sString, i + 1);
				AgregaVariable(parrafo, sDouble, i + 1);
				AgregaVariable(parrafo, sBool, i + 1);
			} else {
				if (parrafo.contains(sIgual)) {
					Operaciones(parrafo, i + 1);
				}
			}
		}
	}
	public void AgregaVariable(String parrafo, CharSequence Tipo, int pos) {
		String parrafoAux = "", variable = "", valor = "";
		CharSequence sIgual = "=", sPublic = "public", sPrivate = "private";
		// Llenado de la declaraci�n de las variables
		if (parrafo.contains(Tipo)) {
			for (int j = 0; j < parrafo.length(); j++) {
				if (parrafoAux.contains(Tipo)) {
					if (parrafoAux.contains(sIgual)) {
						valor += Character.toString(parrafo.charAt(j));
					} else {
						if (parrafo.charAt(j) == ' ') {
							variable = Character.toString(parrafo.charAt(j + 1));
						}
					}
				}
				parrafoAux += Character.toString(parrafo.charAt(j));
			}
			// Validar si la variable ya esta declarada
			if (tablaSimbolos.containsKey(variable))
				listaErroresSemanticos
						.add("La variable " + "'" + variable + "'" + " ya se encuentra declarada en el renglon "
								+ tablaSimbolos.get(variable).getPosicion() + ".");
			else {
				//Verificando el valor asignado con el tipo 
				if(!verificaTipoConValor(Tipo.toString(), valor)){
					listaErroresSemanticos
							.add("Imposible asignar a variable " + "'" + variable + "' Tipo (" +Tipo.toString()+ ") valor:  " + valor
								+" renglon "	+ pos + ".");
					return;
				}
				if (parrafo.contains(sPublic) || parrafo.contains(sPrivate))
					tablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "global"));
				else
					tablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "local"));
			}
		}

	}

	private boolean verificaTipoConValor(String tipo, String valor) {
		if(valor.length()==0) {
			return true;
		}
		switch(tipo) {
			case "boolean":
				return (valor.equals("true") || valor.equals("false"));
			case "int":
				try {
					Integer.parseInt(valor);
					return true;
				}catch(Exception e) {
					return false;
				}
			default:
				return false;
		}
		
	}

	public void Operaciones(String parrafo, int pos) {
		String parrafoAux = "", variable = "", operandoAux="";
		boolean variableEncontrada=false;
		// Variables usadas y no defindas
		// Se recorre el parrafo para obtener la variable de la operaci�n
		for (int j = 0; j < parrafo.length(); j++) {
			if (!variableEncontrada && parrafo.charAt(j) == '=' && (parrafo.charAt(j+1)!= '=') ) {//Encontramos la variable, lo que sigue es la operacion
				variable = parrafoAux;
				parrafoAux= "";
				variableEncontrada=true;
				if (!tablaSimbolos.containsKey(variable)) {
					listaErroresSemanticos
							.add("La variable " + "'" + variable + "' en la posici�n " + pos + " no ha sido definida.");
				break;
				}
			} else { //se eliminan los espacios en blanco
				
				if(Character.isWhitespace(parrafo.charAt(j)))
					continue;
				if (!Character.isWhitespace(parrafo.charAt(j)) && (!esOperador(parrafo.charAt(j)+"")&& !esOperador(parrafoAux))) {
					parrafoAux += Character.toString(parrafo.charAt(j));
					
				}else {
					//Verificamos si es una variable
					if(tablaSimbolos.containsKey(parrafoAux)) { //Es variable
						if(!tablaSimbolos.get(parrafoAux).getTipoDato().equals(tablaSimbolos.get(variable).getTipoDato())) {
							listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
									") con variable "+ parrafoAux + " Tipo ( "+tablaSimbolos.get(parrafoAux)+")");
							break;
						}
					}else {//Es operando u constante
						if(esOperador(parrafoAux)) {//Es un operador
							if(!verificarTipoConOperando(tablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {//No es un tipo de operador correco
								listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
										") con operador "+ parrafoAux);
								break;
							}
						}else {//Es una constante
							if(!verificaTipoConValor(tablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {
								listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
										") con "+ parrafoAux);
								break;
							}
						}
					}
					parrafoAux="";
					if(parrafoAux.length()==0) {//verificamos si necesitamos meter el operador
						parrafoAux+=parrafo.charAt(j);
					}
				}
			}
		}
		if(parrafoAux.length()>0) {//Quedo algo en los operandos/operadores
			//Verificamos si es una variable
			if(tablaSimbolos.containsKey(parrafoAux)) { //Es variable
				if(!tablaSimbolos.get(parrafoAux).getTipoDato().equals(tablaSimbolos.get(variable).getTipoDato())) {
					listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
							") con variable "+ parrafoAux + " Tipo ( "+tablaSimbolos.get(parrafoAux)+")");
				}
			}else {//Es operando u constante
				if(esOperador(parrafoAux)) {//Es un operador
					if(!verificarTipoConOperando(tablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {//No es un tipo de operador correco
						listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
								") con operador "+ parrafoAux);
					}
				}else {//Es una constante
					if(!verificaTipoConValor(tablaSimbolos.get(variable).getTipoDato(), parrafoAux)) {
						listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
								") con "+ parrafoAux);
					}
				}
			}
			parrafoAux="";

		
		}
	}
	
	public boolean verificarTipoConOperando(String tipo, String operando) {
		switch(tipo) {
			case "int":
				return (operando.equals("+") || operando.equals("*") || operando.equals("/") || operando.equals("-"));
			case "boolean":
				return (operando.equals("&") ||operando.equals("&&") || operando.equals("|") || operando.equals("||") || operando.equals("!=")|| operando.equals("=="));
		}
		return false;
	}
	
	public boolean esOperador(String operador) {
		return (operador.equals("+") || operador.equals("*") || operador.equals("/") || operador.equals("-")) || (operador.equals("&") || (operador.equals("&&") || operador.equals("|") || operador.equals("||") || operador.equals("!=")|| operador.equals("==")));
	}
}
