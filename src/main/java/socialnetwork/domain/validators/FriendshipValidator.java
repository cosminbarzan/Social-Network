package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String message = new String();

        if(entity.getId().getLeft().equals(entity.getId().getRight())) {
            message += "Nu se poate crea o prietenie cu utilizatorul insusi!\n";
        }

        if( ! message.isEmpty())
            throw new ValidationException(message);
    }
}
