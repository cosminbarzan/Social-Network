package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String message = new String();

        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            message += "Utilizatorii nu pot avea numele vid!\n";

        if (entity.getFirstName().length() < 3 || entity.getLastName().length() < 3)
            message += "Utilizatorii nu pot avea nume formate din mai putin de 3 litere!\n";

        if ( ! entity.getFirstName().chars().allMatch(Character::isLetter) ||
             ! entity.getLastName().chars().allMatch(Character::isLetter))
            message += "Utilizatorii nu pot avea nume formate decat din litere!\n";

        if ( ! message.isEmpty())
            throw new ValidationException(message);
    }
}
