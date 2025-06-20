package com.tdksoft.appointment.Appointment_storage;

import java.util.List;

public interface CustomRepository{
    public List<Appointment> findAllByEmail(String email);
    //public List<Appointment> findAllForDoc(String docId);
}