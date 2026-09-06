package backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Formatter {
    // CRIA O FORMATADOR
    private ObjectMapper mapper = new ObjectMapper();

    // RETORNA OBJETO ÚNICO
    public <T> T getObjeto(String json, Class<T> classe){
        try {
            ArrayList<T> lista = mapper.readValue(json, mapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, classe));
            return lista.getFirst();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // RETORNA ARRAYLIST DE OBJETOS
    public <T> ArrayList<T> getArrayObjetos(String json, Class<T> classe){
        try {
            ArrayList<T> lista = mapper.readValue(json, mapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, classe));
            return lista;
        } catch (MismatchedInputException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
