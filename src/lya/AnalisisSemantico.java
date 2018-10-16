package lya;

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
	public static void main(String[] args) {
		String codigo = "public class m {\n" + " public int y=0; \n" + " public boolean f=maybe; \n" + " private int z; "
				+ "  int w=10; " + " if(y>9){\n" + "  x=8+9;\n" + " }\n" + " }\n" + "";
		AnalisisSemantico as = new AnalisisSemantico(codigo);
	}

	public AnalisisSemantico(String codigo) {
		recorreCodigo(codigo);
		LlenaTabla();
		for(String error: listaErroresSemanticos) {
			System.out.println(error);
		}
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
/*		String parrafoAux = "", variable = "", operandoAux="";
		
		// Variables usadas y no defindas
		// Se recorre el parrafo para obtener la variable de la operaci�n
		for (int j = 0; j < parrafo.length(); j++) {
			if (parrafo.charAt(j) == '=' && parrafo.charAt(j+1)!= '=') {
				variable = parrafoAux;
				parrafoAux= "";
				if (!tablaSimbolos.containsKey(variable)) {
					listaErroresSemanticos
							.add("La variable " + "'" + variable + "' en la posici�n " + pos + " no ha sido definida.");
				break;
				}
			} else { //se eliminan los espacios en blanco
				if (!Character.isWhitespace(parrafo.charAt(j))) {
					parrafoAux += Character.toString(parrafo.charAt(j));
				}else {
					//Verificamos si es una variable
					if(tablaSimbolos.containsKey(parrafoAux)) {//Es variable
						if(!tablaSimbolos.get(parrafoAux).getTipoDato().equals(tablaSimbolos.get(variable).getTipoDato())) {
							listaErroresSemanticos.add("Tipo de operacion incorrecta variable "+ variable +" Tipo ("+tablaSimbolos.get(variable).getTipoDato()+
									") con variable "+ parrafoAux + " Tipo ( "+tablaSimbolos.get(parrafoAux)+")");
							break;
						}
					}else {//Es operando u constante
						if(verificarTipoConOperando (tablaSimbolos.get(variable), parrafoAux)) {//Es un operador
							
						}else {
							
						}
					}
					parrafoAux="";
				}
			}
		}*/
	}
	
	public boolean verificarTipoConOperando(String tipo, String operando) {
		switch(tipo) {
			case "int":
				return (operando.equals("+") || operando.equals("*") || operando.equals("/") || operando.equals("-"));
			case "boolean":
				return (operando.equals("&&") || operando.equals("|") || operando.equals("||") || operando.equals("!=")|| operando.equals("=="));
		}
		return false;
	}
}
