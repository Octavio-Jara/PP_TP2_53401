package modelo.actividades;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import excepciones.CupoExcedidoException;
import modelo.Estudiante;
import modelo.Inscripcion;

/**
 * Actividad forma parte de un EventoUniversitario.
 * La lista de inscripciones permite materializar la clase asociativa Inscripcion.
 */
public abstract class Actividad implements Serializable{
    protected int id;
    protected String titulo;
    protected int cupoMaximo;

    /* Notar que agregar aquí una lista de inscripciones no tiene  que ver con la definición de la clase asociativa en el modelo.
    * La clase asociativa se define porque además de vincular un estudiante con una actividad, agrega otros atributos que les son propios como
    * la fecha de inscripción o el estado. Agregar aquí la lista de inscripciones es una decisión de implementación sobre la navegabilidad de los datos
    * y no del modelo.
    * Quiza algun alumno se pregunte por qué no haber agregado aquí directamente la lista de estudiantes inscriptos en la actividad, evitando el uso de la clase asociativa.
    *  La respuesta es que cada inscripción tiene otros atributos que les son propios y que exceden a la relación actividad-estudiantes */
    private List<Inscripcion>  inscripciones ;

    /* Variables de clase */
    public static final int CUPO_MINIMO ;

    /* Inicializador estático */
    static {
        CUPO_MINIMO = 5;
        System.out.println("Inicializador estático: se cargó la clase Actividad.");
    }


    public Actividad(int id, String titulo, int cupo) {
        this.id = id;
        this.titulo = titulo;
        this.cupoMaximo = (cupo > CUPO_MINIMO) ? cupo : CUPO_MINIMO;
        this.inscripciones = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return;
        }
        this.titulo = titulo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupo) {
        this.cupoMaximo = (cupo > CUPO_MINIMO) ? cupo : CUPO_MINIMO;
    }

    public Inscripcion inscribir(Estudiante estudiante) throws CupoExcedidoException {
        if (inscripciones.size() >= cupoMaximo) {
            throw new CupoExcedidoException("No se puede inscribir al estudiante " + estudiante.getNombre() + ". Cupo máximo alcanzado.");
        }
        Inscripcion inscripcion = new Inscripcion(this, estudiante, LocalDate.now(), "REGISTRADA");
        inscripciones.add( inscripcion);
        return inscripcion;
    }


    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void mostrarInscripciones() {
        if (inscripciones.isEmpty()) {
            System.out.println("  Sin inscripciones registradas.");
            return;
        }
        System.out.println("   Inscripciones registradas:");
        for (Inscripcion inscripcion : inscripciones) {
            System.out.println("   " + inscripcion.getFecha() +" - "+  inscripcion.getEstado()+ " - " + inscripcion.getEstudiante().getNombre() + " (Legajo: " + inscripcion.getEstudiante().getLegajo() + ")");
        }
    }
        // PUNTO 3 YO / LUCAS HOLA

    public final void mostrarIdentificacion(){
        System.out.println("-" + getTipo() + ":" + titulo + " (id=" + id + ", cupo:" + cupoMaximo + ")");
    }

    public abstract String getTipo();
    public abstract double calcularCostoMateriales();
}
