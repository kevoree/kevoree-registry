package org.kevoree.registry.server.service;

import org.kevoree.registry.server.dao.NamespaceDAO;
import org.kevoree.registry.server.dao.UserDAO;
import org.kevoree.registry.server.exception.NotAvailableException;
import org.kevoree.registry.server.exception.NotValidException;
import org.kevoree.registry.server.exception.PasswordException;
import org.kevoree.registry.server.model.Namespace;
import org.kevoree.registry.server.model.User;
import org.kevoree.registry.server.util.Password;
import org.kevoree.registry.server.util.PasswordHash;

import javax.persistence.EntityManagerFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 * Created by leiko on 10/12/14.
 */
public class UserService {

    private static UserService INSTANCE;

    private final UserDAO userDAO;
    private final EntityManagerFactory emf;

    private UserService(EntityManagerFactory emf) {
        this.emf = emf;
        userDAO = UserDAO.getInstance(emf);
    }

    public static UserService getInstance(EntityManagerFactory emf) {
        if (UserService.INSTANCE == null) {
            UserService.INSTANCE = new UserService(emf);
        }

        return UserService.INSTANCE;
    }

    public void signin(String id, String name, String password)
            throws NotAvailableException, PasswordException, InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userDAO.get(id);
        if (user == null) {
            // this user id is available
            user = new User();
            user.setId(id);
            user.setGravatarEmail(id);
            user.setName(name);

            // hash password
            Password hashedPassword = PasswordHash.createHash(password);
            user.setSalt(hashedPassword.getSalt());
            user.setPassword(hashedPassword.getHash());
            // save it in db
            userDAO.add(user);

        } else {
            // error: user id already exists in db
            throw new NotAvailableException("This email address is already associated with an account");
        }
    }

    public void login(String id, String password)
            throws NotAvailableException, NotValidException, PasswordException, InvalidKeySpecException, NoSuchAlgorithmException {
        User user = userDAO.get(id);
        if (user == null) {
            throw new NotAvailableException("Email address \"" + id + "\" unknown");
        } else {
            // email address is available in db: check for password match
            if (user.getSalt() == null) {
                // seems like this email has been saved using OAuth so there is no password
                throw new NotValidException("It seems like this email address has been registered using OpenID, retry connection using OpenID SignIn service and edit your password in your profile.");

            } else {
                Password hashedPassword = new Password(PasswordHash.PBKDF2_ITERATIONS, user.getPassword(), user.getSalt());
                if (PasswordHash.validatePassword(password, hashedPassword)) {
                    // valid authentication
                    // save user in session
                    userDAO.update(user);

                } else {
                    // wrong password
                    throw new PasswordException("Please enter a correct email and password");
                }
            }
        }
    }

    public void delete(User user) {
        user = userDAO.get(user.getId());
        for (Namespace ns : user.getNamespaces()) {
            if (ns.getOwner() != null && ns.getOwner().getId().equals(user.getId())) {
                // delete namespace which I own
                NamespaceService.getInstance(emf).delete(ns);
            } else {
                // just leave namespace
                user.removeNamespace(ns);
            }
        }
        user = userDAO.get(user.getId());
        userDAO.delete(user);
    }
}
