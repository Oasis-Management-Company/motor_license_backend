package com.app.IVAS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;

public class SequenceServiceImp implements SequenceService {
    private String sequenceName;


    @Autowired
    private JdbcTemplate jdbcTemplate;


    public SequenceServiceImp(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @PostConstruct
    public void init() {
        this.jdbcTemplate.execute(String.format("DO $$ BEGIN CREATE SEQUENCE %s; EXCEPTION WHEN duplicate_table THEN END $$ LANGUAGE plpgsql;", sequenceName));
    }

    @Override
    public Long getNextId() {
        return this.jdbcTemplate.queryForObject(String.format("select nextval ('%s')", sequenceName), Long.class);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
