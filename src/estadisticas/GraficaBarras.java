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

    private int indexInclusive;

    private int indexExclusive;

    private ArrayList<PromedioRating> promediosRatings;

    private final BarChart barChart;

    /*
    * Crea la grafica de barras tomando un rango de datos.
    */
    public GraficaBarras() {
      NumberAxis xAxis = new NumberAxis();
      CategoryAxis yAxis = new CategoryAxis();
      this.indexInclusive = -10;
      this.indexExclusive = 0;
      this.barChart = new BarChart<>(xAxis, yAxis);
      this.barChart.setAnimated(true);
    }

    public BarChart getBarChart() {
        return this.barChart;
    }

    /**
     * Muestra los siguientes N registros.
     */
    public void avanza() {
        this.indexInclusive += 10;
        this.indexExclusive += 10;
        if (this.indexExclusive > this.promediosRatings.size() ) {
            this.indexExclusive = this.promediosRatings.size();
            this.indexInclusive = (this.promediosRatings.size() / 10) * 10;
        }
        actualizaDatos();
    }

    /**
     * Muestra los N registros anteriores.
     */
    public void retrocede() {
        if (this.indexExclusive % 10 != 0 && this.indexExclusive >= 0) {
            int i = this.indexExclusive / 10;
            if (i == 0) {
                this.indexExclusive = this.promediosRatings.size();
                this.indexInclusive = 0;
            } else {
                this.indexExclusive = 10 * (i);
                this.indexInclusive = this.indexExclusive - 10;
            }
            actualizaDatos();
            return;
        }
        this.indexInclusive -= 10;
        this.indexExclusive -= 10;
        if (this.indexInclusive < 0) {
            this.indexInclusive = 0;
            this.indexExclusive = 10;
            if (this.indexExclusive > this.promediosRatings.size()) {
                this.indexExclusive = this.promediosRatings.size();
            }
        }
        actualizaDatos();
    }

    /**
     * Calcula otro archivo.
     */
    public void reset() {
       this.promediosRatings = GeneraEstadisticas.estadisticasRankingPromedio();
       this.indexInclusive = -10;
       this.indexExclusive = 0;
       avanza();
    }

    /**
     * Actualiza los graficos de la barra tomando un conjunto de datos en un rango.
     */
    private void actualizaDatos() {
        ObservableList<XYChart.Series<Double, String>> data = obtenDatos();
        this.barChart.setTitle("Resultados de " +
                            Integer.toString(this.indexInclusive + 1) + " a " +
                            Integer.toString(this.indexExclusive));
        this.barChart.setData(data);
    }

    /**
     * Crea un objeto reconocible para la interfaz grafica que contiene los datos en un rango dado.
     */
    private ObservableList<XYChart.Series<Double, String>> obtenDatos() {
      ArrayList<PromedioRating> top = new ArrayList<> (this.promediosRatings.subList(this.indexInclusive, this.indexExclusive));
      ObservableList<XYChart.Series<Double, String>> data = FXCollections.observableArrayList();
      XYChart.Series<Double, String> serie = new XYChart.Series<>();
      serie.setName("Rating promedio");
      for (PromedioRating promedioRating : top) {
        serie.getData().add(new XYChart.Data<> ((double) promedioRating.getPromedio(), promedioRating.getParametros()));
      }
      data.add(serie);
      return data;
    }
}
