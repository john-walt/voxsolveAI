package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findBySkillName(String skillName); // Fetch a skill by its name
}
