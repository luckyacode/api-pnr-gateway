package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Integer> {
}
