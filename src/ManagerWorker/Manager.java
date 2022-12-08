package ManagerWorker;

import utilidades.Expresion;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Clase para modelar el Manager.
 */
public class Manager {

    private static final String DIR_SUBARCHIVOS = "data" + System.getProperty("file.separator") + "output" + System.getProperty("file.separator") + "subarchivo-";

    /**
     * Crea un pool de hilos y obten la información de cada hilo.
     * @param numHilos hilos a crear en el pool.
     * @param expresiones filtros para los registros.
     * @param seleccionadas columnas seleccionadas
     */
    public static void filtraInformacion(int numHilos, ArrayList<ArrayList<Expresion>> expresiones, String seleccionadas) {
        // El buffer donde los hilos escriben
        Worker.inicializaBufferConcurrente(seleccionadas);

        ExecutorService poolWorkers = Executors.newFixedThreadPool(numHilos);

        for (int i = 0; i < numHilos; i++) {
            String subarchivo = DIR_SUBARCHIVOS + Integer.toString(i+1) + ".csv";
            poolWorkers.execute(new Worker(subarchivo, expresiones, seleccionadas));
        }

        int cantidadWorkers = Thread.activeCount() - 1;
        System.out.println("Threads actuales: " + cantidadWorkers);

        poolWorkers.shutdown();
        while (! poolWorkers.isTerminated()) {
        }

        // Cierra el buffer cuando todos los Threads terminaron su tarea
        Worker.cierraBufferConcurrente();
    }
}
