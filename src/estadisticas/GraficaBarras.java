package estadisticas;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

/**
 * Manejo de la grafica de barras.
 */
 public class GraficaBarras {

     private final static int ELEMENTOS_PAGINA = 5;

     private int numResultados;

     private int indexInclusive;

     private int indexExclusive;

     private ArrayList<EstadisticaRating> promediosRating;
     private ArrayList<EstadisticaRating> minimosRating;
     private ArrayList<EstadisticaRating> maximosRating;
     private ArrayList<EstadisticaRating> medianasRating;

     private BarChart barChart;

     /*
     * Crea la grafica de barras tomando un rango de datos.
     */
     public GraficaBarras() {
       NumberAxis xAxis = new NumberAxis();
       CategoryAxis yAxis = new CategoryAxis();
       this.indexInclusive = -ELEMENTOS_PAGINA;
       this.indexExclusive = 0;
       this.barChart = new BarChart<>(xAxis, yAxis);
       this.barChart.setAnimated(false);
     }

     public BarChart getBarChart() {
         return this.barChart;
     }

     public int getNumResultados() {
         return this.numResultados;
     }

     /**
      * Muestra los siguientes N registros.
      */
     public void avanza() {
         if (this.promediosRating == null) {
             return;
         }
         if (this.promediosRating.isEmpty()) {
             return;
         }
         this.indexInclusive += ELEMENTOS_PAGINA;
         this.indexExclusive += ELEMENTOS_PAGINA;
         if (this.indexExclusive > this.promediosRating.size()) {
             this.indexExclusive = this.promediosRating.size();
             this.indexInclusive = (this.promediosRating.size() / ELEMENTOS_PAGINA) * ELEMENTOS_PAGINA;
             if (this.indexInclusive == this.indexExclusive) {
                 this.indexInclusive -= ELEMENTOS_PAGINA;
                 if (this.indexInclusive < 0) {
                   this.indexInclusive = 0;
                 }
             }
         }
         actualizaDatos();
     }

     /**
      * Muestra los N registros anteriores.
      */
     public void retrocede() {
         if (this.promediosRating == null) {
             return;
         }
         if (this.promediosRating.isEmpty()) {
             return;
         }
         if (this.indexExclusive % ELEMENTOS_PAGINA != 0) {
             this.indexExclusive -= this.indexExclusive % ELEMENTOS_PAGINA;
             this.indexInclusive = this.indexExclusive - ELEMENTOS_PAGINA;
         } else {
             this.indexExclusive -= ELEMENTOS_PAGINA;
             this.indexInclusive -= ELEMENTOS_PAGINA;
             if (this.indexInclusive < 0) {
                 this.indexInclusive = 0;
                 if (ELEMENTOS_PAGINA > this.promediosRating.size()) {
                     this.indexExclusive = this.promediosRating.size();
                 } else {
                     this.indexExclusive = ELEMENTOS_PAGINA;
                 }
             }
         }
         actualizaDatos();
     }

     /**
      * Calcula otro archivo.
      */
     public void reset() {
        this.promediosRating = GeneraEstadisticas.estadisticasRatingPromedio();
        this.medianasRating = GeneraEstadisticas.estadisticasRatingMediana();
        this.maximosRating = GeneraEstadisticas.estadisticasRatingMinimo(false);
        this.minimosRating = GeneraEstadisticas.estadisticasRatingMinimo(true);
        int elementos = promediosRating.size();
        if (elementos == 0) {
          this.indexInclusive = -ELEMENTOS_PAGINA;
          this.indexExclusive = 0;
          this.barChart.setTitle("Sin resultados");
          this.barChart.setData(FXCollections.observableArrayList());
          return;
        }
        if (medianasRating.size() != elementos ||
            maximosRating.size() != elementos ||
            minimosRating.size() != elementos) {
            return;
        }
        this.numResultados = elementos;
        this.indexInclusive = -ELEMENTOS_PAGINA;
        this.indexExclusive = 0;
        avanza();
     }

     /**
      * Actualiza los graficos de la barra tomando un conjunto de datos en un rango.
      */
     private void actualizaDatos() {
         ObservableList<XYChart.Series<Double, String>> data = obtenDatos();
         this.barChart.setTitle("(" + Integer.toString(this.numResultados) + ") " +  "Resultados de " +
                             Integer.toString(this.indexInclusive + 1) + " a " +
                             Integer.toString(this.indexExclusive));
         this.barChart.setData(data);
     }

     /**
      * Crea un objeto reconocible para la interfaz grafica que contiene los datos en un rango dado.
      */
     private ObservableList<XYChart.Series<Double, String>> obtenDatos() {
       ArrayList<EstadisticaRating> topPromedio = new ArrayList<> (this.promediosRating.subList(this.indexInclusive, this.indexExclusive));
       ArrayList<EstadisticaRating> topMediana = new ArrayList<> (this.medianasRating.subList(this.indexInclusive, this.indexExclusive));
       ArrayList<EstadisticaRating> topMinimo = new ArrayList<> (this.minimosRating.subList(this.indexInclusive, this.indexExclusive));
       ArrayList<EstadisticaRating> topMaximo = new ArrayList<> (this.maximosRating.subList(this.indexInclusive, this.indexExclusive));

       ObservableList<XYChart.Series<Double, String>> data = FXCollections.observableArrayList();
       XYChart.Series<Double, String> seriePromedio = new XYChart.Series<>();
       XYChart.Series<Double, String> serieMediana = new XYChart.Series<>();
       XYChart.Series<Double, String> serieMinimo = new XYChart.Series<>();
       XYChart.Series<Double, String> serieMaximo = new XYChart.Series<>();

       seriePromedio.setName("Rating promedio");
       serieMediana.setName("Rating mediana");
       serieMinimo.setName("Rating minimo");
       serieMaximo.setName("Rating maximo");

       for (EstadisticaRating promedioRating : topPromedio) {
         seriePromedio.getData().add(new XYChart.Data<> ((double) promedioRating.getEstadistica(), promedioRating.getParametros()));
       }
       for (EstadisticaRating medianaRating : topMediana) {
         serieMediana.getData().add(new XYChart.Data<> ((double) medianaRating.getEstadistica(), medianaRating.getParametros()));
       }
       for (EstadisticaRating minimoRating : topMinimo) {
         serieMinimo.getData().add(new XYChart.Data<> ((double) minimoRating.getEstadistica(), minimoRating.getParametros()));
       }
       for (EstadisticaRating maximoRating : topMaximo) {
         serieMaximo.getData().add(new XYChart.Data<> ((double) maximoRating.getEstadistica(), maximoRating.getParametros()));
       }
       data.addAll(seriePromedio, serieMediana, serieMinimo, serieMaximo);
       return data;
     }
 }
