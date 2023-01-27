public class pruebas {
    public static void main(String[] args) {
        String uri = "hola";
        uri = uri.substring(1);
        int position = uri.indexOf("/") + 1;
        uri = uri.substring(position);
        //buscar en objects
        //Si existe es objeto y se dirige a la página de objeto
        //Si no es carpeta y hay que mostrar su contenido.
        position = uri.indexOf("/") + 1;
        uri = uri.substring(position);
    }
}
