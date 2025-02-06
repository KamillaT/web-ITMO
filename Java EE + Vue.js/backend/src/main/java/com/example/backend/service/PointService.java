package com.example.backend.service;

import com.example.backend.entity.Point;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import java.util.List;

import static com.example.backend.db.DBManager.em;


@Stateless
public class PointService {

    @Transactional
    public void savePoint(Point point) {
        em.persist(point);
    }

    public List<Point> findPointsByUserId(Long userId) {
        List<Point> allPoints = em.createQuery("SELECT p FROM Point p WHERE p.user.id = :userId", Point.class)
                .setParameter("userId", userId)
                .getResultList();
        return allPoints;
    }

    @Transactional
    public void deletePoints(Long userId) {
        em.createQuery("DELETE FROM Point p WHERE p.user.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
