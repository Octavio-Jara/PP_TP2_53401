package modelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.actividades.Taller;
    /**
     * EventoUniversitario compone una o más actividades.
     * También agrega una sala, que puede existir independientemente del evento.
     */
    public class EventoUniversitario implements Serializable{
        private final String id;
        private String titulo;
        private double costoBase;
        private boolean gratuito;

        /* Clases relacionadas  */
        private Sala sala;
        private List <Actividad> actividades;

        /* Variables de clase */
        private static int cantidadEventos;

        /* Inicializador estático */
        static {
            cantidadEventos = 0;
            System.out.println("Inicializador estático: se cargó la clase EventoUniversitario.");
        }

        public EventoUniversitario(String id, String nombre,  double costo, boolean esGratuito) {
            this.id = id;
            setTitulo(nombre); //se usa setTitulo en lugar de asignación directa porque hay validación de que no sea nulo.
            this.gratuito = esGratuito;
            this.costoBase = gratuito ? 0 : costo;        cantidadEventos++;

            /* Aquí notar como se implementa la relación de composición como una relación estructural del tipo Todo-Parte fuerte .
            * Si se destruye el evento se destruirán también sus actividades.
            * Es decir, la vida útil de cada actividad está fuertemente ligada a la vida útil del evento. */
            this.actividades = new ArrayList<>();
        }

        public EventoUniversitario(EventoUniversitario otroEvento) {
            this(
                    otroEvento.id + "-COPIA",
                    otroEvento.titulo,
                    otroEvento.costoBase,
                    otroEvento.gratuito
            );
        }

        public String getId() {
            return id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String nombre) {
            if (nombre != null && !nombre.isBlank())
                this.titulo = nombre;
        }
    

        public double calcularCostoEstimado() {
            if (this.gratuito){
                return 0;
            }
            double sumarCostoActividades = 0;
            for (Actividad actividad : actividades){
                sumarCostoActividades += actividad.calcularCostoMateriales();
            }
            return (this.costoBase + sumarCostoActividades) * 1.21; // 21% de impuestos
        }

        public Sala getSala() {
            return sala;
        }

        /* Implementa la agregación dinámica. Un evento se realiza en una sala, pero la relación Todo-Parte es débil.
        * Si el evento no se realiza y el objeto que lo representa se destruye, la sala sigue existiendo y puede asignarse a otro evento. */
        public void asignarSala(Sala sala) {
                this.sala = sala;
        }

        /**
         * Representa la composición: la actividad se crea para el evento y queda contenida por él.
         * La relación Todo-Parte es fuerte: si el evento se destruye, las actividades también se destruyen.
         */
    public void crearActividad(int id, String titulo, int cupo, String  tipoActividad) {

        Scanner scanner = new Scanner(System.in);

        switch (tipoActividad) {
            case "charla":
                System.out.print("Ingrese el nombre del disertante para la charla " + titulo + " :  ");
                String disertante = scanner.nextLine();
                Actividad charla = new Charla(id, titulo,cupo, disertante);
                this.actividades.add(charla);
            break;
            case "taller":
                System.out.print("El taller " + titulo + " requiere el uso de Notebook? : S/N  ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                boolean requiereNotebook = false;
                if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                    requiereNotebook = true;
                }
                Actividad taller = new Taller(id, titulo,cupo,requiereNotebook);
                this.actividades.add(taller);
            break;
            case "curso":
                System.out.print("De que nivel es el curso " + titulo  + "?");
                int nivel = scanner.nextInt();
                Actividad curso = new Curso(id, titulo,cupo,nivel);
                this.actividades.add(curso);
            break;
            default:
                System.out.println("Error: Tipo de actividad no reconocido.");
        }
    }
        public List<Actividad>  getActividades() {
            /* Se retorna una lista inmodificable para que mantener el encapsulamiento logrado con la composición
            * y que no puedan agregar actividades desde afuera. */
            return Collections.unmodifiableList(actividades);
        }

        public void  mostrarDatos() {
            System.out.println("===================================================================================");
            System.out.println("Evento codigo=" + id);
            System.out.println("TÍtulo=" + titulo);
            System.out.println("Costo=" + this.calcularCostoEstimado());
            System.out.println("Sala asignada: " + (sala != null ? sala.getNombre() : "Sin sala")+"\n");
            System.out.println("Actividades:");
            System.out.println("____________");
            for (Actividad actividad : actividades) {
                System.out.println("- " + actividad.getTitulo() + " (id=" + actividad.getId() + ")" + " - Cupo máximo: " + actividad.getCupoMaximo());
                actividad.mostrarInscripciones();
            }
            System.out.println("=====================================================================================");
        }

        public static int getCantidadEventos() {
            return cantidadEventos;
        }

        
    public boolean persistirEvento() throws IOException {
        String nombreArchivo = "evento_" + this.id + ".dat";
        try (ObjectOutputStream oos =  //Se usa un patron try-with-resources para garantizar que se cierren los recursos aun si se produjera una excepcion.
                     new ObjectOutputStream(
                             new FileOutputStream(nombreArchivo))) {

            oos.writeObject(this);
            return true;
        }    }

    public EventoUniversitario recuperarEvento(String id)  throws IOException, ClassNotFoundException {

        String nombreArchivo = "evento_" + id + ".dat";

        try (ObjectInputStream ois =  //Se usa un patron try-with-resources para devolver el objeto recuperado
                     new ObjectInputStream(
                             new FileInputStream(nombreArchivo))) {

            return (EventoUniversitario) ois.readObject();
        }
    }

    public <T extends Actividad> List<T> filtrarActividadesPorTipo(Class<T> tipo){
        List<T> resultado = new ArrayList<>();
        for (Actividad actividad : actividades) {
            if (tipo.isInstance(actividad)) {
                resultado.add(tipo.cast(actividad));
            }
        }
        return resultado;
    }
    public double calcularCostoMateriales(List<? extends Actividad> actividades){
        double costoTotal = 0;
        for (Actividad actividad : actividades) {
            costoTotal += actividad.calcularCostoMateriales();
        }
        return costoTotal;
    }

    }
