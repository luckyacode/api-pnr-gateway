package com.praveen.apipnrgateway.repository;

import com.praveen.apipnrgateway.entity.DlqTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DlqTopicRepository extends JpaRepository<DlqTopic,Integer> {
}
