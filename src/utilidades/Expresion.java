package utilidades;

/**
 *
 * Clase para describir las expresiones para filtrar información.
 */
public class Expresion {
    
    private ComparadorEnum comparador;
    private String variable;
    private String valor;
    
    public Expresion(ComparadorEnum comparador, String variable, String valor) {
        this.comparador = comparador;
        this.variable = variable;
        this.valor = valor;
    }

    public ComparadorEnum getComparador() {
        return comparador;
    }

    public String getVariable() {
        return variable;
    }

    public String getValor() {
        return valor;
    }
    
    @Override
    public String toString(){
        String op;
        switch(comparador){
            case IGUALDAD: op = "=";
            break;
            case MENOR_IGUAL: op = "<=";
            break;
            case MAYOR_IGUAL: op = ">=";
            break;
            case MAYOR: op = ">";
            break;
            case MENOR: op = "<";
            break;
            case DIFERENTE: op = "<>";
            break;
            default: op = "";
        }
        return "("+op+","+variable+","+valor+")";
    }
}
