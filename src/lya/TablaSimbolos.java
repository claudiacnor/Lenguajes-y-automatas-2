package lya;

public class TablaSimbolos {
	private String tipoDato;
	private int posicion;
	private Object valor;
	private String alcance;

	public TablaSimbolos(String tipoDato, int posicion, Object valor, String alcance) {
		this.tipoDato = tipoDato;
		this.posicion = posicion;
		this.valor = valor;
		this.alcance = alcance;
	}
	public String getTipoDato() {
		return tipoDato;
	}
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getAlcance() {
		return alcance;
	}
	public void setAlcance(String alcance) {
		this.alcance = alcance;
	}
}
