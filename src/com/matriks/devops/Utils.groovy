package com.matriks.devops

class Utils {
    static String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "-")
    }

    static String getTimestamp() {
        return new Date().format("yyyyMMdd-HHmmss")
    }
}

