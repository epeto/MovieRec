package estadisticas;

/**
 * Objeto que guarda la informacion necesaria para obtener promedio.
 */
 public class EstadisticaRating implements Comparable<EstadisticaRating> {
     private final String titulo;
     private final float estadistica;
     private final String parametros;

     public EstadisticaRating(String titulo, float estadistica, String parametros){
         this.titulo = titulo;
         this.estadistica = estadistica;
         this.parametros = parametros;
     }

     @Override
     public int compareTo(EstadisticaRating valorRating) {
         return this.titulo.strip().compareTo(valorRating.getTitulo().strip());
     }

     public String getTitulo () {
         return this.titulo;
     }

     public float getEstadistica() {
         return this.estadistica;
     }

     public String getParametros() {
         return parametros;
     }

 }
