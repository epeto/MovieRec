/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilidades;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author emmanuel
 */
public class Parser {
    /**
     * Separa una expresión dado un conector.
     * @param expr expresión
     * @param conector AND u OR
     * @return lista de cada token
     */
    private static ArrayList<String> splitConector(String expr, String conector){
        String[] separado = expr.split(conector);
        for(int i=0; i<separado.length; i++){
            String ct = separado[i].strip();
            int indexIni = (ct.charAt(0) == '(')? 1 : 0;
            int indexFin = (ct.charAt(ct.length()-1) == ')')? ct.length()-1 : ct.length();
            separado[i] = ct.substring(indexIni, indexFin);
        }
        ArrayList<String> salida = new ArrayList<>(Arrays.asList(separado));
        return salida;
    }
    
    /**
     * Dado un string en forma normal disyuntiva, lo separa en tokens.
     * @param expr expresión a separar.
     * @return lista de listas de comparaciones.
     */
    private static ArrayList<ArrayList<String>> fnd(String expr){
        ArrayList<String> sinOr = splitConector(expr, "OR");
        ArrayList<ArrayList<String>> sinAnd = new ArrayList<>();
        for(var cadena : sinOr){
            sinAnd.add(splitConector(cadena, "AND"));
        }
        return sinAnd;
    }
    
    /**
     * Dada una expresión con un único comparador, devuelve el comparador.
     * @param expr expresión en string.
     * @return el operador dentro de la expresión.
     * @throws UnsupportedOperationException 
     */
    private static String extraeOperador(String expr) throws UnsupportedOperationException {
        if(expr.contains(">=")){
            return ">=";
        }else if(expr.contains("<=")){
            return "<=";
        }else if(expr.contains("<>")){
            return "<>";
        }else if(expr.contains(">")){
            return ">";
        }else if(expr.contains("<")){
            return "<";
        }else if(expr.contains("=")){
            return "=";
        }else{
            throw new UnsupportedOperationException("Ese comparador no está en nuestra gramática.");
        }
    }
    
    /**
     * Construye una expresión (de la clase Expresion) dado un string con exactamente un comparador.
     * @param expr expresión con un comparador.
     * @return Expresion construida a partir de un String.
     */
    private static Expresion buildExpr(String expr){
        String op = extraeOperador(expr);
        String[] separado = expr.split(op);
        ComparadorEnum oe = ComparadorEnum.IGUALDAD;
        switch(op){
            case "<=" : oe = ComparadorEnum.MENOR_IGUAL;
            break;
            case ">=" : oe = ComparadorEnum.MAYOR_IGUAL;
            break;
            case "<>" : oe = ComparadorEnum.DIFERENTE;
            break;
            case "<" : oe = ComparadorEnum.MENOR;
            break;
            case ">" : oe = ComparadorEnum.MAYOR;
            break;
            case "=" : oe = ComparadorEnum.IGUALDAD;
            break;
        }
        Expresion ne = new Expresion(oe, separado[0].strip(), separado[1].strip());
        return ne;
    }
    
    /**
     * Convierte una expresión en forma normal disyuntiva a una lista de listas de expresiones.
     * @param expr expresión en fnd.
     * @return expresiones separadas.
     */
    public static ArrayList<ArrayList<Expresion>> analiza(String expr){
        if(expr.equals("*")){
            return new ArrayList<>();
        }
        ArrayList<ArrayList<String>> fndExp = fnd(expr);
        ArrayList<ArrayList<Expresion>> expresiones = new ArrayList<>();
        for(ArrayList<String> lista : fndExp){
            ArrayList<Expresion> listaExpr = new ArrayList<>();
            for(String token : lista){
                listaExpr.add(buildExpr(token));
            }
            expresiones.add(listaExpr);
        }
        return expresiones;
    }
    
    public static void main(String[] args){
        String ejemplo = "(hola = 1 AND hola = 2) OR (mundo > 2 AND mundo < 8) OR (perro >= 0) OR (gato <= 3 AND gato = 6 AND gato = 9)";
        System.out.println(Parser.analiza(ejemplo));
    }
}
