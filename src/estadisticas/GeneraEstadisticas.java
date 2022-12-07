package estadisticas;

import ManagerWorker.Worker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

/**
 * Genera las estadisticas.
 */
 public class GeneraEstadisticas {

     /**
      * Genera las estadisticas sobre el rating promedio de los registros en el archivo de resultados.
      * @return una lista de objetos con el promedio y la informacion obtenida de cada pelicula.
      */
     public static ArrayList<EstadisticaRating> estadisticasRatingPromedio()  {
       ArrayList<EstadisticaRating> estadisticaRating = new ArrayList<>();
       File archivo = new File (Worker.DIR_RESULTADO);
       try( FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);) {
            String[] cols = br.readLine().split(",");
            for (int i = 0; i < cols.length; i++) {
              cols[i] = cols[i].strip();
            }
            ArrayList<String> columnas = new ArrayList<>(Arrays.asList(cols));
            String registro;
            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<Float> promedios = new ArrayList<>();
            ArrayList<Integer> frecuencias = new ArrayList<>();
            ArrayList<String> parametros = new ArrayList<>();

            while ((registro = br.readLine()) != null) {
               String[] valores = registro.split(",");
               int index = columnas.indexOf("title");
               String titulo = valores[index];
               if (!titulos.contains(titulo)) {
                 titulos.add(titulo);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 promedios.add(rating);
                 frecuencias.add(1);
                 String params = titulo;
                 for (int i = 0; i < columnas.size(); i++) {
                   String columna = columnas.get(i);
                   if (!columna.equals("title") && !columna.equals("rating") &&
                       !columna.equals("age") && !columna.equals("name") &&
                       !columna.equals("lastname")) {
                     params += ", " + valores[i];
                   }
                 }
                 parametros.add(params);
               } else {
                 int indexTitulo = titulos.indexOf(titulo);
                 int frec = frecuencias.get(indexTitulo);
                 float promedio = promedios.get(indexTitulo);
                 frecuencias.set(indexTitulo, frec + 1);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 promedios.set(indexTitulo, promedio + rating);
               }
           }


           for (int i = 0; i < titulos.size() ;i++) {
               String titulo = titulos.get(i);
               int frecuencia = frecuencias.get(i);
               float suma = promedios.get(i);
               float promedio = suma / frecuencia;
               promedios.set(i, promedio);
               String valores = parametros.get(i);
               estadisticaRating.add(new EstadisticaRating(titulo, promedio, valores));
           }
       } catch (FileNotFoundException e) {
           System.out.printf("No se puede leer el archivo %s%n", Worker.DIR_RESULTADO);
       } catch (Exception e) {
           System.out.println(e);
           e.printStackTrace();
       }
       Collections.sort(estadisticaRating);
       return estadisticaRating;
     }

     /**
      *Genera las estadisticas sobre el rating minimo o maximo de los registros en el archivo de resultados.
      * @param calculaMinimo si es true, regresa el minimo, en otro caso el maximo.
      * @return una lista de objetos con el minimo o maximo y la informacion obtenida de cada pelicula.
      */
     public static ArrayList<EstadisticaRating> estadisticasRatingMinimo(boolean calculaMinimo) {
       ArrayList<EstadisticaRating> estadisticaRating = new ArrayList<>();
       File archivo = new File (Worker.DIR_RESULTADO);
       try( FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);) {
            String[] cols = br.readLine().split(",");
            for (int i = 0; i < cols.length; i++) {
              cols[i] = cols[i].strip();
            }
            ArrayList<String> columnas = new ArrayList<>(Arrays.asList(cols));
            String registro;
            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<Float> minimos = new ArrayList<>();
            ArrayList<String> parametros = new ArrayList<>();


            while ((registro = br.readLine()) != null) {
               String[] valores = registro.split(",");
               int index = columnas.indexOf("title");
               String titulo = valores[index];
               if (!titulos.contains(titulo)) {
                 titulos.add(titulo);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 minimos.add(rating);
                 String params = titulo;
                 for (int i = 0; i < columnas.size(); i++) {
                   String columna = columnas.get(i);
                   if (!columna.equals("title") && !columna.equals("rating") &&
                       !columna.equals("age") && !columna.equals("name") &&
                       !columna.equals("lastname")) {
                     params += ", " + valores[i];
                   }
                 }
                 parametros.add(params);
               } else {
                 int indexTitulo = titulos.indexOf(titulo);
                 Float min = minimos.get(indexTitulo);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 if (calculaMinimo) {
                     if (rating < min) {
                         minimos.set(indexTitulo, rating);
                     }
                 } else {
                     if (rating > min) {
                         minimos.set(indexTitulo, rating);
                     }
                 }
               }
            }
            for (int i = 0; i < titulos.size() ;i++) {
                 String titulo = titulos.get(i);
                 float minimo = minimos.get(i);
                 String valores = parametros.get(i);
                 estadisticaRating.add(new EstadisticaRating(titulo, minimo, valores));
            }
       } catch (FileNotFoundException e) {
           System.out.printf("No se puede leer el archivo %s%n", Worker.DIR_RESULTADO);
       } catch (Exception e) {
           e.printStackTrace();
       }
       Collections.sort(estadisticaRating);
       return estadisticaRating;
     }

     /**
      * Genera las estadisticas sobre el rating mediana de los registros en el archivo de resultados.
      * @return una lista de objetos el rating mediana y la informacion obtenida de cada pelicula.
      */
     public static ArrayList<EstadisticaRating> estadisticasRatingMediana() {
       ArrayList<EstadisticaRating> estadisticaRating = new ArrayList<>();
       File archivo = new File (Worker.DIR_RESULTADO);
       try( FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);) {
            String[] cols = br.readLine().split(",");
            for (int i = 0; i < cols.length; i++) {
              cols[i] = cols[i].strip();
            }
            ArrayList<String> columnas = new ArrayList<>(Arrays.asList(cols));
            String registro;
            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<ArrayList<Float>> ratingsPorTitulo = new ArrayList<>();
            ArrayList<String> parametros = new ArrayList<>();


            while ((registro = br.readLine()) != null) {
               String[] valores = registro.split(",");
               int index = columnas.indexOf("title");
               String titulo = valores[index];
               if (!titulos.contains(titulo)) {
                 titulos.add(titulo);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 ArrayList<Float> ratingsTitulo = new ArrayList<>();
                 ratingsTitulo.add(rating);
                 ratingsPorTitulo.add(ratingsTitulo);
                 String params = titulo;
                 for (int i = 0; i < columnas.size(); i++) {
                   String columna = columnas.get(i);
                   if (!columna.equals("title") && !columna.equals("rating") &&
                       !columna.equals("age") && !columna.equals("name") &&
                       !columna.equals("lastname")) {
                     params += ", " + valores[i];
                   }
                 }
                 parametros.add(params);
               } else {
                 int indexTitulo = titulos.indexOf(titulo);
                 int indexRating = columnas.indexOf("rating");
                 float rating = Float.parseFloat(valores[indexRating].strip());
                 ArrayList<Float> ratings = ratingsPorTitulo.get(indexTitulo);
                 ratings.add (rating);
                 ratingsPorTitulo.set(indexTitulo, ratings);
               }
            }
            for (int i = 0; i < titulos.size() ;i++) {
                 String titulo = titulos.get(i);
                 ArrayList<Float> ratings = ratingsPorTitulo.get(i);
                 Collections.sort(ratings);
                 Float media = ratings.get(ratings.size()/2);
                 String valores = parametros.get(i);
                 estadisticaRating.add(new EstadisticaRating(titulo, media, valores));
            }
       } catch (FileNotFoundException e) {
           System.out.printf("No se puede leer el archivo %s%n", Worker.DIR_RESULTADO);
       } catch (Exception e) {
           e.printStackTrace();
       }
       Collections.sort(estadisticaRating);
       return estadisticaRating;
     }


 }
