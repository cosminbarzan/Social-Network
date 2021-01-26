package socialnetwork.repository.file;


import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long, Long>, Friendship>{
    public FriendshipFile (String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    /**
     * creeaza o prietenie pe baza atributelor din lista
     * @param attributes - lista de atribute
     * @return prietenia
     */
    @Override
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1)), LocalDateTime.parse(attributes.get(2)));
        friendship.setId(new Tuple<>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));
        return friendship;
    }

    /**
     *
     * @param entity - o prietenie
     * @return - prietenia intr-un format de string
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight();
    }
}
