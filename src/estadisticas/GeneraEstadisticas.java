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
    public static ArrayList<PromedioRating> estadisticasRankingPromedio()  {
      ArrayList<PromedioRating> promedioRatings = new ArrayList<>();
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
                  if (!columna.equals("title") && !columna.equals("rating")) {
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
              promedioRatings.add(new PromedioRating(titulo, promedio, valores));
          }
      } catch (FileNotFoundException e) {
          System.out.printf("No se puede leer el archivo %s%n", Worker.DIR_RESULTADO);
      } catch (Exception e) {
          System.out.println(e);
          e.printStackTrace();
      }
      Collections.sort(promedioRatings);
      return promedioRatings;
    }


}
