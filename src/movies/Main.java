
package movies;

import ManagerWorker.Manager;
import utilidades.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Sistema de recomendacion de peliculas
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
    
    @Override
    public void start(Stage primaryStage) {
        anchoVentana = 500;
        altoVentana = 500;
        escenario = primaryStage;

        caja = new VBox();
        
        labelSelect = new Label(" Indique las columnas a seleccionar ");
        
        inputSelect = new TextField();
        inputSelect.setPrefWidth(anchoVentana-100);
        
        labelWhere = new Label(" Escriba las condiciones de filtrado en forma normal disyuntiva. ");
        
        inputWhere = new TextField();
        inputWhere.setPrefWidth(anchoVentana-100);
        
        ejecuta = new Button(" Ejecutar consulta ");
        ejecuta.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ejecutaConsulta();
            }
        });
        
        caja.getChildren().addAll(labelSelect, inputSelect, labelWhere, inputWhere, ejecuta);
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
    
    public void ejecutaConsulta(){
        int numHilos = getNumHilos();
        // Probando con un archivo de pocos registros
        String direccion = "data/out-users-8000_v2.csv";
        Divisor.divideArchivos(numHilos, direccion);
        String select = inputSelect.getText();
        String expr = inputWhere.getText();
        // Interprete envia las clausulas de filtrado....
        ArrayList<ArrayList<Expresion>> expresiones = Parser.analiza(expr);
        // Realiza el filtrado sobre los workers
        Manager.filtraInformacion(numHilos, expresiones, select);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Query");
        alert.setHeaderText("Consulta finalizada.");
        alert.setContentText("Revisar archivo data/resultado.csv");
        alert.showAndWait();

        inputSelect.setText("");
        inputWhere.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
