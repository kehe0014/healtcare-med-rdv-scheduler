package com.tdksoft.appointment.Appointment_storage;

import org.springframework.data.repository.CrudRepository;

public interface AppointmentRepository extends CrudRepository<Appointment,String>,CustomRepository,CustomTwo{

	
	
}
