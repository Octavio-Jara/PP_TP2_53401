package modelo.actividades;

import modelo.Estudiante;
import modelo.certificacion.Certificable;

public class Curso extends Actividad implements Certificable{
    private int nivel;
    public Curso(int id, String titulo, int cupo,int nivel){
        super(id,titulo,cupo);
        this.nivel = nivel;    
    }
    @Override public String getTipo() {
        return this.getClass().getSimpleName();
    }
    @Override public double calcularCostoMateriales() {
        return 0;
    }
    @Override
    public String generarCertificado(Estudiante estudiante) {
        return "Certificado de asistencia al curso de " + this.titulo + " emitido para: " + estudiante.getNombre();
    }
}
