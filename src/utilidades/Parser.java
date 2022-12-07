/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @throws UnsupportedOperationException
     */
     private static Expresion buildExpr(String expr) throws UnsupportedOperationException{
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
        if (!revisaCorrectas(separado[0].strip())) {
            throw new UnsupportedOperationException("Esa variable no es nombre valido de columna.");
        }
        revisaSemantica(oe, separado[0].strip(), separado[1].strip());
        Expresion ne = new Expresion(oe, separado[0].strip(), separado[1].strip());
        return ne;
    }

    /**
     * Revisa si las operaciones descritas por el operando, la columna y el valor son validas.
     * @param op operador.
     * @param columna la columna a revisar.
     * @param valor el valor dado por el usuario.
     */
    public static void revisaSemantica(ComparadorEnum op, String columna, String valor) throws UnsupportedOperationException {
         if (op != ComparadorEnum.IGUALDAD) {
             if (!valor.matches("[0-9]+")) {
                 throw new UnsupportedOperationException("Tienes que tener un valor numérico.");
             }
         } else {
             if (columna.equals("movieId") || columna.equals("year")) {
                 if (!valor.matches("[0-9]+")) {
                     throw new UnsupportedOperationException("Tienes que tener un valor numérico.");
                 }
             }
         }
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

     /**
      * Revisa si las columnas dadas por el usuario son correctas.
      * @param select la entrada del usuario con las columnas.
      * @return true si la cadena tiene columnas validas, false en otro caso.
      */
    public static boolean revisaCorrectas(String select) {
        if(select.strip().equals("*")){
            return true;
        }
        String[] columnas = select.split(",");
        String[] totales = {"idRating", "userId", "movieId", "rating", "timestamp", "title", "year", "genres", "name", "lastname", "age", "imdb", "themoviedb"};
        List<String> listaColumnas = Arrays.asList(totales);
        for(String c : columnas) {
            String columna = c.strip();
            if (!listaColumnas.contains(columna)) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args){
        String ejemplo = "(age >= 20 AND age <= 30 AND year > 2000 AND genres = Thriller) OR (age >= 20 AND age <= 30 AND year > 2000 AND genres = Horror)";
        System.out.println(Parser.analiza(ejemplo));
    }
}
