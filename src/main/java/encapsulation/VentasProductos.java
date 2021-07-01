package encapsulation;

import java.util.ArrayList;

public class VentasProductos {

    private String id;
    private String fechaCompra;
    private String nombreCliente;
    private ArrayList<Producto> listaProductos;
    private float totalVenta;

    public VentasProductos () {}

    public VentasProductos(String id, String fechaCompra, String nombreCliente, ArrayList<Producto> listaProductos) {
        this.id = id;
        this.fechaCompra = fechaCompra;
        this.nombreCliente = nombreCliente;
        this.listaProductos = listaProductos;
        this.totalVenta = calcularTotalVenta();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public float getTotal() {
        return totalVenta;
    }

    public void setTotal(float total) {
        this.totalVenta = total;
    }

    private float calcularTotalVenta(){
        float sum=0;
        for (Producto p: listaProductos) {
            sum += p.getPrecio();
        }
        return sum;
    }
}
