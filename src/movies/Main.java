
package movies;

import ManagerWorker.Manager;
import ManagerWorker.Worker;
import utilidades.*;
import estadisticas.GraficaBarras;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.File;
import java.time.Instant;
import java.time.Duration;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Sistema de consulta de estadisticas de peliculas.
 */
public class Main extends Application{

    int anchoVentana;
    int altoVentana;
    Stage escenario;
    Label labelSelect;
    Label labelWhere;
    Button ejecuta;
    TextField inputSelect;
    TextField inputWhere;
    VBox caja;
    Button avanzaGrafica;
    Button retrocedeGrafica;

    @Override
    public void start(Stage primaryStage) {

        int numHilos = getNumHilos();
        String separator = System.getProperty("file.separator");
        String direccion = "data" + separator + "dataset.csv";
        Divisor.divideArchivos(numHilos, direccion);
        // Asigna el nombre al archivo segun la fecha y hora actuales
        asignaNombreArchivoResultado();

        anchoVentana = 800;
        altoVentana = 700;
        escenario = primaryStage;

        caja = new VBox();

        labelSelect = new Label(" Indique las columnas a seleccionar. ");

        inputSelect = new TextField();
        inputSelect.setPrefWidth(anchoVentana-100);

        labelWhere = new Label(" Escriba las condiciones de filtrado en forma normal disyuntiva. ");

        inputWhere = new TextField();
        inputWhere.setPrefWidth(anchoVentana-100);

        GraficaBarras grafica = new GraficaBarras();

        ejecuta = new Button(" Ejecutar consulta ");
        ejecuta.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               boolean valida = ejecutaConsulta();
               if (valida) {
                   grafica.reset();
                }
             }
        });


        avanzaGrafica = new Button("Avanzar");
        avanzaGrafica.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                grafica.avanza();
            }
         });

        retrocedeGrafica = new Button("Retroceder");
        retrocedeGrafica.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
             grafica.retrocede();
           }
        });

        HBox cajaBotones = new HBox();
        cajaBotones.setSpacing(20);
        cajaBotones.getChildren().addAll(ejecuta, retrocedeGrafica, avanzaGrafica);
        caja.getChildren().addAll(labelSelect, inputSelect, labelWhere, inputWhere, cajaBotones);
        caja.setSpacing(10);

        VBox cajaGraph = new VBox(grafica.getBarChart());
        cajaGraph.setMaxWidth(1500);
        cajaGraph.setAlignment(Pos.TOP_CENTER);
        caja.getChildren().add(cajaGraph);

        Scene scene = new Scene(caja, anchoVentana, altoVentana);
        escenario.setTitle("Consulta");
        escenario.setScene(scene);
        escenario.show();
    }

    /**
     * @return El número de hilos a usar es 4 veces el número de CPU's según la JVM.
     */
    public int getNumHilos() {
        int CPUs = Runtime.getRuntime().availableProcessors();
        return CPUs*4;
    }

     /**
      * Revisa las entradas del usuario.
      * Realiza la división de archivos.
      * Llama al manager para realizar el filtrado.
      * @return true si se pudo realizar la consulta, false en otro caso.
      */
     public boolean ejecutaConsulta(){
        String select = inputSelect.getText();
        String expr = inputWhere.getText();

        if (!Parser.revisaCorrectas(select)) {
             Alert alert = new Alert(AlertType.ERROR);
             alert.setTitle("Error");
             alert.setHeaderText("Columnas no validas.");
             alert.setContentText("Ingresa solo columnas validas: \n  movieId, title, year, genres, imdb, themoviedb");
             alert.showAndWait();
             return false;
         }
         ArrayList<ArrayList<Expresion>> expresiones = null;
         try {
             // Interprete envia las clausulas de filtrado
             expresiones = Parser.analiza(expr);
         } catch (Exception e) {
             Alert alert = new Alert(AlertType.ERROR);
             alert.setTitle("Error");
             alert.setHeaderText("Consulta no valida.");
             alert.setContentText("Ese comparador no está en nuestra gramática o la sintaxis de la cadena es incorrecta.");
             alert.showAndWait();
             return false;
         }
         if (select.strip().equals("*")) {
           select = "movieId,title,year,genres,rating,imdb,themoviedb,age,name,lastname";
         }
         if (!select.contains("title")) {
           select += ", title";
         }
         if (!select.contains("rating")) {
           select += ", rating";
         }

         // Realiza el filtrado sobre los workers
         Instant inicio = Instant.now();
         int numHilos = getNumHilos();
         Manager.filtraInformacion(numHilos, expresiones, select);
         Instant ultimo = Instant.now();
         long tiempoTotal = Duration.between(inicio, ultimo).toSeconds();


         Alert alert = new Alert(AlertType.CONFIRMATION);
         alert.setTitle("Query");
         alert.setHeaderText("Consulta finalizada (" + Long.toString(tiempoTotal) + " segundos)");
         alert.setContentText("Revisar archivo data/resultado.csv");
         alert.showAndWait();

         inputSelect.setText("");
         inputWhere.setText("");
         return true;
     }

     public void asignaNombreArchivoResultado() {
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
       LocalDateTime now = LocalDateTime.now();
       String separator = System.getProperty("file.separator");
       String dir = "data" + separator + "resultados" + separator;
       File file = new File(dir);
       if (!file.exists()) {
         file.mkdirs();
       }
       Worker.DIR_RESULTADO = dir + "dataset(" + dtf.format(now).toString() + ").csv";
       File resultados = new File(Worker.DIR_RESULTADO);
       try {
         resultados.createNewFile();
       } catch (IOException e) {
           System.out.printf("No se puede escribir en el archivo %s%n", Worker.DIR_RESULTADO);
       } catch (Exception e) {
           System.out.println(e);
       }
     }

     public static void main(String[] args) {
         launch(args);
     }

 }
