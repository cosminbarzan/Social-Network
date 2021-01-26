package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User>{

    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /**
     * creeaza un user pe baza atributelor din lista
     * @param attributes - lista de atribute
     * @return userul
     */
    @Override
    public User extractEntity(List<String> attributes) {
        //TODO: implement method
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /**
     *
     * @param entity - un user
     * @return userul intr-un format de string
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
