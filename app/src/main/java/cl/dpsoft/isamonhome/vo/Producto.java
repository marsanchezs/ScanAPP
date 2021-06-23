package cl.dpsoft.isamonhome.vo;

public class Producto {
    private String idCaptura;
    private String codigo;
    private String descripcion;
    private String Sku;
    private String idprod;
    private String patente;
    private String ubicacion;
    private String cantidad;
    private int encontrado; //0 no encontrado 1 encontrado

    public Producto(){
    }

    public String getIdCaptura() {
        return idCaptura;
    }

    public void setIdCaptura(String idCaptura) {
        this.idCaptura = idCaptura;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSku() {
        return Sku;
    }

    public void setSku(String sku) {
        Sku = sku;
    }

    public String getIdprod() {
        return idprod;
    }

    public void setIdprod(String idprod) {
        this.idprod = idprod;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCantidad() {  return cantidad; }

    public void setCantidad(String cantidad) { this.cantidad = cantidad; }

    public int getEncontrado() { return encontrado;
    }

    public void setEncontrado(int encontrado) {
        this.encontrado = encontrado;
    }
}
