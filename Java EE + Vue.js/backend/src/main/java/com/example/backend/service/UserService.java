package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.jwt.JwtUtil;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import static com.example.backend.db.DBManager.em;


@Stateless
public class UserService {

    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void createUser(User user) {
        em.persist(user);
    }
}
