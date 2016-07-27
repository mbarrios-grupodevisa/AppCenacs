package gt.com.metrocasas.appcenacs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class  Elemento {

    private String id = "";
    private String proyecto = "";
    private String clasficacion = "";
    private String elemento = "";
    private boolean estado = false;
    private String comentario = "";
    private String imagen = "";
    private String imagePath = "";

    public Elemento(String id, String proyecto, String clasficacion, String elemento) {
        this.id = id;
        this.proyecto = proyecto;
        this.clasficacion = clasficacion;
        this.elemento = elemento;
    }

    public int isEstado() {
        if (estado) return 1;
        return 0;
    }
}
