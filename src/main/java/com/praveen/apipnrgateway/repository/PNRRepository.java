package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.PNR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PNRRepository extends JpaRepository<PNR,Integer> {
}
