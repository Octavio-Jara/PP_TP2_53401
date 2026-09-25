package modelo;
import java.io.Serializable;
import java.time.LocalDate;

import modelo.actividades.Actividad;

/**
 * Clase asociativa entre Actividad y Estudiantes.
 * La inscripción existe porque un estudiante se inscribe a una actividad concreta.
 * Debe notarse que esta relación tiene atributos propios que no pertenecen ni a la actividad ni al estudiante,
 * por eso es necesario modelarla como una clase independiente.
 */
public class Inscripcion implements Serializable{
    private Actividad actividad;
    private Estudiante estudiante;
    private LocalDate fecha;
    private String estado;

    private TicketDeAcceso ticket;

    public Inscripcion(Actividad actividad, Estudiante estudiante, LocalDate fecha, String estado) {
        this.actividad = actividad;
        this.estudiante = estudiante;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public TicketDeAcceso getTicket() {
        return ticket;
    }

    public void confirmar() {
        this.estado = "CONFIRMADA";
        this.ticket = this.new TicketDeAcceso();
    }
    
    public class TicketDeAcceso {
        private String idTicket;
        private LocalDate fechaEmision;

        // El constructor es privado/protegido dentro del contexto de Inscripcion
        public TicketDeAcceso() {
            this.fechaEmision = LocalDate.now();
            this.idTicket = "TICKET-" + estudiante.getLegajo();
        }

        public String getidTicket() {
            return idTicket;
        }

        public LocalDate getFechaEmision() {
            return fechaEmision;
        }

        public Estudiante getEstudiante() {
            // Acceso directo al atributo de la clase contenedora (Inscripcion)
            return estudiante;
        }

        public void enviarTicket() {
            // Accede al estudiante de la clase contenedora (Inscripcion)
            System.out.println("[HILO] Enviando ticket " + idTicket + " a " + estudiante.getNombre() + " (Emitido: " + fechaEmision + ")");
        }
    }
}
