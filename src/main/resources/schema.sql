-- ==================================================
-- Car Expense Tracking System - Oracle Database Schema
-- ==================================================
-- Run this script in Oracle SQL*Plus or SQL Developer
-- before starting the application.
-- ==================================================

-- Create sequence for auto-incrementing primary key
CREATE SEQUENCE CAR_EXPENSES_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create the main expenses table
CREATE TABLE CAR_EXPENSES (
    EXPENSE_ID   NUMBER(10)     NOT NULL,
    EXPENSE_DATE DATE           NOT NULL,
    CATEGORY     VARCHAR2(100)  NOT NULL,
    AMOUNT       NUMBER(12, 2)  NOT NULL,
    DESCRIPTION  VARCHAR2(500),
    CONSTRAINT PK_CAR_EXPENSES PRIMARY KEY (EXPENSE_ID)
);

-- Create trigger to auto-populate EXPENSE_ID from sequence
CREATE OR REPLACE TRIGGER CAR_EXPENSES_BI
    BEFORE INSERT ON CAR_EXPENSES
    FOR EACH ROW
BEGIN
    IF :NEW.EXPENSE_ID IS NULL THEN
        SELECT CAR_EXPENSES_SEQ.NEXTVAL INTO :NEW.EXPENSE_ID FROM DUAL;
    END IF;
END;
/

-- Create indexes for performance
CREATE INDEX IDX_CAR_EXP_DATE     ON CAR_EXPENSES (EXPENSE_DATE);
CREATE INDEX IDX_CAR_EXP_CATEGORY ON CAR_EXPENSES (CATEGORY);

-- Grant permissions (update YOUR_USER to the actual schema user if needed)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON CAR_EXPENSES TO YOUR_USER;

-- Verify table structure
-- SELECT column_name, data_type, data_length FROM user_tab_columns WHERE table_name = 'CAR_EXPENSES';
