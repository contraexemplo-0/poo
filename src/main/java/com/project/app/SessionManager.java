package com.project.app;

import com.project.model.Patient;

public class SessionManager {

    private static Patient currentPatient;

    public static void setCurrentPatient(Patient patient) {
        currentPatient = patient;
    }

    public static Patient getCurrentPatient() {
        return currentPatient;
    }

    public static boolean isLoggedIn() {
        return currentPatient != null;
    }

    public static void logout() {
        currentPatient = null;
    }
}
