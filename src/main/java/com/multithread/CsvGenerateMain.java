package com.multithread;

import java.io.IOException;

public class CsvGenerateMain {
    public static void main(String[] args) throws IOException {
        new CsvExportThread().start();
    }
}
