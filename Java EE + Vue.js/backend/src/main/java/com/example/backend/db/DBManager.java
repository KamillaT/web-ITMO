package com.example.backend.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBManager {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppPU");
    public static EntityManager em = emf.createEntityManager();
}
