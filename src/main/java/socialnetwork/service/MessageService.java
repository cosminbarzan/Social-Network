package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;
import socialnetwork.utils.MessageE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageService {
    private Repository<Long, Message> repoMessage;
    private Repository<Long, User> repoUser;
    private Repository<Tuple<Long, Long>, Friendship> repoFriend;

    public MessageService(Repository<Long, Message> repoMessage, Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriend) {
        this.repoMessage = repoMessage;
        this.repoUser = repoUser;
        this.repoFriend = repoFriend;
    }

    public Message save(Message message) {
        return repoMessage.save(message);
    }

    public Message getOne(Long id) {
        return repoMessage.findOne(id);
    }

    public void showConversation(Long idFrom, Long idTo) {
        User from = repoUser.findOne(idFrom);
        User to = repoUser.findOne(idTo);

        List<Message> messages = new ArrayList<>();

        List<Message> allMessages = new ArrayList<>();
        repoMessage.findAll().forEach(message -> {
            List<User> toList = message.getTo();
            if (message.getFrom().getId().equals(idFrom) && toList.get(0).getId().equals(idTo)) {
                messages.add(message);
            } else {
                if (message.getFrom().getId().equals(idTo) && toList.get(0).getId().equals(idFrom)) {
                    messages.add(message);
                }
            }
        });

        Collections.sort(messages, Comparator.comparing(message -> message.getDate()));

        for (Message message : messages) {
            if (message.getFrom().getId().equals(idFrom)) {
                System.out.print("MessageId=" + message.getId() + ": " + idFrom + " | " + from.getFirstName() + " " + from.getLastName() + " | " + message.getMessage() + " | " + message.getDate());
                if (message.getReply() != null) {
                    System.out.println(" | " + "reply to: " + message.getReply().getMessage());
                } else {
                    System.out.println();
                }
            } else {
                System.out.print("MessageId=" + message.getId() + ": " + idTo + " | " + to.getFirstName() + " " + to.getLastName() + " | " + message.getMessage() + " | " + message.getDate());
                if (message.getReply() != null) {
                    System.out.println(" | " + "reply to: " + message.getReply().getMessage());
                } else {
                    System.out.println();
                }
            }
        }
    }

    public List<MessageE> toMessageE(List<Message> messages, Long idFrom, Long idTo) {
        User to = repoUser.findOne(idTo);

        Collections.sort(messages, Comparator.comparing(message -> message.getDate()));

        List<MessageE> messageEList = new ArrayList<>();

        for (Message message : messages) {
            if (message.getFrom().getId().equals(idFrom)) {
                MessageE messageE = new MessageE(message.getId(), "You", to.getFirstName() + to.getLastName(),
                        message.getMessage(), message.getDate(), null);

                if (message.getReply() != null) {
                    messageE.setReply(message.getReply().getMessage());
                }

                messageEList.add(messageE);
            } else {
                MessageE messageE = new MessageE(message.getId(), to.getFirstName() + " " + to.getLastName(),
                        "You", message.getMessage(), message.getDate(), null);

                if (message.getReply() != null) {
                    messageE.setReply(message.getReply().getMessage());
                }

                messageEList.add(messageE);
            }
        }
        return messageEList;
    }

    public List<MessageE> anotherUserConv(Long idFrom, Long idTo) {
        User from = repoUser.findOne(idFrom);
        User to = repoUser.findOne(idTo);

        List<Message> messages = new ArrayList<>();

        List<Message> allMessages = new ArrayList<>();
        repoMessage.findAll().forEach(message -> {
            List<User> toList = message.getTo();
            if (message.getFrom().getId().equals(idFrom) && toList.get(0).getId().equals(idTo)) {
                messages.add(message);
            } else {
                if (message.getFrom().getId().equals(idTo) && toList.get(0).getId().equals(idFrom)) {
                    messages.add(message);
                }
            }
        });

        return toMessageE(messages, idFrom, idTo);
    }

    public List<Message> messagesFromAPeriod(Long idTo, LocalDate dateStart, LocalDate dateEnd) {
        List<Message> messages = new ArrayList<>();

        repoMessage.findAll().forEach(message -> {
            List<User> toList = message.getTo();
            if (toList.get(0).getId().equals(idTo) && message.getDate().toLocalDate().compareTo(dateStart) >= 0
                    && message.getDate().toLocalDate().compareTo(dateEnd) <= 0) {
                messages.add(message);
            }
        });
        return messages;
    }

    public List<Message> messagesFromADay(Long idTo, LocalDate dateStart, LocalDate dateEnd, LocalDate date) {
        List<Message> messages = messagesFromAPeriod(idTo, dateStart, dateEnd);
        List<Message> messagesDay = new ArrayList<>();

        messages.stream()
                .filter(x-> x.getDate().toLocalDate().equals(date))
                .forEach(messagesDay::add);

        if(messagesDay.size() > 0)
            return messagesDay;
        return null;
    }

}
