package ManagerWorker;

import utilidades.Expresion;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 *
 * Clase para el comportamiento de cada Worker.
 */
public class Worker extends Thread {
    
    // Direccion del archivo donde se guardan los resultados de cada worker
    public final static String DIR_RESULTADO = "data/resultados.csv"; 
    // El buffer general para escribir
    public static BufferedWriter bufferResultado;
    private String archivo;
    private ArrayList<ArrayList<Expresion>> expresiones;
    private String colSelect;
     
    /**
     * Recibe el archivo sobre el cual va a trabajar.
     * @param archivo el archivo a realizar la operación de filtrado.
     * @param expresiones las listas de expresiones para filtrar el archivo.
     * @param seleccion arreglo con las columnas seleccionadas.
     */
    public Worker(String archivo, ArrayList<ArrayList<Expresion>> expresiones, String seleccion) {
        this.archivo = archivo;
        this.expresiones = expresiones;
        colSelect = seleccion;
    }

    /**
     * Inicializa el buffer de escritura concurrente para los hilos.
     */
    static void inicializaBufferConcurrente() {
        try {            
            File file = new File(DIR_RESULTADO);
            file.createNewFile();
            FileWriter fw = new FileWriter(DIR_RESULTADO);
            bufferResultado = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.printf("No se puede escribir en el archivo %s%n", DIR_RESULTADO);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    /**
     * Una vez que los hilos hayan terminado su tarea, cierra el buffer.
     */
    static void cierraBufferConcurrente() {
        try {
            bufferResultado.close();   
        } catch (IOException e) {
            System.out.println("No se pudo cerrar el buffer de escritura compartida");
        } catch (Exception e) {
            System.out.println(e);
        }
    }    
    
    @Override
    public void run() {
       manejaArchivo(); 
    }
    
    /**
     * Hace una proyección de un registro, dejando solamente las columnas seleccionadas.
     * @param registro
     * @param indSelec lista de índices de las columnas seleccionadas.
     * @return registro sin algunas columnas.
     */
    private String seleccionaColumnas(String registro, ArrayList<Integer> indSelec){
        String salida = "";
        String[] regSplit = registro.split(",");
        for(int i=0; i<indSelec.size()-1; i++){
            int indiceCol = indSelec.get(i);
            salida += regSplit[indiceCol]+",";
        }
        salida += regSplit[indSelec.get(indSelec.size()-1)];
        return salida;
    }
    
    /**
     * Hace el filtrado de información del subarchivo que le tocó.
     */
    private void manejaArchivo() {
        System.out.println(">>>>> Worker " + this.getName() + " trabajando con " + this.archivo);
        File subarchivo = new File (this.archivo);
        try( FileReader fr = new FileReader(subarchivo);
            BufferedReader br = new BufferedReader(fr);) {
            String registro = br.readLine();
            // En la primera fila estan las columnas
            String[] columnaSubarchivo = registro.split(",");
            ArrayList<Integer> indicesColumnas = new ArrayList<>();
            HashMap<String, Integer> tablaNombres = new HashMap<>();
            for(int i=0; i<columnaSubarchivo.length; i++){
                tablaNombres.put(columnaSubarchivo[i], i);
            }
            
            String [] proyeccion;
            if(colSelect.equals("*")){
                proyeccion = columnaSubarchivo;
            }else{
                proyeccion = colSelect.split(",");
            }
            
            for(String cs : proyeccion){
                indicesColumnas.add(tablaNombres.get(cs));
            }
            while ((registro = br.readLine()) != null) {
                if(expresiones.isEmpty()){
                    escribeArchivo(registro+"\n");
                }else{
                    for (ArrayList<Expresion> listaExpresiones : expresiones) {
                        // Filtra las columnas 
                        if (satisfaceCondiciones(registro, listaExpresiones, tablaNombres)) {
                            String proyectado = seleccionaColumnas(registro, indicesColumnas);
                            escribeArchivo(proyectado + "\n");
                            break; // verificar si no sale un bug por esta línea
                        }                
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.printf("%s no pudo escribir en el archivo %s porque no existe", this.getName(), this.archivo);
        } catch (Exception e) {
            System.out.println(e);
        }
    }  
    
    
    /**
     * Escribe de manera concurrente un registro en un archivo especifico para guardar los resultados.
     * @param registro el registro a guardar en el archivo general.
     */
    private synchronized void escribeArchivo(String registro) {
        try {
            bufferResultado.write(registro);
            bufferResultado.flush();
        } catch (IOException e) {
            System.out.printf("%s no pudo escribir en el archivo %s%n", this.getName(), DIR_RESULTADO);
        } catch (Exception e) {
            System.out.println(e);
        } 
    }
    

    /**
     * Le asigna un número dependiendo del tipo de columna
     * @param nombreColumna
     * @return 0 si es genres, 1 si es tipo double, 2 si es entero, 3 en otro caso.
     */
    private int tipoColumna(String nombreColumna){
        switch(nombreColumna){
            case "genres": return 0;
            case "rating": return 1;
            case "idRating":
            case "userId":
            case "movieId":
            case "timestamp":
            case "year":
            case "age": return 2;
            default: return 3;
        }
    }
    
    /**
     * Revisa si el registro dado satisface las condiciones.
     * @param registro 
     * @param listaExpresiones
     * @param tablaNombres
     */
    private boolean satisfaceCondiciones(String registro, ArrayList<Expresion> listaExpresiones, HashMap<String, Integer> tablaNombres) {
        String[] registroSeparado = registro.split(",");
        // Como es una lista de conjunciones, se devuelve falso al primero que
        // no cumpla con la condición.
        for(Expresion expr : listaExpresiones){
            String valorEsperado = expr.getValor();
            int indCol = tablaNombres.get(expr.getVariable());
            String valorReal = registroSeparado[indCol];
            Comparable vrComp;
            Comparable veComp;
            switch(tipoColumna(expr.getVariable())){
                case 1 : vrComp = Double.valueOf(valorReal);
                         veComp = Double.valueOf(valorEsperado);
                break;
                case 2 : vrComp = Integer.valueOf(valorReal);
                         veComp = Integer.valueOf(valorEsperado);
                break;
                default: vrComp = valorReal;
                         veComp = valorEsperado;
            }
            
            if(tipoColumna(expr.getVariable()) == 0){
                String[] generos = valorReal.split("\\|");
                switch(expr.getComparador()){
                    case IGUALDAD:
                    if(!valorReal.contains(valorEsperado)){
                        return false;
                    }
                    break;
                    case DIFERENTE:
                    if(valorReal.contains(valorEsperado)){
                        return false;
                    }
                    break;
                    case MAYOR_IGUAL:
                    for(String gen : generos){
                        if(gen.compareTo(valorEsperado) < 0){
                            return false;
                        }
                    }
                    break;
                    case MENOR_IGUAL:
                    for(String gen : generos){
                        if(gen.compareTo(valorEsperado) > 0){
                            return false;
                        }
                    }
                    break;
                    case MAYOR:
                    for(String gen : generos){
                        if(gen.compareTo(valorEsperado) < 0
                         ||gen.equals(valorEsperado)){
                            return false;
                        }
                    }
                    break;
                    case MENOR:
                    for(String gen : generos){
                        if(gen.compareTo(valorEsperado) > 0
                         ||gen.equals(valorEsperado)){
                            return false;
                        }
                    }
                }
            }else{
                switch(expr.getComparador()){
                    case IGUALDAD:
                    if(!vrComp.equals(veComp)){
                        return false;
                    }
                    break;
                    case DIFERENTE:
                    if(vrComp.equals(veComp)){
                        return false;
                    }
                    break;
                    case MAYOR_IGUAL:
                    if(vrComp.compareTo(veComp) < 0){
                        return false;
                    }
                    break;
                    case MENOR_IGUAL:
                    if(vrComp.compareTo(veComp) > 0){
                        return false;
                    }
                    break;
                    case MAYOR:
                    if(vrComp.compareTo(veComp) <= 0){
                        return false;
                    }
                    break;
                    case MENOR:
                    if(vrComp.compareTo(veComp) >= 0){
                        return false;
                    }
                    break;
                }
            }
        }

        return true;
    }
 
    
    
}
