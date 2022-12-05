package estadisticas;

/**
 * Objeto que guarda la informacion necesaria para obtener promedio.
 */
public class PromedioRating implements Comparable<PromedioRating> {
    private final String titulo;
    private final float promedio;
    private final String parametros;

    public PromedioRating(String titulo, float promedio, String parametros){
        this.titulo = titulo;
        this.promedio = promedio;
        this.parametros = parametros;
    }

    @Override
    public int compareTo(PromedioRating promedioRating) {
        return this.titulo.strip().compareTo(promedioRating.getTitulo().strip());
    }

    public String getTitulo () {
        return this.titulo;
    }

    public float getPromedio() {
        return promedio;
    }

    public String getParametros() {
        return parametros;
    }

}
