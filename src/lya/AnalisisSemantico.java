package lya;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalisisSemantico {
	ArrayList<String> linea = new ArrayList<String>();
	private HashMap<String, TablaSimbolos> tablaSimbolos = new HashMap<String, TablaSimbolos>();
	private ArrayList<String> listaErroresSemanticos = new ArrayList<String>();
	/*
	 * private ArrayList<String> listaErroresSemanticos; private ArrayList<String>
	 * operadoresaritmeticos; private ArrayList<String> operadoreslogicos;
	 */
	public static void main(String[] args) {
		String codigo = "public class m {\n" + " public int y=0; \n" + " public String y=10; \n" + " private int z; "
				+ "  int w=10; " + " if(y>9){\n" + "  x=8+9;\n" + " }\n" + " }\n" + "";
		AnalisisSemantico as = new AnalisisSemantico(codigo);
	}

	public AnalisisSemantico(String codigo) {
		recorreCodigo(codigo);
		LlenaTabla();
		System.out.println(listaErroresSemanticos.get(0));
		System.out.println(listaErroresSemanticos.get(1));
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
		// Llenado de la declaración de las variables
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
				if (parrafo.contains(sPublic) || parrafo.contains(sPrivate))
					tablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "global"));
				else
					tablaSimbolos.put(variable, new TablaSimbolos(Tipo.toString(), pos, valor, "local"));
			}
		}

	}
	public void Operaciones(String parrafo, int pos) {
		String parrafoAux = "", variable = "";
		// Variables usadas y no defindas
		// Se recorre el parrafo para obtener la variable de la operación
		for (int j = 0; j < parrafo.length(); j++) {
			if (parrafo.charAt(j) == '=') {
				variable = parrafoAux;
				break;
			} else { //se eliminan los espacios en blanco
				if (!Character.isWhitespace(parrafo.charAt(j))) {
					parrafoAux += Character.toString(parrafo.charAt(j));
				}
			}
		}
		if (!tablaSimbolos.containsKey(variable)) {
			listaErroresSemanticos
					.add("La variable " + "'" + variable + "' en la posición " + pos + " no ha sido definida.");
		}
	}
}
