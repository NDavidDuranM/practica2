package encapsulation;

import java.util.ArrayList;

public class CarroCompra {

    private String id;
    private ArrayList<Producto> listaProductos;

    public CarroCompra () {}

    public CarroCompra(String id) {
        this.id = id;
        this.listaProductos = new ArrayList<Producto>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void agregarProducto(Producto producto){
        listaProductos.add(producto);
    }

    public Producto buscarProducto(int id){
        Producto producto = null;
        for (Producto prod:listaProductos) {
            if(prod.getId() == id){
                producto = prod;
                break;
            }
        }
        return producto;
    }

    public void eliminarProducto(Producto producto){
        listaProductos.remove(producto);
    }
}
