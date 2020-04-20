package main;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/resourcesDr")
public class RestfulDrPatient extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(DrPatientsRS.class);
        return set;
    }
}
