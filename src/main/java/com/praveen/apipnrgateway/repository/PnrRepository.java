package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.PNR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PnrRepository extends JpaRepository<PNR,Integer> {
    Optional<PNR> findByPnrId(String pnrId);
    List<PNR> findAllByBookingStatus(String bookingStatus);
}
