package com.tdksoft.appointment.Appointment_storage;

import java.util.List;

public interface CustomTwo{
    public List<Appointment> findByDocId(String docId);
}