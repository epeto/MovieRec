package utilidades;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Divide la base de datos fuente en subarchivos.
 */
public class Divisor {
    // Lugar en donde escribir los subarchivos
    private static final String WRITE_DIR = "data/output/";

    // Registros en la base de datos
    //private static final int NUM_RATINGS = 27753444;
    // Registro temporal de 8000 registros
    private static final int NUM_RATINGS = 8000;
    
    /**
     * Divide el archivo especificado en la ruta en multiples subarchivos, 
     * tantos como haya hilos.
     * @param numHilos el numero de subarchivos.
     * @param direccion ruta del archivo fuente.
     */    
    public static void divideArchivos(int numHilos, String direccion) {
        // Cantidad de registros para cada subarchivo
        int num_ratings_hilo = NUM_RATINGS / numHilos;
        int residuo_ratings_hilo = NUM_RATINGS % numHilos;
        
        // Linea sobre la cual debe empezar a leer cada registro
        int linea_inicial = 1;
        // El i-esimo archivo en crearse
        int archivos = 1;

        while (numHilos != 0) {
            // Registros a guardar en un subarchivo
            ArrayList<String> registrosHilo = new ArrayList<String>(); 
            // Lee el archivo y obten una cantidad determinada de registros a partir de una linea del archivo
            leerArchivo(direccion, num_ratings_hilo, linea_inicial, registrosHilo);            
            linea_inicial += num_ratings_hilo;           
            numHilos--;
            // Añade los registros faltantes al ultimo hilo
            if (numHilos == 1) {
                num_ratings_hilo += residuo_ratings_hilo;
            }            
            escribeArchivo(registrosHilo, archivos);
            archivos++;
        }
        

    }
    
    /**
    * Leer tantas lineas del archivo en la dirección dada, a partir de una linea i.
    * Agrega en la lista de registros los registros correspondientes al subarchivo.
    * @param direccion la dirección del archivo.
    * @param lineas cantidad de lineas del archivo a leer.
    * @param i la linea a partir de la cual se empieza a leer el archivo.
    * @param registros la lista con los registros del subarchivo.
    */
    private static void leerArchivo(String direccion, int lineas, int i, ArrayList<String> registros) {

        File archivo = new File (direccion);
        try( FileReader fr = new FileReader(archivo);
             BufferedReader br = new BufferedReader(fr);) {
             int linea_actual = 1;
             // Agrega las columnas
             String columnas = br.readLine();
             registros.add(columnas);
            // Llega hasta la linea donde tiene que iniciar la lectura    
            while (linea_actual != i) {
                br.readLine();
                linea_actual += 1;
            }
            
            // Agrega tantas lineas como se indica
            while (lineas != 0) {
                String registro = br.readLine();   
                registros.add(registro);
                lineas -= 1;
            }            
        
        } catch (FileNotFoundException e) {
            System.out.printf("No se puede leer el archivo %s%n", direccion);
        } catch (Exception e) {
            System.out.println(e);
        }        
    }
    
    
    /**
     * Dada una lista de registros, guardarlos en un archivo con un nombre identificado con un número.
     * @param registros los registros a guardar.
     * @param numArchivo identificador para el nombre del archivo.
     */
    private static void escribeArchivo(ArrayList<String> registros, int numArchivo) {
        File dir = new File(WRITE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String nombreArchivo = "subarchivo-" + numArchivo + ".csv";
        File file = new File(WRITE_DIR + nombreArchivo);
        try( FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw);){
            for(String registro : registros) {
               pw.write(registro + "\n");
            }            
        } catch(IOException e) {
            System.out.printf("No se puede escribir en %s%n", WRITE_DIR + nombreArchivo);
        } catch(Exception e) {
            System.out.println(e);
        }        
    }
}
