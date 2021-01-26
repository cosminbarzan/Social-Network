package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    /**
     * incarca continutul fisierului in memorie
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sau cu lambda - curs 4, sem 4 si 5
//        Path path = Paths.get(fileName);
//        try {
//            List<String> lines = Files.readAllLines(path);
//            lines.forEach(linie -> {
//                E entity=extractEntity(Arrays.asList(linie.split(";")));
//                super.save(entity);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    /**
     *
     * @param entity
     * @return entitatea intr-un format de string
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * salveaza o entitate in memorie si apoi o scrie in fisier
     * @param entity
     * @return - null daca entitatea a fost salvata
     *         - entitatea care exista deja, altfel
     */
    @Override
    public E save(E entity){
        E e=super.save(entity);
        if (e==null)
        {
            writeToFile(entity);
        }
        return e;

    }

    /**
     * sterge o entitate din memorie si rescrie continutul fisierului
     * @param id
     * @return - null, daca nu exista
     *         - entitatea stearsa, altfel
     */
    @Override
    public E delete(ID id) {
        E entity = super.delete(id);
        reloadFile();
        return entity;
    }

    /**
     * scrie la sfarsitul fisierul entitatea specificata
     * @param entity
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * reincarca datele din memorie in fisier
     */
    protected void reloadFile()
    {
        try (BufferedWriter bW = new BufferedWriter (new FileWriter(fileName, false))){
            for (E entity : findAll())
            {
                bW.write(createEntityAsString(entity));
                bW.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

