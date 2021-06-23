package cl.dpsoft.isamonhome.vo;

public class Usuario {

    private boolean bExiste;
    private String sNombre;
    private String rut;

    public Usuario(){ }

    public boolean isbExiste() {
        return bExiste;
    }

    public void setbExiste(boolean bExiste) {
        this.bExiste = bExiste;
    }

    public String getsNombre() {
        return sNombre;
    }

    public void setsNombre(String sNombre) {
        this.sNombre = sNombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }
}
