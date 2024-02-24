package dev.eviez.helloworld;


import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloWorldRepository extends JpaRepository<HelloWorldEntity, Long> {
    boolean existsByEmail(String email);
    HelloWorldEntity findByEmail(String email);
}